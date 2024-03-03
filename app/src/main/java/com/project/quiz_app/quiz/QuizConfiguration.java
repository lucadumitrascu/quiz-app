package com.project.quiz_app.quiz;

import java.io.Serializable;

public class QuizConfiguration implements Serializable {

    String difficulty, category, numberOfQuestions;


    public QuizConfiguration(String difficulty, String category, String numberOfQuestions) {
        this.difficulty = difficulty;
        this.category = category;
        this.numberOfQuestions = numberOfQuestions;
    }

    QuizConfiguration() {
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(String numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }
}
