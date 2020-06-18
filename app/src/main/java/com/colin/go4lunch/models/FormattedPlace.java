package com.colin.go4lunch.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class FormattedPlace implements Serializable {

    private String id;
    private String name;
    private double locationLatitude;
    private double locationLongitude;
    private double rating = -1;
    private String photoReference;

    private String address;
    private String website;
    private String phoneNumber;
    private String isOpenNow;

    private int distance;

    public FormattedPlace() {

    }


    public FormattedPlace(String id,
                          String name,
                          double locationLatitude,
                          double locationLongitude,
                          double rating,
                          String photoRef,
                          int distanceTo,
                          String isOpenNow) {
        this.id = id;
        this.name = name;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.rating = rating;
        this.photoReference = photoRef;
        this.distance = distanceTo;
        this.isOpenNow = isOpenNow;
    }


    public String getId() { return id; }
    public String getName() { return name; }
    public double getLocationLatitude() { return locationLatitude; }
    public double getLocationLongitude() { return locationLongitude; }
    public double getRating() { return rating; }
    public String getPhotoReference() { return photoReference; }
    public String getAddress() { return address; }
    public String getWebsite() { return website; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getIsOpenNow() { return isOpenNow; }
    public int getDistance() { return distance; }


    public void setWebsite(String website) { this.website = website; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRating(double rating) { this.rating = rating; }
    public void setPhotoReference(String photoReference) { this.photoReference = photoReference; }


    @NonNull
    @Override
    public String toString() {
        return "FormattedPlace{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", locationLatitude=" + locationLatitude +
                ", locationLongitude=" + locationLongitude +
                ", rating=" + rating +
                ", photoReference='" + photoReference + '\'' +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isOpenNow='" + isOpenNow + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }
}