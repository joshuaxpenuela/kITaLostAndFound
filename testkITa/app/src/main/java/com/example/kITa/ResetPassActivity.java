package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
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

public class ResetPassActivity extends AppCompatActivity {
    private EditText newPassword1, newPassword2;
    private Button confirmResetPassButton;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_resetpass);

        newPassword1 = findViewById(R.id.resetPassword1);
        newPassword2 = findViewById(R.id.resetPassword2);
        confirmResetPassButton = findViewById(R.id.confirmResetPass);

        email = getIntent().getStringExtra("email");

        confirmResetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1 = newPassword1.getText().toString();
                String password2 = newPassword2.getText().toString();

                if (password1.equals(password2) && !password1.isEmpty()) {
                    resetPassword(password1);
                } else {
                    Toast.makeText(ResetPassActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword(final String password) {
        String url = "http://10.0.2.2/lost_found_db/reset_password.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ResetPassActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ResetPassActivity.this, "Error resetting password", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("new_password", password);  // Corrected parameter key
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}