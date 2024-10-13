package com.example.kITa;

public class SearchItem {
    private int id;
    private String name;
    private String location;
    private String imageResourceId; // Changed to String to hold the URL

    public SearchItem(int id, String name, String location, String imageResourceId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imageResourceId = imageResourceId; // Store image URL
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getImageResourceId() { // Return the URL instead of resource ID
        return imageResourceId;
    }
}
