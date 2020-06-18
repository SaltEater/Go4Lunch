package com.colin.go4lunch.models;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private String email;
    private String photo;
    private String selectedPlaceId;
    private String selectedPlaceName;
    private ArrayList<String> likedPlaces;

    public User(String id, String name, String email, String photo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.selectedPlaceId = "";
        this.selectedPlaceName="";
        likedPlaces = new ArrayList<>();
    }

    public User() { }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelectedPlaceId() {
        return selectedPlaceId;
    }

    public String getSelectedPlaceName() {
        return selectedPlaceName;
    }

    public ArrayList<String> getLikedPlaces() {
        return likedPlaces;
    }
}
