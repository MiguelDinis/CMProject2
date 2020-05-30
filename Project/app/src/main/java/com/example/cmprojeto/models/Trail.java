package com.example.cmprojeto.models;

import java.util.Date;

public class Trail {

    String id;
    private String trailName;
    private String address;
    private String description;
    private String duration;
    private String distance;
    private String velocity;
    private Date date;

    public Trail(String id, String trailName, String address, String description, String duration, String distance, String velocity, Date date) {
        this.id = id;
        this.trailName = trailName;
        this.address = address;
        this.description = description;
        this.duration = duration;
        this.distance = distance;
        this.velocity = velocity;
        this.date = date;
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

    public String getDuration() {
        return duration;
    }

    public String getDistance() {
        return distance;
    }

    public String getVelocity() {
        return velocity;
    }

    public Date getDate() {
        return date;
    }
}
