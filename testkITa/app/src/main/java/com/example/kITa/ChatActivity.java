package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

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
        setContentView(R.layout.fragment_chat); // Linking to fragment_chat.xml

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
            Intent intent = new Intent(ChatActivity.this, GuidelinesActivity.class);
            startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        navLost.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navFound.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ClaimedActivity.class);
            startActivity(intent);
        });

        navChat.setOnClickListener(v -> {
            // Since navChat is the ChatActivity, you can refresh it if needed
            finish();
            startActivity(getIntent());
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
