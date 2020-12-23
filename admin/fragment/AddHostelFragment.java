package com.example.hostelrecommendationsystem.admin.fragment;

import android.app.Activity;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;

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
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.presentation.builder.VanillaPlacePicker;
import com.vanillaplacepicker.utils.KeyUtils;
import com.vanillaplacepicker.utils.MapType;
import com.vanillaplacepicker.utils.PickerLanguage;
import com.vanillaplacepicker.utils.PickerType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private VanillaAddress mHostelPickedAddress;

    private InputValidator mInputValidator;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();

        initViews();
        onViewsClick();
    }


    private void initViews() {

        roomTypeList = new ArrayList<>();
        facilitiesList = new ArrayList<>();

        btnAddHostel = mActivity.findViewById(R.id.btn_add_hostel);
        imgHostel = mActivity.findViewById(R.id.img_add_hostel);
        txtLocationPicker = mActivity.findViewById(R.id.txt_pick_up_location_address);

        etName = mActivity.findViewById(R.id.et_name);
        etHostelPhone = mActivity.findViewById(R.id.et_phone);
        etHostelRooms = mActivity.findViewById(R.id.et_rooms);
        etPerRoomPrice = mActivity.findViewById(R.id.et_price);

        inputLayoutHostelRooms = mActivity.findViewById(R.id.text_input_layout_rooms);
        inputLayoutName = mActivity.findViewById(R.id.text_input_layout_name);
        inputLayoutPhone = mActivity.findViewById(R.id.text_input_layout_phone);
        inputLayoutPerRoomPrice = mActivity.findViewById(R.id.text_input_layout_price);

        CheckBox chbSingleSeater = mActivity.findViewById(R.id.checkBox_single);
        CheckBox chbDoubleSeater = mActivity.findViewById(R.id.checkBox_double);
        CheckBox chbTripleSeater = mActivity.findViewById(R.id.checkBox_triple);
        CheckBox chbTetraSeater = mActivity.findViewById(R.id.checkBox_tetra);

        chbSingleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbDoubleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbTripleSeater.setOnCheckedChangeListener(this::onCheckedChanged);
        chbTetraSeater.setOnCheckedChangeListener(this::onCheckedChanged);

        CheckBox chbGas = mActivity.findViewById(R.id.checkBox_gas);
        CheckBox chbWifi = mActivity.findViewById(R.id.checkBox_wifi);
        CheckBox chbElectricity = mActivity.findViewById(R.id.checkBox_electricity);
        CheckBox chbWarmWater = mActivity.findViewById(R.id.checkBox_hot_water);
        CheckBox chbFilterWater = mActivity.findViewById(R.id.checkBox_filter_water);
        CheckBox chbHasMess = mActivity.findViewById(R.id.checkBox_has_mess);


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
        txtLocationPicker.setOnClickListener(v -> {
            Intent intent = new VanillaPlacePicker.Builder(mActivity)
                    .with(PickerType.MAP_WITH_AUTO_COMPLETE) // Select Picker type to enable autocompelte, map or both
                    .isOpenNow(true)
                    //.withLocation(riderCurrentLocation.getLatitude(), riderCurrentLocation.getLongitude())
                    .setPickerLanguage(PickerLanguage.ENGLISH) // Apply language to picker
                    // .enableShowMapAfterSearchResult(true) // To show the map after selecting the
                    // place from place picker only for PickerType.MAP_WITH_AUTO_COMPLETE

                    /*
                     * Configuration for Map UI
                     */
                    .setMapType(MapType.NORMAL) // Choose map type (Only applicable for map screen)
                    //.setMapStyle(R.raw.style_json) // Containing the JSON style declaration for night-mode styling
                    .setMapPinDrawable(android.R.drawable.ic_menu_mylocation) // To give custom pin image for map marker

                    .build();

            startActivityForResult(intent, REQUEST_PLACE_PICKER);
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
                        uploadImagesToStorage();

                        if (Objects.equals(imagesUrlList.size(), mSelectedUriList.size())) {
                            if (mHostelPickedAddress != null)
                                addHostelToDb();
                            else
                                Toast.makeText(mActivity, "Plz pickup address first", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "list size is not equal: " + imagesUrlList.size() + " selectedList" + mSelectedUriList.size());
                        }

                    } else {
                        Toast.makeText(mActivity, "Plz select at least one image!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    private void uploadImagesToStorage() {
        imagesUploadRef = FirebaseStorage.getInstance().getReference("Images");
        for (Uri uri : mSelectedUriList) {
            StorageReference reference = imagesUploadRef.child(new File(uri.getLastPathSegment()).getName());
            mUploadTask = reference.putFile(uri);
            Task<Uri> urlTask = mUploadTask.continueWithTask(task -> {
                if (!task.isSuccessful())
                    throw task.getException();
                return imagesUploadRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imagesUrlList.add(new Hostel.HostelImages(downloadUri.getPath()));
                } else {
                    Log.d(TAG, "Error");
                }
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
                mHostelPickedAddress.getFormattedAddress(),
                mHostelPickedAddress.getLocality(),
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
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_PLACE_PICKER) {
                mHostelPickedAddress = (VanillaAddress) data.getSerializableExtra(KeyUtils.SELECTED_PLACE);
            }
        }
    }


}