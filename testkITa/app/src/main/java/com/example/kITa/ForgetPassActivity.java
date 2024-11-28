package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button confirmEmailButton, cancelEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_forgotpass);

        emailEditText = findViewById(R.id.email);
        confirmEmailButton = findViewById(R.id.confirmEmail);
        cancelEmailButton = findViewById(R.id.cancelEmail);

        confirmEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (!email.isEmpty()) {
                    // Disable the button to prevent multiple clicks
                    confirmEmailButton.setEnabled(false);
                    sendOTPRequest(email);
                } else {
                    Toast.makeText(ForgetPassActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous screen
            }
        });
    }

    private void sendOTPRequest(final String email) {
        String url = "https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/forget_password.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ForgetPassActivity", "Response: " + response);
                        if (response.contains("OTP sent")) {
                            Intent intent = new Intent(ForgetPassActivity.this, PinActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ForgetPassActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                            // Re-enable the button if the request fails
                            confirmEmailButton.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ForgetPassActivity", "Error: " + error.toString());
                        Toast.makeText(ForgetPassActivity.this, "Error sending OTP", Toast.LENGTH_SHORT).show();
                        // Re-enable the button if there's an error
                        confirmEmailButton.setEnabled(true);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
