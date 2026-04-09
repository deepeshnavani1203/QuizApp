package com.example.quizapp.models;

import java.util.List;

public class Quiz {
    private String id;
    private String title;
    private String category;
    private int timeLimit;
    private List<Question> questions;

    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getTimeLimit() { return timeLimit; }
    public List<Question> getQuestions() { return questions; }
}
