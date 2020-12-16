package com.example.hostelrecommendationsystem.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.activity.CredentialActivity;
import com.example.hostelrecommendationsystem.admin.fragment.AdminRegistrationFragment;
import com.example.hostelrecommendationsystem.user.fragment.UserRegistrationFragment;
import com.example.hostelrecommendationsystem.utils.UtilClass;

public class LoginFragment extends Fragment {

    View view;
    String type;
    EditText edEmail, edPassword;
    TextView tvLogin, tvRegisteration, tvForgotPassword;
    Button buttonLogin;
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


    }
}