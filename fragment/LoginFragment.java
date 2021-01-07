package com.example.hostelrecommendationsystem.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.activity.CredentialActivity;
import com.example.hostelrecommendationsystem.admin.activity.AdminHomeActivity;
import com.example.hostelrecommendationsystem.admin.fragment.AdminRegistrationFragment;
import com.example.hostelrecommendationsystem.admin.model.AdminResponse;
import com.example.hostelrecommendationsystem.user.activity.UserHomeActivity;
import com.example.hostelrecommendationsystem.user.fragment.UserRegistrationFragment;
import com.example.hostelrecommendationsystem.user.model.UserResponse;
import com.example.hostelrecommendationsystem.utils.AppConstant;
import com.example.hostelrecommendationsystem.utils.UtilClass;
import com.example.hostelrecommendationsystem.utils.sharedPref.SharedPrefHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

public class LoginFragment extends Fragment {

    View view;
    String type;
    EditText edEmail, edPassword;
    TextView tvLogin, tvRegisteration, tvForgotPassword;
    Button buttonLogin;
    FirebaseAuth auth;
    DatabaseReference dbRef;
    ProgressDialog dialog;
    private AppCompatActivity mActivity;

    public LoginFragment() {
    }

    public LoginFragment(String btnText) {
        this.type = btnText;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CredentialActivity) getActivity())
                .setActionBarTitle(type + " Login");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews();
        return view;
    }

    private void initViews() {

        mActivity = (AppCompatActivity) getActivity();

        System.out.println(type);

        edEmail = view.findViewById(R.id.edEmail);
        edPassword = view.findViewById(R.id.edPassword);
        tvLogin = view.findViewById(R.id.tv_fragment_login);
        tvLogin.setText(type + " Login");
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password);
        tvRegisteration = view.findViewById(R.id.tv_register_now);
        buttonLogin = view.findViewById(R.id.button_login);

        tvRegisteration.setOnClickListener(v -> {
            if (type.equals("Admin"))
                UtilClass.loadFragment(new AdminRegistrationFragment(), mActivity, R.id.credentials_frame_layout);
            else
                UtilClass.loadFragment(new UserRegistrationFragment(), mActivity, R.id.credentials_frame_layout);
        });

        tvForgotPassword.setOnClickListener(v -> {

            Toast.makeText(mActivity, "Forgot Password Fragment to implement", Toast.LENGTH_SHORT).show();
        });
        buttonLogin.setOnClickListener(v -> loginUser());

    }

    private void loginUser() {

        auth = FirebaseAuth.getInstance();

        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(mActivity, "All Fields are required to Login", Toast.LENGTH_SHORT)
                    .show();
        } else {
            dialog = new ProgressDialog(mActivity);
            //dialog.setMessage("Logging . . . \n Please Wait ");
            dialog.setCancelable(false);
            dialog.setTitle("Authenticating!");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            if (type.equals("Admin")) {
                                checkAdminLogin(email, password);
                            } else {
                                checkUserLogin(email, password);
                            }
                        } else {
                            Toast.makeText(mActivity, "Authentication failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void checkUserLogin(String email, String password) {
        dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.USER);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Objects.equals(snap.child("email").getValue(), email)
                            && Objects.equals(snap.child("password").getValue(), password)) {
                        UserResponse userResponse = snap.getValue(UserResponse.class);
                      //  navigateToActivity(UserHomeActivity.class);

                        if (userResponse != null) {
                            SharedPrefHelper.getmHelper().setUserResponse(new Gson().toJson(userResponse));
                            navigateToActivity(UserHomeActivity.class);
                        } else {
                            Toast.makeText(mActivity, "User is a not registered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkAdminLogin(String email, String password) {

        dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.ADMIN);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Objects.equals(snap.child("email").getValue(), email)
                            && Objects.equals(snap.child("password").getValue(), password)) {
                        AdminResponse adminResponse = snap.getValue(AdminResponse.class);
//                        navigateToActivity(AdminHomeActivity.class);

                        if (adminResponse != null) {
                            SharedPrefHelper.getmHelper().setUserResponse(new Gson().toJson(adminResponse));
                            navigateToActivity(AdminHomeActivity.class);
                        } else {
                            Toast.makeText(mActivity, "Admin is a not registered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void navigateToActivity(Class activity) {
        Intent intent = new Intent(mActivity, activity);
        intent.putExtra("type", type);
        mActivity.startActivity(intent);

        mActivity.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }
}