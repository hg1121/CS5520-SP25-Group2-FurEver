package com.example.furever;

import java.util.Date;

public class Post {
    private String id;
    private String userId;
    private String userName;
    private Date timestamp;
    private String breed;
    private String sex;
    private String age;
    private String description;
    private String address;
    private double latitude;
    private double longitude;

    // Empty constructor required for Firestore
    public Post() {
    }

    public Post(String userId, String userName, String breed, String sex, String age, 
                String description, String address, double latitude, double longitude) {
        this.userId = userId;
        this.userName = userName;
        this.breed = breed;
        this.sex = sex;
        this.age = age;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = new Date();
    }

    public Post(String userId, String userName, String breed, String sex, String age, String description) {
        this(userId, userName, breed, sex, age, description, "", 0.0, 0.0);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
} 