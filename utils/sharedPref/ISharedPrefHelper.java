package com.example.hostelrecommendationsystem.utils.sharedPref;

public interface ISharedPrefHelper {

    String getAdminResponse();

    void setAdminResponse(String adminResponse);

    String getUserResponse();

    void setUserResponse(String userResponse);

    void clearPreferences();

}
