package com.example.hostelrecommendationsystem.admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelrecommendationsystem.R;
import com.example.hostelrecommendationsystem.activity.CredentialActivity;
import com.example.hostelrecommendationsystem.admin.fragment.AdminHomeFragment;
import com.example.hostelrecommendationsystem.utils.UtilClass;

public class AdminHomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        UtilClass.loadFragment(new AdminHomeFragment(), this, R.id.admin_frame_layout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_log_out) {
            startActivity(new Intent(this, CredentialActivity.class));
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}