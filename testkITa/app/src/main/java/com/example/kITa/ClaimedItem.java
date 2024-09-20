package com.example.kITa;

public class ClaimedItem {
    private int id;
    private String itemName;
    private String location;

    public ClaimedItem(int id, String itemName, String location) {
        this.id = id;
        this.itemName = itemName;
        this.location = location;
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
}