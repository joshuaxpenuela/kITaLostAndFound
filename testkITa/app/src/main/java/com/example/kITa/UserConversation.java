package com.example.kITa;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserConversation {
    private int senderId;
    private int receiverId;
    private String message;
    private String createdAt;  // This will be a formatted string for display

    public UserConversation(int senderId, int receiverId, String message, String createdAt) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters for the fields
    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // This method returns a formatted date string for the message time
    public String getFormattedTime() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(createdAt);
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a, dd MMM yyyy");
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return createdAt;
        }
    }
}
