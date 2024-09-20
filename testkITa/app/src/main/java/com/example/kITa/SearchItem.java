package com.example.kITa;

public class SearchItem {
    private int id;
    private String name;
    private String location;
    private int imageResourceId;

    public SearchItem(int id, String name, String location, int imageResourceId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imageResourceId = imageResourceId;
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

    public int getImageResourceId() {
        return imageResourceId;
    }
}