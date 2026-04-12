package com.example.quizapp.models;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String questionText;
    private List<String> options;
    private int correctOptionIndex;
    private String topic;
    private String difficulty;
    private int userSelectedAnswerIndex = -1;

    public Question(String questionText, List<String> options, int correctOptionIndex, String topic,
            String difficulty) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.topic = topic;
        this.difficulty = difficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCorrectAnswer() {
        return options.get(correctOptionIndex);
    }

    public int getCorrectAnswerIndex() {
        return correctOptionIndex;
    }

    public int getUserSelectedAnswerIndex() {
        return userSelectedAnswerIndex;
    }

    public void setUserSelectedAnswerIndex(int index) {
        this.userSelectedAnswerIndex = index;
    }
}
