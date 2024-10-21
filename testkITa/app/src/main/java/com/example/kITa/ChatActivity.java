package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;
    private CardView cssCardView;
    private TextView messageTextView, timeMessageTextView;
    private RecyclerView userChatRecyclerView;
    private ExistingUserChatAdapter chatAdapter;
    private FloatingActionButton addUsrMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);

        initializeViews();
        setClickListeners();
        loadUserConversations();
        loadLatestAdminMessage();
    }

    private void initializeViews() {
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);
        cssCardView = findViewById(R.id.CSSCardView);
        addUsrMsg = findViewById(R.id.addUsrMsg);

        messageTextView = findViewById(R.id.message);
        timeMessageTextView = findViewById(R.id.timeMessage);
        userChatRecyclerView = findViewById(R.id.UserChatRecyclerView);
        userChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ExistingUserChatAdapter(this);
        userChatRecyclerView.setAdapter(chatAdapter);
    }

    private void setClickListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, GuidelinesActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, SearchActivity.class)));
        navLost.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, MainActivity.class)));
        navFound.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, ClaimedActivity.class)));
        navChat.setOnClickListener(v -> { finish(); startActivity(getIntent()); });
        navNotifications.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, ProfileActivity.class)));

        cssCardView.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, AdminChatActivity.class)));

        addUsrMsg.setOnClickListener(v -> {
            UserSearchDialogFragment dialog = new UserSearchDialogFragment();
            dialog.show(getSupportFragmentManager(), "UserSearchDialogFragment");
        });
    }

    private void loadUserConversations() {
        ApiClient.getUserConversations(UserSession.getInstance().getId(), new ApiCallback<List<UserConversation>>() {
            @Override
            public void onSuccess(List<UserConversation> conversations) {
                chatAdapter.setConversations(conversations);  // This method is now available
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChatActivity.this, "Error loading conversations: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLatestAdminMessage() {
        ApiClient.getLatestAdminMessage(new ApiCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                if (message != null) {
                    messageTextView.setText(message.getText());
                    timeMessageTextView.setText(message.getFormattedTime());
                } else {
                    messageTextView.setText("");
                    timeMessageTextView.setText("");
                }

            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChatActivity.this, "Error loading admin messages: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
