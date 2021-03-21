package com.example.hostelrecommendationsystem.utils.sharedPref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hostelrecommendationsystem.utils.AppContext;

public class SharedPrefHelper implements ISharedPrefHelper {

    private static final String PREF_NAME = "PREF_NAME";
    private static final String PREF_ADMIN_RESPONSE = "PREF_ADMIN_RESPONSE";
    private static final String PREF_USER_RESPONSE = "PREF_USER_RESPONSE";
    private static final String PREF_USER_FILTER = "PREF_USER_FILTER";

    public static SharedPrefHelper mHelper;
    private final SharedPreferences mPreferences;

    public SharedPrefHelper() {
        mPreferences = AppContext.getmContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefHelper getmHelper() {
        if (mHelper == null) {
            mHelper = new SharedPrefHelper();
        }
        return mHelper;
    }

    @Override
    public String getAdminResponse() {
        return mPreferences.getString(PREF_ADMIN_RESPONSE, null);
    }

    @Override
    public void setAdminResponse(String adminResponse) {
        mPreferences.edit().putString(PREF_ADMIN_RESPONSE, adminResponse).apply();
    }

    @Override
    public String getUserResponse() {
        return mPreferences.getString(PREF_USER_RESPONSE, null);
    }

    @Override
    public void setUserResponse(String userResponse) {
        mPreferences.edit().putString(PREF_USER_RESPONSE, userResponse).apply();
    }

    @Override
    public void clearPreferences() {
        mPreferences.edit().clear().apply();
    }

    @Override
    public String getFilterData() {
        return mPreferences.getString(PREF_USER_FILTER, null);
    }

    @Override
    public void setFilterData(String filterData) {
        mPreferences.edit().putString(PREF_USER_FILTER, filterData).apply();
    }
}
