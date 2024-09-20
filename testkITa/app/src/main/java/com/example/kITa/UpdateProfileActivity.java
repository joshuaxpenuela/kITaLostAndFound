package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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
    private ImageButton navFound;
    private ImageButton navChat;
    private ImageButton navNotifications;
    private ImageButton navProfile;

    private RequestQueue requestQueue;
    private String email;

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
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        requestQueue = Volley.newRequestQueue(this);

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

        navFound.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, ClaimedActivity.class);
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

        if (departmentSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Add your own phone number validation logic here
        // For example, you can use a regular expression to validate the phone number format
        return phoneNumber.matches("^\\+?[0-9]{10,13}$");
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
                            // Update the UserSession with the new details
                            UserSession.getInstance().saveUserData(firstName, lastName, email, contactNo, department);
                            finish();
                        } else {
                            String error = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(UpdateProfileActivity.this, error, Toast.LENGTH_SHORT).show();
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
                params.put("email", email);
                params.put("firstName", firstNameEditText.getText().toString().trim());
                params.put("lastName", lastNameEditText.getText().toString().trim());
                params.put("contactNo", contactNoEditText.getText().toString().trim());
                params.put("department", departmentSpinner.getSelectedItem().toString().trim());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}