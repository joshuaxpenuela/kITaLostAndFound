package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PreLostLoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private TextView forgotPasswordLink;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_prelogin);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginbtn);
        signupButton = findViewById(R.id.signupBtn);
        forgotPasswordLink = findViewById(R.id.forgotPassLink);

        requestQueue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(v -> loginUser());

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(PreLostLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreLostLoginActivity.this, ForgetPassActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/lost_found_db/login_req.php"; // Update with your server URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("LoginActivity", "Server Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            Toast.makeText(PreLostLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Save user data to UserSession
                            int id = jsonResponse.optInt("id");
                            String firstName = jsonResponse.optString("Fname", "N/A");
                            String lastName = jsonResponse.optString("Lname", "N/A");
                            String contactNo = jsonResponse.optString("contactNo", "N/A");
                            String dept = jsonResponse.optString("dept", "N/A");

                            UserSession.getInstance(this).saveUserData(id, firstName, lastName, email, contactNo, dept);

                            Intent intent = new Intent(PreLostLoginActivity.this, ReportLostActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String error = jsonResponse.optString("error", "Unknown error");
                            Toast.makeText(PreLostLoginActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PreLostLoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LoginActivity", "Error: " + error.getMessage());
                    Toast.makeText(PreLostLoginActivity.this, "Error during login", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}