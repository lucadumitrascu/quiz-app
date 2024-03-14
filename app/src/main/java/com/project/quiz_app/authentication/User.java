package com.project.quiz_app.authentication;

public class User {

    String name, email, dailyQuizAvailableDate;
    int placeInLeaderboard, totalPracticeScore, lastPracticeQuizScore, totalDailyScore, lastDailyQuizScore;


    public User(String name, String email, String dailyQuizAvailableDate, int place, int totalPracticeScore, int lastQuizScore, int dailyTotalScore, int dailyLastQuizScore) {
        this.name = name;
        this.email = email;
        this.dailyQuizAvailableDate = dailyQuizAvailableDate;
        this.placeInLeaderboard = place;
        this.totalPracticeScore = totalPracticeScore;
        this.lastPracticeQuizScore = lastQuizScore;
        this.totalDailyScore = dailyTotalScore;
        this.lastDailyQuizScore = dailyLastQuizScore;
    }

    public User() {
    }


    public int getTotalDailyScore() {
        return totalDailyScore;
    }

    public void setTotalDailyScore(int totalDailyScore) {
        this.totalDailyScore = totalDailyScore;
    }

    public int getLastDailyQuizScore() {
        return lastDailyQuizScore;
    }

    public void setLastDailyQuizScore(int lastDailyQuizScore) {
        this.lastDailyQuizScore = lastDailyQuizScore;
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

    public int getTotalPracticeScore() {
        return totalPracticeScore;
    }

    public void setTotalPracticeScore(int totalPracticeScore) {
        this.totalPracticeScore = totalPracticeScore;
    }

    public int getLastPracticeQuizScore() {
        return lastPracticeQuizScore;
    }

    public void setLastPracticeQuizScore(int lastPracticeQuizScore) {
        this.lastPracticeQuizScore = lastPracticeQuizScore;
    }
}
