package com.project.quiz_app.authentication;

public class User {

    String name, email, dailyQuizAvailableDate;
    int placeInLeaderboard, totalScore, lastQuizScore, dailyTotalScore, dailyLastQuizScore;


    public User(String name, String email, String dailyQuizAvailableDate, int place, int totalScore, int lastQuizScore, int dailyTotalScore, int dailyLastQuizScore) {
        this.name = name;
        this.email = email;
        this.dailyQuizAvailableDate = dailyQuizAvailableDate;
        this.placeInLeaderboard = place;
        this.totalScore = totalScore;
        this.lastQuizScore = lastQuizScore;
        this.dailyTotalScore = dailyTotalScore;
        this.dailyLastQuizScore = dailyLastQuizScore;
    }

    public User() {
    }


    public int getDailyTotalScore() {
        return dailyTotalScore;
    }

    public void setDailyTotalScore(int dailyTotalScore) {
        this.dailyTotalScore = dailyTotalScore;
    }

    public int getDailyLastQuizScore() {
        return dailyLastQuizScore;
    }

    public void setDailyLastQuizScore(int dailyLastQuizScore) {
        this.dailyLastQuizScore = dailyLastQuizScore;
    }

    public String getDailyQuizAvailableDate() {
        return dailyQuizAvailableDate;
    }

    public void setDailyQuizAvailableDate(String dailyQuizAvailableDate) {
        this.dailyQuizAvailableDate = dailyQuizAvailableDate;
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

    public int getPlaceInLeaderboard() {
        return placeInLeaderboard;
    }

    public void setPlaceInLeaderboard(int placeInLeaderboard) {
        this.placeInLeaderboard = placeInLeaderboard;
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
