package com.example.hostelrecommendationsystem.user.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.activity.CredentialActivity;
import com.example.hostelrecommendationsystem.fragment.LoginFragment;
import com.example.hostelrecommendationsystem.user.model.UserResponse;
import com.example.hostelrecommendationsystem.utils.AppConstant;
import com.example.hostelrecommendationsystem.utils.UtilClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import gun0912.tedbottompicker.TedBottomPicker;

public class UserRegistrationFragment extends Fragment {

    View view;
    EditText userName, userEmail, userPassword, userConfirmPassword, userContactNumber, userAddress;
    ImageView userImage;
    Button btnAddUser;
    Spinner genderSpinner;
    String[] gender = {"Male", "Female"};
    /* Firebase */
    FirebaseAuth auth;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    private AppCompatActivity mActivity;
    private Uri imageUri;
    private UploadTask mUploadTask;

    public UserRegistrationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CredentialActivity) getActivity())
                .setActionBarTitle("User Registration");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_registration, container, false);

        initViews();
        return view;
    }

    private void initViews() {

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(AppConstant.IMAGE_USER);
        mActivity = (AppCompatActivity) getActivity();

        userName = view.findViewById(R.id.ed_user_name);
        userEmail = view.findViewById(R.id.ed_user_email);
        userPassword = view.findViewById(R.id.ed_user_password);
        userConfirmPassword = view.findViewById(R.id.ed_user_confirm_password);
        userContactNumber = view.findViewById(R.id.ed_user_contact_no);
        userAddress = view.findViewById(R.id.ed_user_address);
        genderSpinner = view.findViewById(R.id.user_gender_spinner);
        userImage = view.findViewById(R.id.user_profile_image);
        btnAddUser = view.findViewById(R.id.user_login_button);

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item, gender);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        genderSpinner.setAdapter(arrayAdapter);

        userImage.setOnClickListener(view -> chooseImage());
        btnAddUser.setOnClickListener(v -> {
            String email = userEmail.getText().toString();
            String password = userPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(mActivity, "Email and Password is Required!", Toast.LENGTH_LONG)
                        .show();
            } else if (password.length() < 6) {
                Toast.makeText(mActivity, "Password must be greater than 6.", Toast.LENGTH_LONG)
                        .show();
            } else {
                registerUserToFirebaseAuth(email, password);
            }

        });

    }

    private void chooseImage() {
        TedBottomPicker.with(mActivity)
            .show(uri -> {
               imageUri = uri;
               Glide.with(mActivity).load(uri).into(userImage);
            });
    }

    private void registerUserToFirebaseAuth(String email, String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (mUploadTask != null && mUploadTask.isInProgress()) {
                            Toast.makeText(mActivity, "Uploading in progress . . . \n" +
                                    "Please Wait", Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            if (imageUri != null && imageUri.getPath() != null) {
                                storageReference = FirebaseStorage.getInstance()
                                        .getReference(AppConstant.IMAGE_USER)
                                        .child(new File(imageUri.getPath()).getName());

                                /*getting downloadable link from uploaded image*/
                                mUploadTask = (UploadTask) storageReference.putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            storageReference.getDownloadUrl()
                                                    .addOnSuccessListener(this::registerUserToFirebase);
                                        });
                            } else {
                                Toast.makeText(mActivity, "Please Attach an Image", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    } else {
                        Toast.makeText(mActivity, "Firebase Error", Toast.LENGTH_LONG)
                                .show();
                    }
                }).addOnFailureListener(mActivity, e -> {
            Toast.makeText(mActivity, "FirebaseAuth Error " + e.toString(), Toast.LENGTH_LONG)
                    .show();
        });
    }

    private void registerUserToFirebase(Uri uri) {

        String name = userName.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPassword = userConfirmPassword.getText().toString();
        String phone = userContactNumber.getText().toString();
        String address = userAddress.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        firebaseUser = auth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(mActivity, "Fill all the fields", Toast.LENGTH_LONG)
                    .show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(mActivity, "Password must be same.", Toast.LENGTH_LONG)
                    .show();
        } else {
            reference = FirebaseDatabase.getInstance().getReference(AppConstant.USER)
                    .child(userID);

            UserResponse response = new UserResponse(userID, uri.toString(), name, email,
                    password, phone, address, gender);

            reference.setValue(response).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(mActivity, "User Added Successfully! ", Toast.LENGTH_LONG)
                            .show();
                    UtilClass.loadFragment(new LoginFragment("User"), mActivity, R.id.credentials_frame_layout);
                }
            });
        }
    }
}