package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class GuidelinesActivity extends AppCompatActivity {

    private ImageButton guideIcon;
    private ImageButton searchIcon;
    private ImageButton navLost;
    private ImageButton navChat;
    private ImageButton navNotifications;
    private ImageButton navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guideline); // Linking to fragment_guideline.xml

        // Initialize ImageButtons BEFORE calling toggleNavigationBasedOnEmail()
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);


        // Set onClickListeners
        guideIcon.setOnClickListener(v -> {
            // Since guideIcon is already on the GuidelinesActivity, you can refresh it if needed
            finish();
            startActivity(getIntent());
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(GuidelinesActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        navLost.setOnClickListener(v -> {
            Intent intent = new Intent(GuidelinesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(GuidelinesActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(GuidelinesActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(GuidelinesActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

}