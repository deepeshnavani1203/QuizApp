package com.example.quizapp.models;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;
    private String topic;
    private String difficulty; // Added: Easy, Medium, Hard
    private int userSelectedAnswerIndex = -1;

    public Question(String questionText, List<String> options, int correctAnswerIndex, String topic, String difficulty) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.topic = topic;
        this.difficulty = difficulty;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public String getTopic() { return topic; }
    public String getDifficulty() { return difficulty; }
    public int getUserSelectedAnswerIndex() { return userSelectedAnswerIndex; }
    public void setUserSelectedAnswerIndex(int index) { this.userSelectedAnswerIndex = index; }
}
