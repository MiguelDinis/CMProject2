package com.example.ghostrunner.models;

import java.util.Date;

public class Trail {

    private String id;
    private String userId;
    private String trailName;
    private String address;
    private String description;
    private String distance;
    private String CreatedDate;
    private String urlPhoto;

    public Trail(String id, String userId, String trailName, String address, String description, String distance,String CreatedDate, String urlPhoto) {
        this.id = id;
        this.trailName = trailName;
        this.address = address;
        this.description = description;
        this.distance = distance;
        this.CreatedDate = CreatedDate;
        this.urlPhoto = urlPhoto;
        this.userId = userId;
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


    public String getDate() {
        return CreatedDate;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public String getUserId(){
        return userId;
    }
}
