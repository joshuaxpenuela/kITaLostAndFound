package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    private ImageButton guideIcon;
    private ImageButton searchIcon;
    private ImageButton navLost;
    private ImageButton navFound;
    private ImageButton navChat;
    private ImageButton navNotifications;
    private ImageButton navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification); // Linking to fragment_notification.xml

        // Initialize ImageButtons
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        // Set onClickListeners
        guideIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, GuidelinesActivity.class);
            startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        navLost.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navFound.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, ClaimedActivity.class);
            startActivity(intent);
        });

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> {
            // Since navNotifications is the NotificationActivity, you can refresh it if needed
            finish();
            startActivity(getIntent());
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
