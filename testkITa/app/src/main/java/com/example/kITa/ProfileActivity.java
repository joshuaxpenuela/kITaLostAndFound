package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView contactNoTextView;
    private TextView deptTextView;
    private Button logoutButton;
    private TextView editProfile;

    private ImageButton guideIcon;
    private ImageButton searchIcon;
    private ImageButton navLost;
    private ImageButton navChat;
    private ImageButton navNotifications;
    private ImageButton navProfile;

    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UserSession with context
        userSession = UserSession.getInstance(this);

        // Check if user is logged in
        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Please log-in your account.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.fragment_profile);

        // Initialize views
        nameTextView = findViewById(R.id.Name);
        emailTextView = findViewById(R.id.email);
        contactNoTextView = findViewById(R.id.contactNo);
        deptTextView = findViewById(R.id.dept);
        logoutButton = findViewById(R.id.logout);
        editProfile = findViewById(R.id.EditProfile);

        // Initialize navigation buttons
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        // Load user data from the UserSession
        loadUserProfile();

        // Set onClickListeners
        logoutButton.setOnClickListener(v -> {
            // Clear user session with context
            userSession.clearSession();

            // Clear all activities in the stack and start LoginActivity
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        guideIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, GuidelinesActivity.class);
            startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        navLost.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });

        editProfile.setOnClickListener(v -> {
            if (userSession.isLoggedIn()) {
                Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please log-in as Seeker to update your profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status when returning to the activity
        if (!userSession.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        // Reload user profile in case it was updated
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (userSession.isLoggedIn()) {
            Log.d("ProfileActivity", "Email: " + userSession.getEmail());

            String fullName = userSession.getFirstName() + " " + userSession.getLastName();
            nameTextView.setText(fullName);
            emailTextView.setText(userSession.getEmail());
            contactNoTextView.setText(userSession.getContactNo());
            deptTextView.setText(userSession.getDept());
        } else {
            Toast.makeText(this, "Please log-in as Seeker.", Toast.LENGTH_SHORT).show();
            Log.e("ProfileActivity", "User not logged in");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}