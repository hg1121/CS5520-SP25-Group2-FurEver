package com.example.furever;

public class User {
    private String uid;
    private String email;
    private String address;

    // Required empty constructor for Firebase
    public User() {
    }

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
} 