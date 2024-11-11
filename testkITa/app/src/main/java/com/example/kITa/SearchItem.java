package com.example.kITa;

public class SearchItem {
    private int id;
    private String name;
    private String location;
    private String imageResourceId; // Changed to String to hold the URL
    private String itemDetails;

    public SearchItem(int id, String name, String location, String imageResourceId, String itemDetails) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imageResourceId = imageResourceId; // Store image URL
        this.itemDetails = itemDetails;
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

    public String getItemDetails() {return itemDetails;}
}
