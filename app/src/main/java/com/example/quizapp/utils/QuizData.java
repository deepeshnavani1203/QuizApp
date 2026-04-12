package com.example.quizapp.utils;

import com.example.quizapp.models.Question;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizData {

    public static List<Question> getQuestionsByTopic(String topic) {
        List<Question> questions = new ArrayList<>();
        
        switch (topic) {
            case "OOPS":
                questions.add(new Question("Which of the following is not a pillar of OOPS?", 
                        Arrays.asList("Encapsulation", "Polymorphism", "Compilation", "Inheritance"), 2, "OOPS"));
                questions.add(new Question("Which concept allows a class to have more than one method with the same name?", 
                        Arrays.asList("Overloading", "Overriding", "Abstraction", "Interface"), 0, "OOPS"));
                questions.add(new Question("Hiding internal details and showing functionality is known as?", 
                        Arrays.asList("Inheritance", "Abstraction", "Encapsulation", "Polymorphism"), 1, "OOPS"));
                questions.add(new Question("Basing an object or class upon another object or class is called?", 
                        Arrays.asList("Polymorphism", "Abstraction", "Inheritance", "Encapsulation"), 2, "OOPS"));
                questions.add(new Question("Which pillar of OOPS describes bundling data and methods together?", 
                        Arrays.asList("Encapsulation", "Abstraction", "Inheritance", "Polymorphism"), 0, "OOPS"));
                break;

            case "C":
                questions.add(new Question("Who is the father of C language?", 
                        Arrays.asList("Bjarne Stroustrup", "James Gosling", "Dennis Ritchie", "Guido van Rossum"), 2, "C"));
                questions.add(new Question("Which of the following is a literal constant in C?", 
                        Arrays.asList("int", "main", "123", "return"), 2, "C"));
                questions.add(new Question("Which operator can be used to find the size of a variable?", 
                        Arrays.asList("size()", "sizeof", "length", "count"), 1, "C"));
                questions.add(new Question("Which format specifier is used for integers in C?", 
                        Arrays.asList("%f", "%c", "%d", "%s"), 2, "C"));
                questions.add(new Question("C is which type of language?", 
                        Arrays.asList("Procedural", "Object-Oriented", "Functional", "Logic"), 0, "C"));
                break;

            case "C++":
                questions.add(new Question("C++ is an extension of which language?", 
                        Arrays.asList("Java", "C", "Python", "Pascal"), 1, "C++"));
                questions.add(new Question("Which operator is used for console output in C++?", 
                        Arrays.asList("printf", "cout <<", "System.out.print", "write"), 1, "C++"));
                questions.add(new Question("What is the use of 'new' operator in C++?", 
                        Arrays.asList("Delete object", "Allocate memory", "Initialize variable", "None"), 1, "C++"));
                questions.add(new Question("Which concept is used in C++ but not in C?", 
                        Arrays.asList("Pointers", "Functions", "Classes", "Arrays"), 2, "C++"));
                questions.add(new Question("Which header file is required for cin and cout?", 
                        Arrays.asList("stdio.h", "conio.h", "iostream", "math.h"), 2, "C++"));
                break;

            case "Python":
                questions.add(new Question("Who developed Python?", 
                        Arrays.asList("Dennis Ritchie", "Guido van Rossum", "Bill Gates", "Mark Zuckerberg"), 1, "Python"));
                questions.add(new Question("Which of the following is the correct extension for Python files?", 
                        Arrays.asList(".python", ".pt", ".py", ".pyt"), 2, "Python"));
                questions.add(new Question("Which function is used to get the length of a list in Python?", 
                        Arrays.asList("length()", "len()", "size()", "count()"), 1, "Python"));
                questions.add(new Question("Python uses which of the following for defining blocks of code?", 
                        Arrays.asList("Brackets", "Indentation", "Parentheses", "Quotes"), 1, "Python"));
                questions.add(new Question("Which data type is immutable in Python?", 
                        Arrays.asList("List", "Set", "Dictionary", "Tuple"), 3, "Python"));
                break;

            case "Java":
                questions.add(new Question("Java is developed by?", Arrays.asList("Microsoft", "Oracle", "Sun Microsystems", "Apple"), 2, "Java"));
                questions.add(new Question("Which of these is not a Java feature?", Arrays.asList("Pointer", "Portable", "Dynamic", "Architecture Neutral"), 0, "Java"));
                questions.add(new Question("Which component is used to compile, debug and execute Java programs?", Arrays.asList("JRE", "JIT", "JDK", "JVM"), 2, "Java"));
                questions.add(new Question("What is the extension of Java bytecode files?", Arrays.asList(".java", ".txt", ".class", ".exe"), 2, "Java"));
                questions.add(new Question("Which package contains the System class?", Arrays.asList("java.util", "java.lang", "java.io", "java.net"), 1, "Java"));
                questions.add(new Question("Which of these keywords is used to define interfaces?", Arrays.asList("interface", "Interface", "intf", "none"), 0, "Java"));
                questions.add(new Question("Which of these is used to handle exceptions?", Arrays.asList("try", "catch", "throw", "all of these"), 3, "Java"));
                questions.add(new Question("Which of these is a reserved word in Java?", Arrays.asList("object", "strictfp", "main", "system"), 1, "Java"));
                questions.add(new Question("What is the size of int variable?", Arrays.asList("8 bit", "16 bit", "32 bit", "64 bit"), 2, "Java"));
                questions.add(new Question("Which method is used to start a thread?", Arrays.asList("run()", "start()", "init()", "resume()"), 1, "Java"));
                break;

            case "DBMS":
                questions.add(new Question("What does DBMS stand for?", Arrays.asList("Database Management System", "Data Binary Management System", "Database Maker System", "Data Business Management System"), 0, "DBMS"));
                questions.add(new Question("Which of the following is a type of database?", Arrays.asList("Hierarchical", "Network", "Relational", "All of the above"), 3, "DBMS"));
                questions.add(new Question("What is a primary key?", Arrays.asList("A unique identifier for a row", "A key used for encryption", "A common field between tables", "The first column in a table"), 0, "DBMS"));
                questions.add(new Question("SQL stands for?", Arrays.asList("Structured Query Language", "Simple Query Language", "Standard Query Language", "Sequential Query Language"), 0, "DBMS"));
                questions.add(new Question("Which command is used to remove a table?", Arrays.asList("DELETE", "REMOVE", "DROP", "TRUNCATE"), 2, "DBMS"));
                questions.add(new Question("A row in a database table is also known as?", Arrays.asList("Field", "Tuple", "Record", "Both 2 and 3"), 3, "DBMS"));
                questions.add(new Question("Which of these refers to data about data?", Arrays.asList("Metadata", "Global data", "Subdata", "Hyperdata"), 0, "DBMS"));
                questions.add(new Question("ACID properties stand for?", Arrays.asList("Atomicity, Consistency, Isolation, Durability", "Accuracy, Consistency, Isolation, Durability", "Atomicity, Compactness, Isolation, Durability", "None of these"), 0, "DBMS"));
                questions.add(new Question("Which key relates two tables?", Arrays.asList("Primary Key", "Foreign Key", "Candidate Key", "Super Key"), 1, "DBMS"));
                break;

            case "OS":
                questions.add(new Question("What is the full form of OS?", Arrays.asList("Operating System", "Open System", "Optical System", "Order System"), 0, "OS"));
                questions.add(new Question("Which of the following is not an operating system?", Arrays.asList("Windows", "Linux", "Oracle", "macOS"), 2, "OS"));
                questions.add(new Question("What is a kernel?", Arrays.asList("The core part of OS", "A file system", "A user interface", "A hardware component"), 0, "OS"));
                questions.add(new Question("Which algorithm is used for CPU scheduling?", Arrays.asList("FIFO", "Round Robin", "SJF", "All of the above"), 3, "OS"));
                questions.add(new Question("What is a deadlock?", Arrays.asList("Infinite loop", "Waiting for resources", "System crash", "Virus"), 1, "OS"));
                questions.add(new Question("Which memory is used for temporary storage?", Arrays.asList("ROM", "RAM", "Hard Disk", "Cache"), 1, "OS"));
                questions.add(new Question("What is paging in OS?", Arrays.asList("Memory management scheme", "File management", "Process scheduling", "IO management"), 0, "OS"));
                questions.add(new Question("Virtual memory is?", Arrays.asList("Extra RAM", "On-disk illusion of RAM", "Non-existent memory", "Secondary storage"), 1, "OS"));
                questions.add(new Question("Which is the first program run by OS?", Arrays.asList("Compiler", "BIOS/Loader", "Shell", "Editor"), 1, "OS"));
                break;
        }
        
        return questions;
    }
}
