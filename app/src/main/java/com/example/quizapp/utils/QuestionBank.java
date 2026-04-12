package com.example.quizapp.utils;

import com.example.quizapp.models.Question;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionBank {

    private static Map<String, List<Question>> topicMap = new HashMap<>();

    static {
        initializeBank();
    }

    private static void initializeBank() {
        String[] topics = { "Java", "C++", "Python", "JS", "Git", "OS", "React", "Node.js", "DBMS", "Networks", "C" };
        for (String topic : topics) {
            topicMap.put(topic, generate150Questions(topic));
        }
    }

    public static List<Question> getQuestions(String topic, String difficulty, boolean isLearnMode) {
        List<Question> all = topicMap.get(topic);
        if (all == null)
            return new ArrayList<>();

        if (isLearnMode) {
            return new ArrayList<>(all);
        }

        // Test Mode: Exactly 20
        List<Question> filtered = new ArrayList<>();
        for (Question q : all) {
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

    private static List<Question> generate150Questions(String topic) {
        List<Question> list = new ArrayList<>();

        // Sample Core Questions
        if (topic.equals("Java")) {
            list.add(new Question("Size of char in Java?", Arrays.asList("4-bit", "8-bit", "16-bit", "32-bit"), 2,
                    "Java", "Easy"));
            list.add(new Question("Which keyword is used for inheritance?",
                    Arrays.asList("extends", "implements", "inherits", "using"), 0, "Java", "Easy"));
        } else if (topic.equals("Python")) {
            list.add(new Question("Python dictionary is defined by?", Arrays.asList("[]", "{}", "()", "<>"), 1,
                    "Python", "Easy"));
        }

        // Generate up to 150 placeholder questions with clean option labels
        int currentSize = list.size();
        for (int i = currentSize; i < 150; i++) {
            String qText = topic + " Question " + (i + 1);
            List<String> options = Arrays.asList("Option A", "Option B", "Option C", "Option D");
            int correctIndex = i % 4; // rotate correct answer for variety
            String diff = (i % 3 == 0) ? "Hard" : (i % 2 == 0 ? "Medium" : "Easy");

            list.add(new Question(qText, options, correctIndex, topic, diff));
        }
        return list;
    }
}
