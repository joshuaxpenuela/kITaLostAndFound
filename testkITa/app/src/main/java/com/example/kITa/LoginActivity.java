package com.example.kITa;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private TextView forgotPasswordLink;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginbtn);
        signupButton = findViewById(R.id.signupBtn);
        forgotPasswordLink = findViewById(R.id.forgotPassLink);

        requestQueue = Volley.newRequestQueue(this);

        loginButton.setOnClickListener(v -> loginUser());

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPassActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Show custom dialog when back button is pressed
        super.onBackPressed();
        showUserSelectDialog();
    }

    private void showUserSelectDialog() {
        // Create a custom dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox_userselect);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get dialog buttons
        Button proceedButton = dialog.findViewById(R.id.ProceedUserSelect);
        Button cancelButton = dialog.findViewById(R.id.CancelUserSelect);

        // Proceed button click listener
        proceedButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(LoginActivity.this, UserSelectActivity.class);
            startActivity(intent);
            finish();
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/login_req.php"; // Update with your server URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("LoginActivity", "Server Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Save user data to UserSession
                            int id = jsonResponse.optInt("id");
                            String firstName = jsonResponse.optString("Fname", "N/A");
                            String lastName = jsonResponse.optString("Lname", "N/A");
                            String contactNo = jsonResponse.optString("contactNo", "N/A");
                            String dept = jsonResponse.optString("dept", "N/A");

                            UserSession.getInstance().saveUserData(id, firstName, lastName, email, contactNo, dept);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String error = jsonResponse.optString("error", "Unknown error");
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LoginActivity", "Error: " + error.getMessage());
                    Toast.makeText(LoginActivity.this, "Error during login", Toast.LENGTH_SHORT).show();
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