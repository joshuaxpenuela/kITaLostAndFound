package com.example.kITa;

public class ClaimedItem {

    private int id;
    private String itemName;
    private String location;
    private String img1; // Add img1 field

    public ClaimedItem(int id, String itemName, String location, String img1) {
        this.id = id;
        this.itemName = itemName;
        this.location = location;
        this.img1 = img1; // Initialize img1
    }

    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getLocation() {
        return location;
    }

    public String getImg1() {
        return img1; // Getter for img1 (image URL)
    }
}
