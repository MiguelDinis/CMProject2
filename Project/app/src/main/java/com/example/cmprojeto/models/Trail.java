package com.example.cmprojeto.models;

import java.util.Date;

public class Trail {

    String id;
    private String trailName;
    private String address;
    private String description;
    private String distance;
    private Date CreatedDate;
    private String urlPhoto;

    public Trail(String id, String trailName, String address, String description, String duration, String distance, String velocity, Date CreatedDate, String urlPhoto) {
        this.id = id;
        this.trailName = trailName;
        this.address = address;
        this.description = description;
        this.distance = distance;
        this.CreatedDate = CreatedDate;
        this.urlPhoto = urlPhoto;
    }

    public String getId() {
        return id;
    }

    public String getTrailName() {
        return trailName;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }


    public String getDistance() {
        return distance;
    }


    public Date getDate() {
        return CreatedDate;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }
}
