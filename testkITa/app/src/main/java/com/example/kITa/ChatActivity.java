package com.example.kITa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ChatActivity extends AppCompatActivity {

    private ImageButton guideIcon, searchIcon, navLost, navChat, navNotifications, navProfile;
    private CardView cssCardView;
    private TextView messageTextView, timeMessageTextView;
    private int currentUserId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if email is empty before setting content view
        if (isEmailEmpty()) {
            // Stay in the current activity
            Toast.makeText(this, "Please log-in as Seeker to proceed messaging.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.fragment_chat);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Retrieve the current user's ID
        currentUserId = getCurrentUserId();

        initializeViews();
        toggleNavigationBasedOnEmail();
        setClickListeners();
        loadLatestAdminMessage();
    }

    private void toggleNavigationBasedOnEmail() {
        boolean isEmailEmpty = isEmailEmpty();
        navChat.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
        navNotifications.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
    }

    // Method to check if email is empty
    private boolean isEmailEmpty() {
        UserSession userSession = UserSession.getInstance();
        String email = userSession.getEmail();
        return email == null || email.trim().isEmpty();
    }

    private void initializeViews() {
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);
        cssCardView = findViewById(R.id.CSSCardView);

        messageTextView = findViewById(R.id.message);
        timeMessageTextView = findViewById(R.id.timeMessage);
    }

    private void setClickListeners() {
        guideIcon.setOnClickListener(v -> navigateIfEmailNotEmpty(GuidelinesActivity.class));
        searchIcon.setOnClickListener(v -> navigateIfEmailNotEmpty(SearchActivity.class));
        navLost.setOnClickListener(v -> navigateIfEmailNotEmpty(MainActivity.class));
        navProfile.setOnClickListener(v -> navigateIfEmailNotEmpty(ProfileActivity.class));

        navChat.setOnClickListener(v -> {
            if (isEmailEmpty()) {
                Toast.makeText(this, "Please log-in to access chat", Toast.LENGTH_SHORT).show();
            } else {
                finish();
                startActivity(getIntent());
            }
        });

        navNotifications.setOnClickListener(v -> navigateIfEmailNotEmpty(NotificationActivity.class));

        cssCardView.setOnClickListener(v -> navigateIfEmailNotEmpty(AdminChatActivity.class));
    }

    // Helper method to navigate only if email is not empty
    private void navigateIfEmailNotEmpty(Class<?> destinationActivity) {
        if (isEmailEmpty()) {
            Toast.makeText(this, "Please log-in to access this feature", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(ChatActivity.this, destinationActivity));
        }
    }

    private void loadLatestAdminMessage() {
        // Pass the current user's ID to the getLatestAdminMessage method
        ApiClient.getLatestAdminMessage(currentUserId, new ApiCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                if (message != null) {
                    messageTextView.setText(message.getText());
                    timeMessageTextView.setText(message.getFormattedTime());
                } else {
                    messageTextView.setText("No messages");
                    timeMessageTextView.setText("");
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChatActivity.this, "Error loading admin messages: " + error, Toast.LENGTH_SHORT).show();
                messageTextView.setText("Error loading messages");
                timeMessageTextView.setText("");
            }
        });
    }

    // Method to get the current user's ID from SharedPreferences
    private int getCurrentUserId() {
        // Retrieve the user ID from SharedPreferences
        // Replace "user_id" with the key you use to store the user ID
        return sharedPreferences.getInt("user_id", -1);
    }
}