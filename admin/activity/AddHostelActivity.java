package com.example.hostelrecommendationsystem.admin.activity;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.admin.model.Hostel;
import com.example.hostelrecommendationsystem.utils.AppConstant;
import com.example.hostelrecommendationsystem.utils.InputValidator;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class AddHostelActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = AddHostelActivity.class.getSimpleName();
    private static final int REQUEST_PLACE_PICKER = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hostel);
        initViews();
        onViewsClick();
    }

    private TextView txtLocationPicker;
    private TextInputEditText etName, etHostelPhone, etHostelRooms, etPerRoomPrice;
    private TextInputLayout inputLayoutName, inputLayoutPhone, inputLayoutHostelRooms, inputLayoutPerRoomPrice;

    private Button btnAddHostel;
    private ImageView imgHostel;
    //private VanillaAddress mHostelPickedAddress;
    private InputValidator mInputValidator;

    private List<String> roomTypeList, facilitiesList;

    private void initViews() {
        roomTypeList = new ArrayList<>();
        facilitiesList = new ArrayList<>();

        btnAddHostel = findViewById(R.id.btn_add_hostel);
        imgHostel = findViewById(R.id.img_add_hostel);
        txtLocationPicker = findViewById(R.id.txt_pick_up_location_address);

        etName = findViewById(R.id.et_name);
        etHostelPhone = findViewById(R.id.et_phone);
        etHostelRooms = findViewById(R.id.et_rooms);
        etPerRoomPrice = findViewById(R.id.et_price);

        inputLayoutHostelRooms = findViewById(R.id.text_input_layout_rooms);
        inputLayoutName = findViewById(R.id.text_input_layout_name);
        inputLayoutPhone = findViewById(R.id.text_input_layout_phone);
        inputLayoutPerRoomPrice = findViewById(R.id.text_input_layout_price);

        CheckBox chbSingleSeater = findViewById(R.id.checkBox_single);
        CheckBox chbDoubleSeater = findViewById(R.id.checkBox_double);
        CheckBox chbTripleSeater = findViewById(R.id.checkBox_triple);
        CheckBox chbTetraSeater = findViewById(R.id.checkBox_tetra);

        chbSingleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbDoubleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbTripleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbTetraSeater.setOnCheckedChangeListener(this::onCheckedChanged);

        CheckBox chbGas = findViewById(R.id.checkBox_gas);
        CheckBox chbWifi = findViewById(R.id.checkBox_wifi);
        CheckBox chbElectricity = findViewById(R.id.checkBox_electricity);
        CheckBox chbWarmWater = findViewById(R.id.checkBox_hot_water);
        CheckBox chbFilterWater = findViewById(R.id.checkBox_filter_water);
        CheckBox chbHasMess = findViewById(R.id.checkBox_has_mess);


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

    private List<Uri> mSelectedUriList;
    private StorageReference imagesUploadRef;
    private UploadTask mUploadTask;
    private List<Hostel.HostelImages> imagesUrlList;

    private void onViewsClick() {
        mSelectedUriList = new ArrayList<>();
        imagesUrlList = new ArrayList<>();
        mInputValidator = new InputValidator(this);

        if (checkPermissions()) {
            imgHostel.setOnClickListener(v -> {
                TedBottomPicker.with(this)
                        .setPeekHeight(this.getResources().getDisplayMetrics().heightPixels / 2)
                        .showTitle(true)
                        .setCompleteButtonText("Done")
                        .setEmptySelectionText("No Select")
                        .setSelectedUriList(mSelectedUriList)
                        .showMultiImage(uriList -> {
                            // here is selected image uri list
                            mSelectedUriList.addAll(uriList);
                            Glide.with(this).load(uriList.get(0)).into(imgHostel);

                        });
            });
        }

        txtLocationPicker.setOnClickListener(v -> {
           /* Intent intent = new VanillaPlacePicker.Builder(this)
                    .with(PickerType.MAP_WITH_AUTO_COMPLETE) // Select Picker type to enable autocompelte, map or both
                    .setLocationRestriction(new LatLng(34.124487, 73.190961), new LatLng(34.228272, 73.245651 )) // Restrict location bounds in map and autocomplete
                    //.withLocation(riderCurrentLocation.getLatitude(), riderCurrentLocation.getLongitude())
                    //.setPickerLanguage(PickerLanguage.ENGLISH) // Apply language to picker
                    // .enableShowMapAfterSearchResult(true) // To show the map after selecting the
                    // place from place picker only for PickerType.MAP_WITH_AUTO_COMPLETE

                    *//*
             * Configuration for Map UI
             *//*
                    .setMapType(MapType.NORMAL) // Choose map type (Only applicable for map screen)
                    //.setMapStyle(R.raw.style_json) // Containing the JSON style declaration for night-mode styling
                    .setMapPinDrawable(android.R.drawable.ic_menu_mylocation) // To give custom pin image for map marker

                    .build();*/
            Intent intent = new PlacePicker.IntentBuilder()
                    .setLatLong(34.14685, 73.21449)  // Initial Latitude and Longitude the Map will load into
                    .showLatLong(true)  // Show Coordinates in the Activity
                    .setMapZoom(13.0f)  // Map Zoom Level. Default: 14.0
                    .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                    .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                    //.setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
                    .setMarkerImageImageColor(R.color.colorPrimary)
                    .setFabColor(R.color.colorPrimaryDark)
                    .setPrimaryTextColor(R.color.colorWhite) // Change text color of Shortened Address
                    .setSecondaryTextColor(R.color.colorBlack) // Change text color of full Address
                    .setBottomViewColor(R.color.colorPrimary) // Change Address View Background Color (Default: White)
                    // .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
                    .setMapType(MapType.NORMAL)
                    .setPlaceSearchBar(true, getString(R.string.map_api_key)) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
                    .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                    .hideLocationButton(true)   //Hide Location Button (Default: false)
                    .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false)
                    .build(this);

            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
        });

        btnAddHostel.setOnClickListener(view -> {
            /* validation goes here */
            if (mInputValidator.isInputEditTextFilled(etName, inputLayoutName, "Name is required!")
                    && mInputValidator.isInputEditTextFilled(etHostelPhone, inputLayoutPhone, "Phone is required")
                    && mInputValidator.isInputEditTextFilled(etHostelRooms, inputLayoutHostelRooms, "Room must not be empty!")
                    && mInputValidator.isInputEditTextFilled(etPerRoomPrice, inputLayoutPerRoomPrice, "Price is Required!")) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(this, "uploads is in progress....", Toast.LENGTH_SHORT).show();
                } else {
                    if (mSelectedUriList.size() > 0) {
                        if (mHostelPickedAddress != null)
                            uploadImagesToStorage();
                        else
                            Toast.makeText(this, "Plz choose Address First!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, "Plz select at least one image!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Allow Permissions", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                city,
                mHostelPickedAddress.getLatitude() + "," + mHostelPickedAddress.getLongitude(),
                etHostelPhone.getText().toString().trim(),
                facilitiesBuilder.toString(),
                etHostelRooms.getText().toString(),
                roomTypeBuilder.toString(),
                etPerRoomPrice.getText().toString(),
                imagesUrlList);

        dbRef.child(firebaseUID).setValue(hostel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Hostel is added!", Toast.LENGTH_SHORT).show();
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
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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