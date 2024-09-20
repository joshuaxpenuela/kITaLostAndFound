package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UserSelectActivity extends AppCompatActivity {

    private Button claimantUserButton;
    private Button finderUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_userselect);

        // Initialize buttons
        claimantUserButton = findViewById(R.id.claimantUser);
        finderUserButton = findViewById(R.id.finderUser);

        // Set click listeners
        claimantUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to RegisterActivity
                Intent intent = new Intent(UserSelectActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        finderUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to MainActivity
                Intent intent = new Intent(UserSelectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
