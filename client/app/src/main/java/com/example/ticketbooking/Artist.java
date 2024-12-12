package com.example.ticketbooking;

import java.util.List;

public class Artist {
    private String artistName;
    private String originName;
    private String birthDay;
    private String duration;
    private String more;
    private String image;

    // Constructor
    public Artist(String artistName, String originName, String birthDay,
                  String duration, String more, String image) {
        this.artistName = artistName;
        this.originName = originName;
        this.birthDay = birthDay;
        this.duration = duration;
        this.more = more;
        this.image = image;
    }

    // Getter và Setter
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    // Các getter và setter khác...
}
