package com.example.kITa;

public class OlderItem {
    private int id;
    private String imageUrl;
    private String itemName;
    private String itemLocation;
    private String date;
    private String time;
    private String status;

    public OlderItem(int id, String imageUrl, String itemName, String itemLocation, String date, String time, String status) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.itemLocation = itemLocation;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }
}