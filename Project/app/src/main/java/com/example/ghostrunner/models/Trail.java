package com.example.ghostrunner.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Trail {

    private String id;
    private String trailName;
    private String address;
    private String description;
    private String distance;
    private String CreatedDate;
    private String urlPhoto;
    private GeoPoint coordEnd;
    private GeoPoint coordStart;


    public Trail(String id, String trailName, String address, String description, String distance,String CreatedDate, String urlPhoto,GeoPoint coordEnd, GeoPoint coordStart ) {
        this.id = id;
        this.trailName = trailName;
        this.address = address;
        this.description = description;
        this.distance = distance;
        this.CreatedDate = CreatedDate;
        this.urlPhoto = urlPhoto;
        this.coordStart = coordStart;
        this.coordEnd = coordEnd;
    }

    public Trail(){}

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

    public GeoPoint getCoordEnd() {
        return coordEnd;
    }

    public GeoPoint getCoordStart() {
        return coordStart;
    }
}
