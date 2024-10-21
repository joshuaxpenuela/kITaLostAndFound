package com.example.kITa;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private int senderId;
    private int receiverId;
    private String text;
    private Date date;  // Date field to store the timestamp
    private String mediaUrl; // Field to store media URL

    // Constructor for message details
    public Message(int senderId, int receiverId, String text, Date date, String mediaUrl) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.date = date; // Initialize date
        this.mediaUrl = mediaUrl; // Initialize media URL
    }

    // Method to get the text of the message
    public String getText() {
        return text;
    }

    // Method to get the sender ID
    public int getSenderId() {
        return senderId;
    }

    // Method to get the receiver ID
    public int getReceiverId() {
        return receiverId;
    }

    // Method to get the media URL
    public String getMediaUrl() {
        return mediaUrl;
    }

    // Method to get the date of the message
    public Date getDate() {
        return date; // Add this method to retrieve the date
    }

    // Method to get formatted time from the date
    public String getFormattedTime() {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, dd MMM yyyy");
            return sdf.format(date);
        }
        return "";
    }
}
