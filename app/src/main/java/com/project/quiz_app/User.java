package com.project.quiz_app;

public class User {

    String name, email;
    int place, totalScore, lastQuizScore;

    public User(String name, String email, int place, int totalScore, int lastQuizScore) {
        this.name = name;
        this.email = email;
        this.place = place;
        this.lastQuizScore = lastQuizScore;
        this.totalScore = totalScore;
    }

    public User() {
    }

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

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getLastQuizScore() {
        return lastQuizScore;
    }

    public void setLastQuizScore(int lastQuizScore) {
        this.lastQuizScore = lastQuizScore;
    }
}
