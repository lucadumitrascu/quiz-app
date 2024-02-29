package com.project.quiz_app;

public class User {

    String name, email;
    int place, score;

    public User(String name, String email, int place, int score) {
        this.name = name;
        this.email = email;
        this.place = place;
        this.score = score;
    }

    public User(){}

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

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
