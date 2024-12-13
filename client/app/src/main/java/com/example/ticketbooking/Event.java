package com.example.ticketbooking;

import java.util.List;

public class Event {
    private String id;
    private String name;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private String image;
    private int minPrice;
    private int maxPrice;
    private List<String> artists;

    // Constructor
    public Event(String id, String name, String description, String location,
                 String startDate, String endDate, String image, int minPrice,
                 int maxPrice, List<String> artists) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.artists = artists;
    }

    public List<String> getArtists() {
        return artists;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getImage() {
        return image;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }
}
