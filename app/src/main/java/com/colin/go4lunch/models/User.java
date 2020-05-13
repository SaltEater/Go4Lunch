package com.colin.go4lunch.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String photo;

    public User(String id, String name, String email, String photo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photo = photo;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
