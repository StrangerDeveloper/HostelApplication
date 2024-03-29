
package com.example.hostelrecommendationsystem.user.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.bikcrum.locationupdate.LocationUpdate;
import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.activity.CredentialActivity;
import com.example.hostelrecommendationsystem.admin.model.Hostel;
import com.example.hostelrecommendationsystem.utils.AppConstant;
import com.example.hostelrecommendationsystem.utils.sharedPref.SharedPrefHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity implements OnMapReadyCallback, LocationUpdate.OnLocationUpdatedListener {

    LocationUpdate mLocationUpdate;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        initView();


        mLocationUpdate = LocationUpdate.getInstance(this);
        mLocationUpdate.onCreate(savedInstanceState);

        mLocationUpdate.setLocationUpdateIntervalInMilliseconds(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationUpdate.setLocationFastestUpdateIntervalInMilliseconds(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);


    }

    private String getCityFromLocation(LatLng myCurrentLatLng) {

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(myCurrentLatLng.latitude, myCurrentLatLng.longitude, 1);
            Address address = addressList.get(0);

            return address.getLocality();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

    private SearchView mSearchView;
    private ImageView imgFilter;

    private void initView() {

        mSearchView = findViewById(R.id.search_map);
        if (SharedPrefHelper.getmHelper().getFilterData() != null) {
            mSearchView.setQueryHint(SharedPrefHelper.getmHelper().getFilterData());
        }
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String query = mSearchView.getQuery().toString();
                if (SharedPrefHelper.getmHelper().getFilterData().equalsIgnoreCase(getString(R.string.search_for_hostel_by_city)))
                    searchHostelOnMap(query);
                else
                    searchHostelByName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        imgFilter = findViewById(R.id.img_filter);
        imgFilter.setOnClickListener(v -> {
            showFilterDialog();
        });


    }


    private Dialog mDialog;
    private RadioButton rbGeneral;

    private void showFilterDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(this, R.style.DialogTheme);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            //mDialog.getWindow().setBackgroundDrawable();

            mDialog.setContentView(R.layout.dialog_filter_hostel);
            mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            RadioGroup rdgFilter = mDialog.findViewById(R.id.rg_filter);
            RadioButton rbCity = mDialog.findViewById(R.id.rdb_by_city);
            RadioButton rbName = mDialog.findViewById(R.id.rdb_by_name);
            Button btnApplyFilter = mDialog.findViewById(R.id.btn_apply_filter);

            if (SharedPrefHelper.getmHelper().getFilterData().equalsIgnoreCase(getString(R.string.search_for_hostel_by_city)))
                rbCity.setChecked(true);
            else
                rbName.setChecked(true);

            btnApplyFilter.setOnClickListener(v -> {
                int selectedID = rdgFilter.getCheckedRadioButtonId();
                rbGeneral = mDialog.findViewById(selectedID);
                SharedPrefHelper.getmHelper().setFilterData(rbGeneral.getText().toString());
                mSearchView.setQueryHint(rbGeneral.getText());
                mDialog.dismiss();
            });

            TextView txtClose = mDialog.findViewById(R.id.txt_close);
            txtClose.setOnClickListener(v -> {
                mDialog.dismiss();
                mDialog = null;
            });


            mDialog.show();

        } else {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void searchHostelOnMap(String query) {

        Geocoder geocoder = new Geocoder(this);
        try {
            mGoogleMap.clear();
            List<Address> addressList = geocoder.getFromLocationName(query, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                //mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(query));
//            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                Log.e("city", address.getLocality());

                getHostelByCity(address.getLocality());
            } else
                Toast.makeText(this, "Please change Filter if you wants to search by Name", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void getHostelByCity(String city) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference(AppConstant.HOSTEL);
        dbRef.orderByChild("city").equalTo(city)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Hostel hostel = snapshot.getValue(Hostel.class);

                                String[] value = hostel.getLocation().split(",");
                                double lat = Double.parseDouble(value[0]);
                                double lng = Double.parseDouble(value[1]);
                                LatLng hostelLatLang = new LatLng(lat, lng);
                                MarkerOptions markerOptions = new MarkerOptions().position(hostelLatLang).title(hostel.getHostelName());
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hostel));

                                mGoogleMap.addMarker(markerOptions);

                                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(saloonLatLang));
                                // mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(saloonLatLang, 15F));

                            }
                            // Log.d("ListSaloon", listModel.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchHostelByName(String hostelName) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference(AppConstant.HOSTEL);
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Hostel hostel = snapshot.getValue(Hostel.class);

                        if (hostel != null && hostel.getHostelName().toLowerCase().startsWith(hostelName.toLowerCase())) {
                            //Log.d("TAG", hostel.toString());
                            String[] value = hostel.getLocation().split(",");
                            double lat = Double.parseDouble(value[0]);
                            double lng = Double.parseDouble(value[1]);
                            LatLng hostelLatLang = new LatLng(lat, lng);
                            MarkerOptions markerOptions = new MarkerOptions().position(hostelLatLang).title(hostel.getHostelName());
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hostel));

                            mGoogleMap.addMarker(markerOptions);

                            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(saloonLatLang));

                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15F));
                        }

                    }
                    // Log.d("ListSaloon", listModel.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private GoogleMap mGoogleMap;
    private LatLng myCurrentLatLng;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        LatLngBounds abbottabadBounds = new LatLngBounds(
                new LatLng(34.124487, 73.190961), // SW bounds
                new LatLng(34.228272, 73.245651)  // NE bounds
        );

        LatLngBounds kpBounds = new LatLngBounds(
                new LatLng(31.343979, 70.417799), // SW bounds
                new LatLng(36.747357, 73.864252)  // NE bounds
        );


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kpBounds.getCenter(), 10));

        if (myCurrentLatLng != null) {
            setLocationOnMap(myCurrentLatLng);
        }
    }

    private void setLocationOnMap(LatLng latLng) {

        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Me"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //don't forget to call this
        mLocationUpdate.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationUpdated(Location mCurrentLocation, String mLastUpdateTime) {
        /*Log.v(TAG, "onLocationUpdated is called");
        long lat = mCurrentLocation.getLatitude();
        long lng = mCurrentLocation.getLongitude();*/
        myCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        //by default only for abbottbad city
        getHostelByCity(getCityFromLocation(myCurrentLatLng));

    }

    @Override
    protected void onDestroy() {
        //don't forget to call this
        mLocationUpdate.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLocationUpdate.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_log_out) {
            startActivity(new Intent(this, CredentialActivity.class));
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}