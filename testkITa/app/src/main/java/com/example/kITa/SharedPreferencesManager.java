package com.example.kITa;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "UserPrefs";
    private static SharedPreferencesManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Private constructor to enforce singleton pattern
    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Static method to get the single instance of SharedPreferencesManager
    public static SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    // Save user data
    public void saveUserData(String firstName, String lastName, String email, String contactNo, String dept) {
        editor.putString("Fname", firstName);
        editor.putString("Lname", lastName);
        editor.putString("email", email);
        editor.putString("contactNo", contactNo);
        editor.putString("dept", dept);
        editor.apply();
    }

    // Save email separately
    public void saveUserEmail(String email) {
        editor.putString("email", email);
        editor.apply();
    }

    // Retrieve user data
    public String getFirstName() {
        return sharedPreferences.getString("Fname", "N/A");
    }

    public String getLastName() {
        return sharedPreferences.getString("Lname", "N/A");
    }

    public String getEmail() {
        return sharedPreferences.getString("email", "N/A");
    }

    public String getContactNo() {
        return sharedPreferences.getString("contactNo", "N/A");
    }

    public String getDept() {
        return sharedPreferences.getString("dept", "N/A");
    }

    // Clear user data
    public void clearUserData() {
        editor.clear();
        editor.apply();
    }

}
