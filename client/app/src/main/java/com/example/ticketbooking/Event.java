package com.example.ticketbooking;

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

    // Constructor
    public Event(String id, String name, String description, String location,
                 String startDate, String endDate, String image, int minPrice, int maxPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Other getters and setters if necessary
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
