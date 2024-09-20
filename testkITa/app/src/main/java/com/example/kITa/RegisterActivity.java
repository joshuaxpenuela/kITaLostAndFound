package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, password, confirmPassword, contactNo;
    private Spinner department;
    private Button signUp;
    private ProgressBar loading;
    private TextView loginLink; // TextView for LoginLink

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
        loginLink = findViewById(R.id.LoginLink); // Initialize loginLink

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
                // Redirect to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Prevent going back to RegisterActivity
            }
        });
    }

    private void registerUser() {
        final String fName = firstName.getText().toString().trim();
        final String lName = lastName.getText().toString().trim();
        final String userEmail = email.getText().toString().trim();
        final String userPassword = password.getText().toString().trim();
        final String userConfirmPassword = confirmPassword.getText().toString().trim();
        final String userContactNo = contactNo.getText().toString().trim();
        final String userDept = department.getSelectedItem().toString();

        // Check if any field is empty
        if (fName.isEmpty() || lName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()
                || userConfirmPassword.isEmpty() || userContactNo.isEmpty() || userDept.equals("Select Department")) {
            Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            if (userDept.equals("Select Department")) {
                ((TextView)department.getSelectedView()).setError("Please select a department");
            }
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

        // Validate password strength (optional, add your own criteria)
        if (userPassword.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            password.requestFocus();
            return;
        }

        // Validate contact number (optional, add your own criteria)
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
                            // Redirect to LoginActivity after successful registration
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.equals("email_exists")) {
                            email.setError("This email is already registered");
                            email.requestFocus();
                            Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
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
                return params;
            }
        };

        // Add request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
