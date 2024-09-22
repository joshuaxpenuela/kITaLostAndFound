package com.example.kITa;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    public int getText;
    private int id;
    private int senderId;
    private int receiverId;
    private String text;
    private boolean isAdminSender;
    private Date createdAt;
    private String mediaUrl;

    public Message(int id, int senderId, int receiverId, String text, boolean isAdminSender, Date createdAt, String mediaUrl) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.isAdminSender = isAdminSender;
        this.createdAt = createdAt;
        this.mediaUrl = mediaUrl;
    }

    // Getters and setters

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(createdAt);
    }

    public int getSenderId() {
        return senderId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getText() {
        return text;
    }

    public void setId(Integer messageId) {
    }
    public int getReceiverId() { return receiverId;
    }

    public boolean isAdminSender() {
        return isAdminSender;
    }
}