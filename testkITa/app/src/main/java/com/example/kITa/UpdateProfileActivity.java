package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText contactNoEditText;
    private Spinner departmentSpinner;
    private TextView emailTextView;
    private TextView submitUpdate;
    private ImageButton guideIcon;
    private ImageButton searchIcon;
    private ImageButton navLost;
    private ImageButton navChat;
    private ImageButton navNotifications;
    private ImageButton navProfile;

    private RequestQueue requestQueue;
    private String email;
    private int id;
    private ArrayAdapter<String> departmentAdapter;
    private List<String> collegesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_updateprofile);

        // Initialize views
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        contactNoEditText = findViewById(R.id.contactNo);
        departmentSpinner = findViewById(R.id.department);
        emailTextView = findViewById(R.id.email);
        submitUpdate = findViewById(R.id.submitUpdate);

        // Initialize navigation icons
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        requestQueue = Volley.newRequestQueue(this);

        // Initialize colleges list and adapter
        collegesList = new ArrayList<>();
        collegesList.add("Select College"); // Add default option
        departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, collegesList);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);

        // Fetch colleges from database
        fetchColleges();

        // Retrieve email from UserSession
        UserSession userSession = UserSession.getInstance();
        email = userSession.getEmail();
        if (email != null) {
            emailTextView.setText(email);
        } else {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
        }

        submitUpdate.setOnClickListener(v -> {
            if (validateInputFields()) {
                updateProfile();
            }
        });

        // Set onClickListeners for navigation
        guideIcon.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, GuidelinesActivity.class);
            startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        navLost.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void fetchColleges() {
        String url = "http://10.0.2.2/lost_found_db/college.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            collegesList.clear();
                            collegesList.add("Select College"); // Add default option

                            // Add colleges from response
                            for (int i = 0; i < jsonResponse.getJSONArray("colleges").length(); i++) {
                                collegesList.add(jsonResponse.getJSONArray("colleges").getString(i));
                            }
                            departmentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(UpdateProfileActivity.this,
                                    "Failed to fetch colleges: " + jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UpdateProfileActivity.this,
                                "Error parsing colleges: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(UpdateProfileActivity.this,
                        "Error fetching colleges: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(stringRequest);
    }

    private boolean validateInputFields() {
        boolean isValid = true;

        if (firstNameEditText.getText().toString().trim().isEmpty()) {
            firstNameEditText.setError("First name is required");
            isValid = false;
        }

        if (lastNameEditText.getText().toString().trim().isEmpty()) {
            lastNameEditText.setError("Last name is required");
            isValid = false;
        }

        if (contactNoEditText.getText().toString().trim().isEmpty()) {
            contactNoEditText.setError("Contact number is required");
            isValid = false;
        } else if (!isValidPhoneNumber(contactNoEditText.getText().toString().trim())) {
            contactNoEditText.setError("Invalid phone number format");
            isValid = false;
        }

        if (departmentSpinner.getSelectedItem().toString().equals("Select College")) {
            Toast.makeText(this, "Please select a college", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidPhoneNumber(String phone) {
        // Regex for phone numbers starting with "09" and having exactly 11 digits
        String phoneRegex = "^09\\d{9}$";
        return phone.matches(phoneRegex);
    }

    private void updateProfile() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String contactNo = contactNoEditText.getText().toString().trim();
        String department = departmentSpinner.getSelectedItem().toString();

        String url = "http://10.0.2.2/lost_found_db/update_profile.php";

        if (email == null || email.isEmpty()) {
            Toast.makeText(UpdateProfileActivity.this, "Email is not set", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("UpdateProfileActivity", "Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            UserSession.getInstance().saveUserData(id, firstName, lastName, email, contactNo, department);
                            finish();
                        } else {
                            String error = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(UpdateProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            if (error.equals("Contact number already exists")) {
                                contactNoEditText.setError("Contact number already in use");
                                contactNoEditText.requestFocus();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UpdateProfileActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(UpdateProfileActivity.this, "Error during update: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("email", email);
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("contactNo", contactNo);
                params.put("department", department);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}