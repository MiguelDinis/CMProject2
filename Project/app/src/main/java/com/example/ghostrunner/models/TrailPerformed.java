package com.example.ghostrunner.models;

import java.util.Date;

public class TrailPerformed {
    private String id;
    private String userId;
    private String trailId;
    private String performedDate;
    private String velocity;
    private String duration;


    public TrailPerformed(String id, String userId, String trailId, String duration,String velocity, String performedDate) {
        this.id = id;
        this.userId = userId;
        this.trailId = trailId;
        this.performedDate = performedDate;
        this.velocity = velocity;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTrailId() {
        return trailId;
    }

    public String getPerformedDate() {
        return performedDate;
    }

    public String getVelocity() {
        return velocity;
    }

    public String getDuration() {
        return duration;
    }
}
