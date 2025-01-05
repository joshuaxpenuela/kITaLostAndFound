package com.example.kITa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminChatActivity extends AppCompatActivity {

    private EditText messageInput;
    private ImageButton sendMessageButton, uploadImgButton;
    private ImageButton guideIcon, searchIcon, navLost, navChat, navNotifications, navProfile;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private DatabaseHelper databaseHelper;
    private Uri selectedImageUri;

    private Handler messageHandler;
    private static final long MESSAGE_CHECK_INTERVAL = 3000; // Check every 3 seconds
    private boolean isActivityActive = false;
    private List<Message> currentMessages = new ArrayList<>();
    private int lastMessageCount = 0;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chatadmin);

        UserSession userSession = UserSession.getInstance(this);

        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Please log-in your account.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupRecyclerView();
        setupMessageHandler();
        loadPreviousMessages();
        setNavbarListeners();

        sendMessageButton.setOnClickListener(v -> sendMessage());
    }

    private void initializeViews() {
        messageInput = findViewById(R.id.message);
        sendMessageButton = findViewById(R.id.sendMessage);
        uploadImgButton = findViewById(R.id.uploadImg);
        chatRecyclerView = findViewById(R.id.chatAdminRecyclerView);

        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupRecyclerView() {
        int currentUserId = UserSession.getInstance(this).getId();
        chatAdapter = new ChatAdapter(this, currentMessages, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupMessageHandler() {
        messageHandler = new Handler(Looper.getMainLooper());
        startMessageChecking();
    }

    private void startMessageChecking() {
        isActivityActive = true;
        messageHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isActivityActive) {
                    checkForNewMessages();
                    messageHandler.postDelayed(this, MESSAGE_CHECK_INTERVAL);
                }
            }
        });
    }

    private void checkForNewMessages() {
        int userId = UserSession.getInstance(this).getId();
        ApiClient.getUserMessages(userId, new ApiCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages.size() > lastMessageCount) {
                    // New messages received
                    updateMessages(messages);
                    lastMessageCount = messages.size();
                }
            }

            @Override
            public void onError(String error) {
                // Silent error handling for background checks
            }
        });
    }

    private void updateMessages(List<Message> newMessages) {
        runOnUiThread(() -> {
            currentMessages.clear();
            currentMessages.addAll(newMessages);
            chatAdapter.updateMessages(newMessages);
            if (!newMessages.isEmpty()) {
                chatRecyclerView.scrollToPosition(newMessages.size() - 1);
            }
        });
    }

    private void loadPreviousMessages() {
        int userId = UserSession.getInstance(this).getId();
        ApiClient.getUserMessages(userId, new ApiCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                updateMessages(messages);
                lastMessageCount = messages.size();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminChatActivity.this, "Error loading messages: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNavbarListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(this, PreGuidelineActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        navLost.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navChat.setOnClickListener(v -> { finish(); startActivity(getIntent()); });
        navNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        int senderId = UserSession.getInstance(this).getId();

        if (!messageText.isEmpty()) {
            ApiClient.sendMessageToAdmins(senderId, messageText, new ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    if (success) {
                        Message newMessage = new Message(senderId, 0, messageText, new Date(), null);
                        currentMessages.add(newMessage);
                        chatAdapter.updateMessages(currentMessages);
                        messageInput.setText("");
                        chatRecyclerView.scrollToPosition(currentMessages.size() - 1);
                        lastMessageCount = currentMessages.size();
                    } else {
                        Toast.makeText(AdminChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AdminChatActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityActive = true;
        startMessageChecking();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityActive = false;
        messageHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityActive = false;
        if (messageHandler != null) {
            messageHandler.removeCallbacksAndMessages(null);
        }
    }
}