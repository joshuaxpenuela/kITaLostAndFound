package com.example.kITa;

public class NotificationItem {
    private String message;
    private String date;
    private boolean isToday;

    public NotificationItem(String message, String date, boolean isToday) {
        this.message = message;
        this.date = date;
        this.isToday = isToday;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public boolean isToday() {
        return isToday;
    }
}