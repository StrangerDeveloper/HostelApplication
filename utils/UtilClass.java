package com.example.hostelrecommendationsystem.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class UtilClass {

    /*
    * in this utilclass i create it for comman methods used in the app for less
    * code.
    * loadfragment method is used for loading different methods.
    * checkpermission method is for checking that the user give access to the
    * app for accessing camera, gallery and external storage.
    */

    public static void loadFragment(Fragment fragment, AppCompatActivity appCompatActivity, int container) {
        FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(container,
                        fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static boolean checkPermissions(AppCompatActivity mActivity) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
            return false;
        } else {
            Toast.makeText(mActivity, "Permissions Allowed!", Toast.LENGTH_LONG).show();
            return true;
        }
    }

}
