package com.example.ghostrunner;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class ImageModel implements Serializable {

    private long id;
    private String imageName;
    private int imagePath;
    private LatLng coordsStart;
    private LatLng coordsEnd;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setCoordsStart(LatLng coordsStart) {
        this.coordsStart = coordsStart;
    }
    public void setCoordsEnd(LatLng coordsEnd) {
        this.coordsEnd = coordsEnd;
    }

    public String getImageName() {
        return imageName;
    }
    public LatLng getCoordsStart() {
        return coordsStart;
    }
    public LatLng getCoordsEnd() {
        return coordsEnd;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }
}
