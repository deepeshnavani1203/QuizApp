package com.example.quizapp.models;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String id;
    private String questionText;
    private List<String> options;
    private int correctOptionIndex;
    private String topic;
    private String difficulty;
    private String explanation;
    private int userSelectedAnswerIndex = -1;
    private long timeTakenMs = 0; // time spent on this question in milliseconds

    public Question(String id, String questionText, List<String> options, int correctOptionIndex, String topic,
            String difficulty, String explanation) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.topic = topic;
        this.difficulty = difficulty;
        this.explanation = explanation;
    }

    public String getId() { return id; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public String getTopic() { return topic; }
    public String getDifficulty() { return difficulty; }
    public String getExplanation() { return explanation != null ? explanation : "No explanation available."; }
    public String getCorrectAnswer() { return options.get(correctOptionIndex); }
    public int getCorrectAnswerIndex() { return correctOptionIndex; }
    public int getUserSelectedAnswerIndex() { return userSelectedAnswerIndex; }
    public void setUserSelectedAnswerIndex(int index) { this.userSelectedAnswerIndex = index; }
    public long getTimeTakenMs() { return timeTakenMs; }
    public void setTimeTakenMs(long timeTakenMs) { this.timeTakenMs = timeTakenMs; }
}
