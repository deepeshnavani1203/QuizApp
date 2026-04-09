package com.example.quizapp.models;

import java.util.List;

public class ResultResponse {
    private String resultId;
    private Analytics analysis;

    public String getResultId() { return resultId; }
    public Analytics getAnalysis() { return analysis; }

    public static class Analytics {
        private List<String> weakTopics;
        private List<String> strengths;
        private int totalQuizzesTaken;
        private double averageScore;

        public List<String> getWeakTopics() { return weakTopics; }
        public List<String> getStrengths() { return strengths; }
        public int getTotalQuizzesTaken() { return totalQuizzesTaken; }
        public double getAverageScore() { return averageScore; }
    }
}
