package com.example.hostelrecommendationsystem.admin.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.admin.model.Hostel;
import com.example.hostelrecommendationsystem.utils.AppConstant;
import com.example.hostelrecommendationsystem.utils.InputValidator;
import com.example.hostelrecommendationsystem.utils.UtilClass;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gun0912.tedbottompicker.TedBottomPicker;

public class AddHostelFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = AddHostelFragment.class.getSimpleName();
    private static final int REQUEST_PLACE_PICKER = 1010;

    public AddHostelFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_hostel, container, false);
    }

    private AppCompatActivity mActivity;
    private TextView txtLocationPicker;
    private TextInputEditText etName, etHostelPhone, etHostelRooms, etPerRoomPrice;
    private TextInputLayout inputLayoutName, inputLayoutPhone, inputLayoutHostelRooms, inputLayoutPerRoomPrice;

    private Button btnAddHostel;
    private ImageView imgHostel;
    // private VanillaAddress mHostelPickedAddress;

    private InputValidator mInputValidator;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();

        initViews(view);
        onViewsClick();
    }


    private void initViews(View view) {

        roomTypeList = new ArrayList<>();
        facilitiesList = new ArrayList<>();

        btnAddHostel = view.findViewById(R.id.btn_add_hostel);
        imgHostel = view.findViewById(R.id.img_add_hostel);
        txtLocationPicker = view.findViewById(R.id.txt_pick_up_location_address);

        etName = view.findViewById(R.id.et_name);
        etHostelPhone = view.findViewById(R.id.et_phone);
        etHostelRooms = view.findViewById(R.id.et_rooms);
        etPerRoomPrice = view.findViewById(R.id.et_price);

        inputLayoutHostelRooms = view.findViewById(R.id.text_input_layout_rooms);
        inputLayoutName = view.findViewById(R.id.text_input_layout_name);
        inputLayoutPhone = view.findViewById(R.id.text_input_layout_phone);
        inputLayoutPerRoomPrice = view.findViewById(R.id.text_input_layout_price);

        CheckBox chbSingleSeater = view.findViewById(R.id.checkBox_single);
        CheckBox chbDoubleSeater = view.findViewById(R.id.checkBox_double);
        CheckBox chbTripleSeater = view.findViewById(R.id.checkBox_triple);
        CheckBox chbTetraSeater = view.findViewById(R.id.checkBox_tetra);

        chbSingleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbDoubleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbTripleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbTetraSeater.setOnCheckedChangeListener(this::onCheckedChanged);

        CheckBox chbGas = view.findViewById(R.id.checkBox_gas);
        CheckBox chbWifi = view.findViewById(R.id.checkBox_wifi);
        CheckBox chbElectricity = view.findViewById(R.id.checkBox_electricity);
        CheckBox chbWarmWater = view.findViewById(R.id.checkBox_hot_water);
        CheckBox chbFilterWater = view.findViewById(R.id.checkBox_filter_water);
        CheckBox chbHasMess = view.findViewById(R.id.checkBox_has_mess);


        chbGas.setOnCheckedChangeListener(this::onCheckedChanged);
        chbWifi.setOnCheckedChangeListener(this::onCheckedChanged);
        chbElectricity.setOnCheckedChangeListener(this::onCheckedChanged);
        chbWarmWater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbFilterWater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbHasMess.setOnCheckedChangeListener(this::onCheckedChanged);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.getId() == R.id.checkBox_single ||
                compoundButton.getId() == R.id.checkBox_double ||
                compoundButton.getId() == R.id.checkBox_triple ||
                compoundButton.getId() == R.id.checkBox_tetra) {
            if (isChecked) {
                roomTypeList.add(compoundButton.getText().toString());
            } else {
                roomTypeList.remove(compoundButton.getText().toString());
            }
        }
        if (compoundButton.getId() == R.id.checkBox_gas ||
                compoundButton.getId() == R.id.checkBox_wifi ||
                compoundButton.getId() == R.id.checkBox_electricity ||
                compoundButton.getId() == R.id.checkBox_hot_water ||
                compoundButton.getId() == R.id.checkBox_filter_water ||
                compoundButton.getId() == R.id.checkBox_has_mess) {
            if (isChecked) {
                facilitiesList.add(compoundButton.getText().toString());
            } else {
                facilitiesList.remove(compoundButton.getText().toString());
            }
        }
    }

    List<String> roomTypeList, facilitiesList;


    private List<Uri> mSelectedUriList;
    private StorageReference imagesUploadRef;
    private UploadTask mUploadTask;
    private List<Hostel.HostelImages> imagesUrlList;

    private void onViewsClick() {
        mSelectedUriList = new ArrayList<>();
        imagesUrlList = new ArrayList<>();
        mInputValidator = new InputValidator(mActivity);

        if (checkPermissions()) {
            imgHostel.setOnClickListener(v -> {
                TedBottomPicker.with(mActivity)
                        .setPeekHeight(mActivity.getResources().getDisplayMetrics().heightPixels / 2)
                        .showTitle(true)
                        .setCompleteButtonText("Done")
                        .setEmptySelectionText("No Select")
                        .setSelectedUriList(mSelectedUriList)
                        .showMultiImage(uriList -> {
                            // here is selected image uri list
                            mSelectedUriList.addAll(uriList);
                            Glide.with(mActivity).load(uriList.get(0)).into(imgHostel);

                        });
            });
        }
        txtLocationPicker.setOnClickListener(v -> {
            Intent intent = new PlacePicker.IntentBuilder()
                    .setLatLong(34.14685, 73.21449)  // Initial Latitude and Longitude the Map will load into
                    .showLatLong(true)  // Show Coordinates in the Activity
                    .setMapZoom(13.0f)  // Map Zoom Level. Default: 14.0
                    .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                    .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                    //.setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
                    .setMarkerImageImageColor(R.color.purple_500)
                    .setFabColor(R.color.purple_500)
                    .setPrimaryTextColor(R.color.colorWhite) // Change text color of Shortened Address
                    .setSecondaryTextColor(R.color.colorWhite) // Change text color of full Address
                    .setBottomViewColor(R.color.purple_500) // Change Address View Background Color (Default: White)
                    // .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
                    .setMapType(MapType.NORMAL)
                    .setPlaceSearchBar(true, getString(R.string.map_api_key)) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
                    .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                    .hideLocationButton(true)   //Hide Location Button (Default: false)
                    .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false)
                    .build(mActivity);

            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
        });

        btnAddHostel.setOnClickListener(view -> {
            /* validation goes here */
            if (mInputValidator.isInputEditTextFilled(etName, inputLayoutName, "Name is required!")
                    && mInputValidator.isInputEditTextFilled(etHostelPhone, inputLayoutPhone, "Phone is required")
                    && mInputValidator.isInputEditTextFilled(etHostelRooms, inputLayoutHostelRooms, "Room must not be empty!")
                    && mInputValidator.isInputEditTextFilled(etPerRoomPrice, inputLayoutPerRoomPrice, "Price is Required!")) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(mActivity, "uploads is in progress....", Toast.LENGTH_SHORT).show();
                } else {
                    if (mSelectedUriList.size() > 0) {
                        if (mHostelPickedAddress != null)
                            uploadImagesToStorage();
                        else
                            Toast.makeText(mActivity, "Plz choose Address First!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(mActivity, "Plz select at least one image!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mActivity, "Allow Permissions", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    private void uploadImagesToStorage() {
        final ProgressDialog progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("Images Uploading...");
        progressDialog.show();
        // imagesUploadRef = FirebaseStorage.getInstance();
        for (Uri uri : mSelectedUriList) {
            // Uri file = Uri.fromFile(new File(uri.getLastPathSegment()));
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference("HostelImages")
                    .child(new File(uri.getLastPathSegment()).getName());

            mUploadTask = storageReference.putFile(uri);
            Task<Uri> urlTask = mUploadTask.continueWithTask(task -> {
                if (!task.isSuccessful())
                    throw task.getException();
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    progressDialog.dismiss();
                    imagesUrlList.add(new Hostel.HostelImages(downloadUri.getPath()));
                    if (imagesUrlList.size() == mSelectedUriList.size()) {
                        addHostelToDb();
                    }

                } else {
                    Log.d(TAG, "Error");
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.getMessage());
                progressDialog.dismiss();
            });
        }

    }

    private void addHostelToDb() {
        StringBuilder roomTypeBuilder = new StringBuilder();
        StringBuilder facilitiesBuilder = new StringBuilder();

        if (facilitiesList.size() > 0) {
            for (String s : facilitiesList) {
                facilitiesBuilder.append(s).append(",");
            }
        } else {
            facilitiesBuilder.append("");
        }

        if (roomTypeList.size() > 0) {
            for (String s : roomTypeList) {
                roomTypeBuilder.append(s).append(",");
            }
        } else {
            roomTypeBuilder.append("");
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.HOSTEL);
        String firebaseUID = dbRef.push().getKey();

        Hostel hostel = new Hostel(firebaseUID,
                etName.getText().toString().trim(),
                addressBuilder.toString(),
                city, FirebaseAuth.getInstance().getUid(),
                mHostelPickedAddress.getLatitude() + "," + mHostelPickedAddress.getLongitude(),
                etHostelPhone.getText().toString().trim(),
                facilitiesBuilder.toString(),
                etHostelRooms.getText().toString(),
                roomTypeBuilder.toString(),
                etPerRoomPrice.getText().toString(),
                imagesUrlList);

        dbRef.child(firebaseUID).setValue(hostel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(mActivity, "Hostel is added!", Toast.LENGTH_SHORT).show();
                UtilClass.loadFragment(new AdminHomeFragment(), mActivity, R.id.admin_frame_layout);
            }
        });
    }

    private AddressData mHostelPickedAddress;
    private StringBuilder addressBuilder;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == Constants.PLACE_PICKER_REQUEST) {
                mHostelPickedAddress = data.getParcelableExtra(Constants.ADDRESS_INTENT);

                getAddressFromPickedAddress(mHostelPickedAddress);
            }
        }

    }

    private String city;

    private void getAddressFromPickedAddress(AddressData mHostelPickedAddress) {
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(mHostelPickedAddress.getLatitude(), mHostelPickedAddress.getLongitude(), 1);
            addressBuilder = new StringBuilder();
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                addressBuilder.append(address.getAddressLine(0));
                city = address.getLocality();
                txtLocationPicker.setText(addressBuilder.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}