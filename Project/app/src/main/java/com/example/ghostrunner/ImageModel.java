package com.example.ghostrunner;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class ImageModel implements Serializable {

    private String id;
    private String imageName;
    private int imagePath;
    private String trailName;
    private String duration;
    private String distance;
    private String speed;
    private String date;
    private GeoPoint coordsStart;
    private GeoPoint coordsEnd;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setTrailName(String trailName) {
        this.trailName = trailName;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCoordsStart(GeoPoint coordsStart) {
        this.coordsStart = coordsStart;
    }

    public void setCoordsEnd(GeoPoint coordsEnd) {
        this.coordsEnd = coordsEnd;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public String getTrailName() {
        return trailName;
    }
    public String getDuration() {
        return duration;
    }
    public String getDistance() {
        return distance;
    }
    public String getSpeed() {
        return speed;
    }
    public String getDate() {
        return date;
    }

    public GeoPoint getCoordsStart() {
        return coordsStart;
    }

    public GeoPoint getCoordsEnd() {
        return coordsEnd;
    }

    public int getImagePath() {
        return imagePath;
    }


}
