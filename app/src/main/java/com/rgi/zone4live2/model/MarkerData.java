package com.rgi.zone4live2.model;

public class MarkerData {
    double latitude, longitude;
    String title, snippet, imagePhoto, address;

    public MarkerData(double latitude, double longitude, String title, String snippet, String imagePhoto) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.snippet = snippet;
        this.imagePhoto = imagePhoto;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getImagePhoto() {
        return imagePhoto;
    }

    public void setImagePhoto(String imagePhoto) {
        this.imagePhoto = imagePhoto;
    }
}
