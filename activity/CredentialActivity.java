package com.example.hostelrecommendationsystem.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.fragment.LoginFragment;
import com.example.hostelrecommendationsystem.utils.UtilClass;

public class CredentialActivity extends AppCompatActivity implements View.OnClickListener {

    Fragment fragment;
    Button btnAdmin, btnUser;
    private String btnText;
    private ConstraintLayout loginOptionLayout;
    private FrameLayout frameLayout;
    private boolean isLoginOptionShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential);
        setActionBarTitle("Login Option");

        loginOptionLayout = findViewById(R.id.login_option_layout);
        btnAdmin = findViewById(R.id.button_admin_login);
        btnUser = findViewById(R.id.button_login_user);

        frameLayout = findViewById(R.id.credentials_frame_layout);

        btnAdmin.setOnClickListener(this);
        btnUser.setOnClickListener(this);

        if (loginOptionLayout.getVisibility() == View.VISIBLE)
            isLoginOptionShown = true;

    }

    @Override
    public void onClick(View v) {

        if (R.id.button_admin_login == v.getId())
            btnText = "Admin";
        else
            btnText = "User";

        if (isLoginOptionShown)
            loginOptionLayout.setVisibility(View.GONE);

        frameLayout.setVisibility(View.VISIBLE);
        UtilClass.loadFragment(new LoginFragment(btnText), this, R.id.credentials_frame_layout);

    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
