package com.example.cmprojeto.models;

public class User {

    String id;
    String userName;
    int userWeight;
    int userHeight;
    int userCalGoal;

    public User() {}

    public User(String id, String userName, int userWeight, int userHeight, int userCalGoal) {
        this.id = id;
        this.userName = userName;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.userCalGoal = userCalGoal;
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
}
