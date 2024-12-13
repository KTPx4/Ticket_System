package com.example.ticketbooking.home.adapter;

public class Artist {
    private String artistName;
    private String artistId;  // Add ID for the artist

    public Artist(String artistName, String artistId) {
        this.artistName = artistName;
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }
}
