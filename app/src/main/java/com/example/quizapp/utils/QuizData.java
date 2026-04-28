package com.example.quizapp.utils;

import com.example.quizapp.models.Question;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizData {

    private static Map<String, List<Question>> quizMap = new HashMap<>();

    static {
        initializeMap();
    }

    private static void initializeMap() {
        quizMap.put("Java", generateQuestions("Java", 150));
        quizMap.put("C++", generateQuestions("C++", 150));
        quizMap.put("Python", generateQuestions("Python", 150));
        quizMap.put("JS", generateQuestions("JS", 150));
        quizMap.put("React", generateQuestions("React", 150));
        quizMap.put("Git", generateQuestions("Git", 150));
        quizMap.put("OS", generateQuestions("OS", 150));
        quizMap.put("DBMS", generateQuestions("DBMS", 150));
        quizMap.put("Node.js", generateQuestions("Node.js", 150));
        quizMap.put("Networks", generateQuestions("Networks", 150));
        quizMap.put("C", generateQuestions("C", 150));
    }

    public static List<Question> getQuestions(String topic, String difficulty, boolean isLearnMode) {
        List<Question> allQuestions = quizMap.get(topic);
        if (allQuestions == null) return new ArrayList<>();

        if (isLearnMode) {
            // Return all questions for learning
            return new ArrayList<>(allQuestions);
        }

        // Test Mode: Filter by difficulty and shuffle to pick 20
        List<Question> filtered = new ArrayList<>();
        for (Question q : allQuestions) {
            if (difficulty.equals("Random") || q.getDifficulty().equalsIgnoreCase(difficulty)) {
                filtered.add(q);
            }
        }

        Collections.shuffle(filtered);
        if (filtered.size() > 20) {
            return new ArrayList<>(filtered.subList(0, 20));
        }
        return filtered;
    }

    private static List<Question> generateQuestions(String topic, int count) {
        List<Question> list = new ArrayList<>();
        // Real Base Questions
        if (topic.equals("Java")) {
            list.add(new Question("j1_data", "Size of float in Java?", Arrays.asList("16-bit", "32-bit", "64-bit", "8-bit"), 1, "Java", "Easy", ""));
            list.add(new Question("j2_data", "Java is a ___ language.", Arrays.asList("Object-Oriented", "Functional", "Procedural", "Logic"), 0, "Java", "Easy", ""));
        } else if (topic.equals("Python")) {
            list.add(new Question("p1_data", "Which of these is a Python dictionary?", Arrays.asList("[]", "{}", "()", "<>"), 1, "Python", "Easy", ""));
        }
        
        // Fill remaining with generated variants to reach 150
        int start = list.size();
        for (int i = start; i < count; i++) {
            String qText = topic + " Advanced Question " + (i + 1);
            List<String> options = Arrays.asList("Option A Value", "Option B Value", "Option C Value", "Option D Value");
            String diff = (i % 3 == 0) ? "Hard" : (i % 2 == 0 ? "Medium" : "Easy");
            list.add(new Question(topic + "_data_" + i, qText, options, 0, topic, diff, ""));
        }
        return list;
    }
}
