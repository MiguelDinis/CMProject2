package com.example.cmprojeto.models;

import java.util.List;

public class User {

    String id;
    String userName;
    String mPhotoUrl;
    int userWeight;
    int userHeight;
    int userCalGoal;
    List<String> friends;

    public User() {}

    public User(String id, String userName, int userWeight, int userHeight, int userCalGoal, String mPhotoUrl, List<String> friends)
     {
        this.id = id;
        this.userName = userName;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.userCalGoal = userCalGoal;
        this.mPhotoUrl = mPhotoUrl;
        this.friends = friends;
    }


    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserWeight() {
        return userWeight;
    }

    public int getUserHeight() {
        return userHeight;
    }

    public int getUserCalGoal() {
        return userCalGoal;
    }

    public List<String> getFriends() {
        return friends;
    }
}
