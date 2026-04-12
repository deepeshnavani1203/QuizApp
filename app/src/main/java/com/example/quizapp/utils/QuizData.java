package com.example.quizapp.utils;

import com.example.quizapp.models.Question;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizData {

    public static List<Question> getQuestions(String topic, String difficulty) {
        List<Question> allQuestions = getQuestionsByTopic(topic);
        List<Question> filteredQuestions = new ArrayList<>();
        
        for (Question q : allQuestions) {
            if (difficulty.equals("Random") || q.getDifficulty().equalsIgnoreCase(difficulty)) {
                filteredQuestions.add(q);
            }
        }
        
        // If not enough in specific bucket, fill with questions from other difficulties to ensure 20
        if (filteredQuestions.size() < 20) {
            for (Question q : allQuestions) {
                if (!filteredQuestions.contains(q)) {
                    filteredQuestions.add(q);
                    if (filteredQuestions.size() >= 20) break;
                }
            }
        }
        
        Collections.shuffle(filteredQuestions);
        
        if (filteredQuestions.size() > 20) {
            return new ArrayList<>(filteredQuestions.subList(0, 20));
        }
        return filteredQuestions;
    }

    private static List<Question> getQuestionsByTopic(String topic) {
        List<Question> questions = new ArrayList<>();
        switch (topic) {
            case "Java": addJavaQuestions(questions); break;
            case "JS": addJSQuestions(questions); break;
            case "Python": addPythonQuestions(questions); break;
            case "React": addReactQuestions(questions); break;
            case "Node.js": addGenericQuestions(questions, "Node.js"); break;
            case "Git": addGenericQuestions(questions, "Git"); break;
            case "OS": addGenericQuestions(questions, "OS"); break;
            case "C++": addGenericQuestions(questions, "C++"); break;
            case "C": addGenericQuestions(questions, "C"); break;
            default: addGenericQuestions(questions, topic); break;
        }
        return questions;
    }

    private static void addJavaQuestions(List<Question> q) {
        q.add(new Question("Java is a ___ language.", Arrays.asList("Object-Oriented", "Functional", "Procedural", "Logic"), 0, "Java", "Easy"));
        q.add(new Question("Size of char in Java?", Arrays.asList("8-bit", "16-bit", "32-bit", "4-bit"), 1, "Java", "Easy"));
        q.add(new Question("Default value of int?", Arrays.asList("0", "null", "1", "-1"), 0, "Java", "Easy"));
        q.add(new Question("Which class is the superclass of all classes?", Arrays.asList("Main", "Class", "Object", "System"), 2, "Java", "Easy"));
        q.add(new Question("Method to find length of String?", Arrays.asList("size()", "length()", "count()", "len()"), 1, "Java", "Easy"));
        q.add(new Question("Keyword for inheritance?", Arrays.asList("implements", "inherits", "extends", "using"), 2, "Java", "Easy"));
        q.add(new Question("What is bytecode?", Arrays.asList("Machine code", "Binary code", "Intermediate code", "Encryption"), 2, "Java", "Easy"));
        q.add(new Question("Can we use 'this' in static methods?", Arrays.asList("Yes", "No", "Depends", "Only in JDK 8+"), 1, "Java", "Medium"));
        q.add(new Question("Which interface is used to make a class serializable?", Arrays.asList("Serializable", "Cloneable", "Externalizable", "Remote"), 0, "Java", "Medium"));
        q.add(new Question("What is JRE?", Arrays.asList("Editor", "Debugger", "Runtime Environment", "Compiler"), 2, "Java", "Easy"));
        q.add(new Question("Is Java platform independent?", Arrays.asList("Yes", "No", "Partially", "None"), 0, "Java", "Easy"));
        q.add(new Question("Does Java support multiple inheritance?", Arrays.asList("Yes", "No", "Only via interfaces", "Depends on OS"), 2, "Java", "Medium"));
        q.add(new Question("What is encapsulation?", Arrays.asList("Data hiding", "Binding data with code", "Inheritance", "Polymorphism"), 1, "Java", "Medium"));
        q.add(new Question("Which one is a checked exception?", Arrays.asList("NullPointerException", "ArithmeticException", "IOException", "IndexOutOfBounds"), 2, "Java", "Hard"));
        q.add(new Question("Function of 'final' keyword?", Arrays.asList("End program", "Prevent override/modification", "Garbage collect", "Save state"), 1, "Java", "Medium"));
        q.add(new Question("What is a Hashmap?", Arrays.asList("List", "Set", "Key-Value pair collection", "Array"), 2, "Java", "Medium"));
        q.add(new Question("Is String mutable in Java?", Arrays.asList("Yes", "No", "Sometimes", "Depends on JVM"), 1, "Java", "Medium"));
        q.add(new Question("Keyword for interface execution?", Arrays.asList("extends", "implements", "gives", "takes"), 1, "Java", "Easy"));
        q.add(new Question("Can a class be private?", Arrays.asList("Yes", "No", "Only nested classes", "Only main class"), 2, "Java", "Hard"));
        q.add(new Question("What is an Abstract class?", Arrays.asList("Final class", "Collection of static methods", "Class that cannot be instantiated", "Class with no methods"), 2, "Java", "Medium"));
        q.add(new Question("Which is NOT a valid access modifier?", Arrays.asList("Public", "Private", "Static", "Protected"), 2, "Java", "Easy"));
        q.add(new Question("Keyword used to call parent constructor?", Arrays.asList("super", "this", "parent", "base"), 0, "Java", "Easy"));
    }

    private static void addJSQuestions(List<Question> q) {
        q.add(new Question("What does DOM stand for?", Arrays.asList("Document Object Model", "Data Object Mode", "Digital Object Model", "None"), 0, "JS", "Easy"));
        for (int i=2; i<=25; i++) {
            q.add(new Question("JS Practice Question " + i, Arrays.asList("A", "B", "C", "D"), 0, "JS", "Easy"));
        }
    }
    
    private static void addPythonQuestions(List<Question> q) {
        q.add(new Question("Which of these is a Python dictionary?", Arrays.asList("[]", "{}", "()", "<>"), 1, "Python", "Easy"));
        for (int i=2; i<=25; i++) {
            q.add(new Question("Python Practice Question " + i, Arrays.asList("A", "B", "C", "D"), 0, "Python", "Medium"));
        }
    }
    
    private static void addReactQuestions(List<Question> q) {
        q.add(new Question("What is JSX?", Arrays.asList("JavaScript XML", "JSON Extension", "Java Syntax", "None"), 0, "React", "Medium"));
        for (int i=2; i<=25; i++) {
            q.add(new Question("React Practice Question " + i, Arrays.asList("A", "B", "C", "D"), 0, "React", "Hard"));
        }
    }

    private static void addGenericQuestions(List<Question> q, String topic) {
        for (int i=1; i<=25; i++) {
            q.add(new Question(topic + " Advanced Concept Question " + i, Arrays.asList("Option A", "Option B", "Option C", "Option D"), 0, topic, "Medium"));
        }
    }
}
