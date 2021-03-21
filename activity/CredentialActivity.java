package com.example.hostelrecommendationsystem.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.fragment.LoginFragment;
import com.example.hostelrecommendationsystem.utils.UtilClass;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class CredentialActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    Button btnAdmin, btnUser;
    private String btnText;
    private ConstraintLayout loginOptionLayout;
    private FrameLayout frameLayout;
    private boolean isLoginOptionShown;

    private static final int RC_GENERAL_PERM = 1012;
    private static final int RC_SETTINGS_SCREEN_PERM = 1023;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential);
        setActionBarTitle("Login Option");

        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        setLoginOptionLayout();
    }


    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton("Settings")
                    .setNegativeButton("Cancel")
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
        }
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

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @AfterPermissionGranted(RC_GENERAL_PERM)
    private void requestPermissions() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA,
        };

        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "Permissions are needed", RC_GENERAL_PERM, perms);
        } else {
            setLoginOptionLayout();
        }
    }

    private void setLoginOptionLayout() {
        loginOptionLayout = findViewById(R.id.login_option_layout);
        btnAdmin = findViewById(R.id.button_admin_login);
        btnUser = findViewById(R.id.button_login_user);

        frameLayout = findViewById(R.id.credentials_frame_layout);

        btnAdmin.setOnClickListener(this);
        btnUser.setOnClickListener(this);


        if (loginOptionLayout.getVisibility() == View.VISIBLE)
            isLoginOptionShown = true;
    }

}
