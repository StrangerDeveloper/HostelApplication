package com.example.hostelrecommendationsystem.admin.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.admin.fragment.AdminHomeFragment;
import com.example.hostelrecommendationsystem.utils.UtilClass;

public class AdminHomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        UtilClass.loadFragment(new AdminHomeFragment(), this, R.id.admin_frame_layout);
    }

}