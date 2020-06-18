package com.colin.go4lunch.models.googleplacedetails;

import com.colin.go4lunch.models.googleplacesearch.Photo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("website")
    @Expose
    private String website;

    public String getFormattedAddress() { return formattedAddress; }

    public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }

    public String getFormattedPhoneNumber() { return formattedPhoneNumber; }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) { this.formattedPhoneNumber = formattedPhoneNumber; }

    public Double getRating() { return rating; }

    public void setRating(Double rating) { this.rating = rating; }

    public String getWebsite() { return website; }

    public void setWebsite(String website) { this.website = website; }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
