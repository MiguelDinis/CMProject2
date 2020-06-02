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
    private String distance;
    private String CreatedDate;
    private String urlPhoto;
    private GeoPoint coordStart;
    private GeoPoint coordEnd;
    private List<LatLng> trailPoints;

    public Trail(String id, String trailName, String address, String description, String distance,String CreatedDate, String urlPhoto,GeoPoint coordStart, GeoPoint coordEnd,List<LatLng> trailPoints ) {
        this.id = id;
        this.trailName = trailName;
        this.address = address;
        this.description = description;
        this.distance = distance;
        this.CreatedDate = CreatedDate;
        this.urlPhoto = urlPhoto;
        this.coordStart = coordStart;
        this.coordEnd = coordEnd;
        this.trailPoints = trailPoints;

    }
    public Trail(){}

    public void setId(String id) {
        this.id = id;
    }
    public void setTrailName(String trailName) {
        this.trailName = trailName;
    }



    public void setDistance(String distance) {
        this.distance = distance;
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

    public List<LatLng> getTrailPoints() {
        return trailPoints;
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

    public void setTrailPoints(List<LatLng> trailPoints) {
        this.trailPoints = trailPoints;
    }
}