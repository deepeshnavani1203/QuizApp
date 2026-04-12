const express = require("express");
const router = express.Router();
const Quiz = require("../models/Quiz");

// Get all quizzes
router.get("/", async (req, res) => {
  try {
    const quizzes = await Quiz.find({ category: { $ne: "networks" } }).select(
      "title category description timeLimit",
    );
    res.json(quizzes);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get specific quiz with shuffled questions and options
router.get("/:id", async (req, res) => {
  try {
    const quiz = await Quiz.findById(req.params.id);
    if (!quiz) return res.status(404).json({ message: "Quiz not found" });

    // Shuffle questions
    const shuffledQuestions = quiz.questions
      .sort(() => Math.random() - 0.5)
      .map((q) => {
        // Shuffle options and keep track of correct answer
        const originalOptions = [...q.options];
        const correctOption = originalOptions[q.correctOptionIndex];
        const shuffledOptions = originalOptions.sort(() => Math.random() - 0.5);
        const newCorrectIndex = shuffledOptions.indexOf(correctOption);

        return {
          id: q._id,
          questionText: q.questionText,
          options: shuffledOptions,
          correctOptionIndex: newCorrectIndex,
          topic: q.topic,
          difficulty: q.difficulty,
        };
      });

    res.json({
      id: quiz._id,
      title: quiz.title,
      category: quiz.category,
      description: quiz.description,
      timeLimit: quiz.timeLimit,
      questions: shuffledQuestions,
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Create a new quiz
router.post("/", async (req, res) => {
  try {
    const { title, category, description, timeLimit, questions } = req.body;
    const newQuiz = new Quiz({
      title,
      category,
      description,
      timeLimit,
      questions,
    });
    const savedQuiz = await newQuiz.save();
    res.status(201).json(savedQuiz);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Update a quiz
router.put("/:id", async (req, res) => {
  try {
    const updatedQuiz = await Quiz.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
    });
    if (!updatedQuiz)
      return res.status(404).json({ message: "Quiz not found" });
    res.json(updatedQuiz);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Delete a quiz
router.delete("/:id", async (req, res) => {
  try {
    const deletedQuiz = await Quiz.findByIdAndDelete(req.params.id);
    if (!deletedQuiz)
      return res.status(404).json({ message: "Quiz not found" });
    res.json({ message: "Quiz deleted successfully" });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;
