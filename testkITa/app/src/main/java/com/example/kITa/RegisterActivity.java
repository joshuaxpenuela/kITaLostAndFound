package com.example.kITa;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, password, confirmPassword, contactNo;
    private Spinner department;
    private Button signUp;
    private ProgressBar loading;
    private TextView loginLink, dataPrivacyDetails;
    private CheckBox dataPrivacyCheckbox;
    private List<String> collegesList;
    private Dialog dataPrivacyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        // Initialize views
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        contactNo = findViewById(R.id.contactNo);
        department = findViewById(R.id.department);
        signUp = findViewById(R.id.signUp);
        loading = findViewById(R.id.loading);
        loginLink = findViewById(R.id.LoginLink);
        dataPrivacyCheckbox = findViewById(R.id.DataPrivacy);
        dataPrivacyDetails = findViewById(R.id.DataPrivacyDetails);

        // Initialize data privacy dialog
        initializeDataPrivacyDialog();

        // Initialize colleges list and fetch data
        collegesList = new ArrayList<>();
        collegesList.add("Select College"); // Default option
        fetchColleges();

        // Set onClick listener for signUp button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set onClick listener for LoginLink TextView
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set onClick listener for Data Privacy Details
        dataPrivacyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPrivacyDialog();
            }
        });
    }

    private void initializeDataPrivacyDialog() {
        dataPrivacyDialog = new Dialog(this);
        dataPrivacyDialog.setContentView(R.layout.dialogbox_dataprivacy);
        dataPrivacyDialog.setCancelable(false);

        Button closeButton = dataPrivacyDialog.findViewById(R.id.closeDataPrivacy);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPrivacyDialog.dismiss();
            }
        });
    }

    private void showDataPrivacyDialog() {
        if (dataPrivacyDialog != null) {
            dataPrivacyDialog.show();
        }
    }

    private void fetchColleges() {
        String url = "http://10.0.2.2/lost_found_db/college.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray collegesArray = response.getJSONArray("colleges");
                                for (int i = 0; i < collegesArray.length(); i++) {
                                    collegesList.add(collegesArray.getString(i));
                                }
                                setupSpinner();
                            } else {
                                Toast.makeText(RegisterActivity.this,
                                        "Failed to fetch colleges: " + response.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this,
                                    "Error parsing college data",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,
                                "Error fetching colleges: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                collegesList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(adapter);
    }

    private void registerUser() {
        final String fName = firstName.getText().toString().trim();
        final String lName = lastName.getText().toString().trim();
        final String userEmail = email.getText().toString().trim();
        final String userPassword = password.getText().toString().trim();
        final String userConfirmPassword = confirmPassword.getText().toString().trim();
        final String userContactNo = contactNo.getText().toString().trim();
        final String userDept = department.getSelectedItem().toString();
        final String dataPrivacyStatus = dataPrivacyCheckbox.isChecked() ? "Agreed" : "Disagreed";

        // Check if any field is empty
        if (fName.isEmpty() || lName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()
                || userConfirmPassword.isEmpty() || userContactNo.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if department is "Select College"
        if (userDept.equals("Select College")) {
            ((TextView)department.getSelectedView()).setError("Please select a valid department");
            department.requestFocus();
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
            return;
        }

        // Validate password match
        if (!userPassword.equals(userConfirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            password.setError("Passwords do not match");
            confirmPassword.setError("Passwords do not match");
            password.requestFocus();
            return;
        }

        // Validate password strength
        if (userPassword.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            password.requestFocus();
            return;
        }

        // Validate contact number
        if (!userContactNo.matches("\\d+")) {
            contactNo.setError("Enter a valid contact number");
            contactNo.requestFocus();
            return;
        }

        // Show loading spinner
        loading.setVisibility(View.VISIBLE);

        // Create and send the registration request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2/lost_found_db/register_req.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.setVisibility(View.GONE);
                        if (response.equals("success")) {
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.equals("email_exists")) {
                            email.setError("This email is already registered");
                            email.requestFocus();
                            Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        } else if (response.equals("contact_exists")) {
                            contactNo.setError("This contact number is already registered");
                            contactNo.requestFocus();
                            Toast.makeText(RegisterActivity.this, "Contact number already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed: " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Registration Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Fname", fName);
                params.put("Lname", lName);
                params.put("email", userEmail);
                params.put("password", userPassword);
                params.put("contactNo", userContactNo);
                params.put("dept", userDept);
                params.put("dataPrivacy", dataPrivacyStatus);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}