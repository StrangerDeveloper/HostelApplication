package com.example.hostelrecommendationsystem.admin.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.activity.CredentialActivity;
import com.example.hostelrecommendationsystem.admin.model.AdminResponse;
import com.example.hostelrecommendationsystem.fragment.LoginFragment;
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

public class AdminRegistrationFragment extends Fragment {

    View view;
    EditText adminName, adminEmail, adminPassword, adminConfirmPassword, adminContactNumber, adminAddress;
    ImageView adminImage;
    Button btnAddAdmin;

    /* Firebase */
    FirebaseAuth auth;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;

    private AppCompatActivity mActivity;
    private Uri imageUri;
    private UploadTask mUploadTask;

    public AdminRegistrationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CredentialActivity) getActivity())
                .setActionBarTitle("Admin Registration");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_registration, container, false);
        initViews();

        return view;
    }



    private void initViews() {

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(AppConstant.ADMIN);
        mActivity = (AppCompatActivity) getActivity();

        adminName = view.findViewById(R.id.ed_admin_name);
        adminEmail = view.findViewById(R.id.ed_admin_email);
        adminPassword = view.findViewById(R.id.ed_admin_password);
        adminConfirmPassword = view.findViewById(R.id.ed_admin_confirm_password);
        adminContactNumber = view.findViewById(R.id.ed_admin_contact_no);
        adminAddress = view.findViewById(R.id.ed_admin_address);
        adminImage = view.findViewById(R.id.admin_profile_image);
        btnAddAdmin = view.findViewById(R.id.admin_login_button);

        adminImage.setOnClickListener(view -> chooseImage());

        btnAddAdmin.setOnClickListener(v -> {
            String email = adminEmail.getText().toString();
            String password = adminPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(mActivity, "Email and Password is Required!", Toast.LENGTH_LONG)
                        .show();
            } else if (password.length() < 6) {
                Toast.makeText(mActivity, "Password must be greater than 6.", Toast.LENGTH_LONG)
                        .show();
            } else {
                registerAdminToFirebaseAuth(email, password);
            }

        });

    }

    private void registerAdminToFirebaseAuth(String email, String password) {

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
                                        .getReference(AppConstant.IMAGE_ADMIN)
                                        .child(new File(imageUri.getPath()).getName());

                                /*getting downloadable link from uploaded image*/
                                mUploadTask = (UploadTask) storageReference.putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            storageReference.getDownloadUrl()
                                                    .addOnSuccessListener(this::registerAdminToFirebase);
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

    private void registerAdminToFirebase(Uri uri) {

        String name = adminName.getText().toString();
        String email = adminEmail.getText().toString();
        String password = adminPassword.getText().toString();
        String confirmPassword = adminConfirmPassword.getText().toString();
        String phone = adminContactNumber.getText().toString();
        String address = adminAddress.getText().toString();

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
        }
        {
            reference = FirebaseDatabase.getInstance().getReference(AppConstant.ADMIN)
                    .child(userID);

            AdminResponse response = new AdminResponse(userID, uri.toString(), name, email,
                    password, phone, address);

            reference.setValue(response).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(mActivity, "Admin Added Successfully! ", Toast.LENGTH_LONG)
                            .show();
                    UtilClass.loadFragment(new LoginFragment("Admin"), mActivity, R.id.credentials_frame_layout);
                }
            });
        }

    }

    private void chooseImage() {
        TedBottomPicker.with(mActivity)
                .show(uri -> {
                    imageUri = uri;
                    Glide.with(mActivity).load(uri).into(adminImage);
                });
    }


}