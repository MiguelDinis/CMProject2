package com.example.ghostrunner.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class Trail {

    private String id;
    private String trailName;
    private String address;
    private String description;
    private String duration;
    private String distance;
    private String speed;
    private String CreatedDate;
    private String urlPhoto;
    private GeoPoint coordStart;
    private GeoPoint coordEnd;
    private List<GeoPoint> trailPoints;
    private String parentTrail;

    public Trail(String id, String trailName, String address, String description, String duration, String distance,String speed,String CreatedDate, String urlPhoto,GeoPoint coordStart, GeoPoint coordEnd,List<GeoPoint> trailPoints, String parentTrail ) {
        this.id = id;
        this.trailName = trailName;
        this.address = address;
        this.description = description;
        this.duration = duration;
        this.distance = distance;
        this.speed = speed;
        this.CreatedDate = CreatedDate;
        this.urlPhoto = urlPhoto;
        this.coordStart = coordStart;
        this.coordEnd = coordEnd;
        this.trailPoints = trailPoints;
        this.parentTrail = parentTrail;

    }
    public Trail(){}

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


    public void setCoordsStart(GeoPoint coordsStart) {
        this.coordStart = coordsStart;
    }

    public void setCoordsEnd(GeoPoint coordsEnd) {
        this.coordEnd = coordsEnd;
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
    public String getSpeed() {
        return speed;
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

    public List<GeoPoint> getTrailPoints() {
        return trailPoints;
    }

    public String getParentTrail(){
        return parentTrail;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public void setCoordStart(GeoPoint coordStart) {
        this.coordStart = coordStart;
    }

    public void setCoordEnd(GeoPoint coordEnd) {
        this.coordEnd = coordEnd;
    }

    public void setTrailPoints(List<GeoPoint> trailPoints) {
        this.trailPoints = trailPoints;
    }


}