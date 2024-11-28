package com.example.kITa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class AdminChatActivity extends AppCompatActivity {

    private EditText messageInput;
    private ImageButton sendMessageButton, uploadImgButton;
    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private DatabaseHelper databaseHelper;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chatadmin);

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupRecyclerView();
        loadMessages();

        setNavbarListeners();

        sendMessageButton.setOnClickListener(v -> sendMessage());

        // Periodically check for new admin messages
        checkForNewAdminMessages();
    }

    private void initializeViews() {
        messageInput = findViewById(R.id.message);
        sendMessageButton = findViewById(R.id.sendMessage);
        uploadImgButton = findViewById(R.id.uploadImg);
        chatRecyclerView = findViewById(R.id.chatAdminRecyclerView);

        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupRecyclerView() {
        int currentUserId = UserSession.getInstance().getId();
        List<Message> userMessages = databaseHelper.getUserMessages(currentUserId);
        chatAdapter = new ChatAdapter(this, userMessages, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void loadMessages() {
        int userId = UserSession.getInstance().getId();
        ApiClient.getUserMessages(userId, new ApiCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                chatAdapter.updateMessages(messages);
                chatRecyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminChatActivity.this, "Error loading messages: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForNewAdminMessages() {
        ApiClient.getLatestAdminMessage(currentUserId, new ApiCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                if (message != null) {
                    chatAdapter.addMessage(message);
                    chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onError(String error) {
                // Optional: Handle error silently or log
            }
        });
    }

    private void setNavbarListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(this, GuidelinesActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        navLost.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navFound.setOnClickListener(v -> startActivity(new Intent(this, ClaimedActivity.class)));
        navChat.setOnClickListener(v -> { finish(); startActivity(getIntent()); });
        navNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        int senderId = UserSession.getInstance().getId();

        if (!messageText.isEmpty()) {
            ApiClient.sendMessageToAdmins(senderId, messageText, new ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    if (success) {
                        Message newMessage = new Message(senderId, 1, messageText, new Date(), null);
                        databaseHelper.insertMessage(senderId, 1, messageText, false, null);
                        chatAdapter.addMessage(newMessage);
                        messageInput.setText("");
                        chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
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
}