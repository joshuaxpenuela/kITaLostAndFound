package com.example.kITa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
    private Uri selectedImageUri; // Store the selected image URI

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chatadmin);

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupRecyclerView();
        loadPreviousMessages();

        setNavbarListeners();

        sendMessageButton.setOnClickListener(v -> sendMessage());
        uploadImgButton.setOnClickListener(v -> selectImage());
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
        chatAdapter = new ChatAdapter(this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void loadPreviousMessages() {
        int userId = UserSession.getInstance().getId();
        ApiClient.getUserMessages(userId, new ApiCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                chatAdapter.setMessages(messages);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminChatActivity.this, "Error loading messages: " + error, Toast.LENGTH_SHORT).show();
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
        int receiverId = 1; // Set to the receiver ID as needed

        // If there's a message to send
        if (!messageText.isEmpty()) {
            // If an image was selected, send it
            if (selectedImageUri != null) {
                ApiClient.uploadImage(senderId, selectedImageUri, new ApiCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            // Insert message into local database with image
                            chatAdapter.addMessage(new Message(senderId, receiverId, messageText, new Date(), selectedImageUri.toString()));
                            messageInput.setText("");
                            selectedImageUri = null; // Reset the image URI
                        } else {
                            Toast.makeText(AdminChatActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminChatActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Send text message only
                ApiClient.sendMessageToAdmins(senderId, messageText, new ApiCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            // Insert message into local database as text
                            databaseHelper.insertMessage(senderId, receiverId, messageText, false, null); // media_url is null
                            chatAdapter.addMessage(new Message(senderId, receiverId, messageText, new Date(), null));
                            messageInput.setText("");
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

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData(); // Store the selected image URI
            chatAdapter.addImage(selectedImageUri); // Optional: Add image to the chat
        }
    }
}
