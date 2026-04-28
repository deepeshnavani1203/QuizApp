package com.example.quizapp.models;

import java.io.Serializable;

public class QuizResult implements Serializable {
    private int id;
    private String quizName;
    private int score;
    private int totalQuestions;
    private int timeTaken; // in seconds
    private int accuracy; // percentage
    private String date;

    public QuizResult(String quizName, int score, int totalQuestions, int timeTaken, int accuracy, String date) {
        this.quizName = quizName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTaken = timeTaken;
        this.accuracy = accuracy;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getQuizName() { return quizName; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getTimeTaken() { return timeTaken; }
    public int getAccuracy() { return accuracy; }
    public String getDate() { return date; }
}
