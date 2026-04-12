const express = require("express");
const router = express.Router();
const Result = require("../models/Result");
const User = require("../models/User");

// Submit quiz result
router.post("/submit", async (req, res) => {
  try {
    const {
      userId,
      quizId,
      score,
      totalQuestions,
      correctAnswers,
      wrongAnswers,
      timeTaken,
      suspiciousActivity,
      topicPerformance,
    } = req.body;

    const newResult = new Result({
      userId,
      quizId,
      score,
      totalQuestions,
      correctAnswers,
      wrongAnswers,
      timeTaken,
      suspiciousActivity,
      topicPerformance,
    });

    const savedResult = await newResult.save();

    // AI Analysis: Update user profile and identify weak topics
    const user = await User.findById(userId);
    if (user) {
      user.quizHistory.push({
        resultId: savedResult._id,
        quizId: quizId,
        score: score,
      });

      // Re-calculate analytics
      const allScores = await Result.find({ userId: userId }).select("score");
      user.analytics.totalQuizzesTaken = allScores.length;
      user.analytics.averageScore =
        allScores.reduce((acc, curr) => acc + curr.score, 0) / allScores.length;

      // Identify weak topics (Rule-based: Score < 60% in a topic)
      const weakTopics = new Set(user.analytics.weakTopics);
      const strengths = new Set(user.analytics.strengths);

      topicPerformance.forEach((tp) => {
        const percentage = (tp.correct / tp.total) * 100;
        if (percentage < 60) {
          weakTopics.add(tp.topic);
          strengths.delete(tp.topic);
        } else if (percentage >= 80) {
          strengths.add(tp.topic);
          weakTopics.delete(tp.topic);
        }
      });

      user.analytics.weakTopics = Array.from(weakTopics);
      user.analytics.strengths = Array.from(strengths);

      await user.save();
    }

    res.json({
      resultId: savedResult._id,
      analysis: user ? user.analytics : {},
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get all results for a user
router.get("/user/:userId", async (req, res) => {
  try {
    const results = await Result.find({ userId: req.params.userId });
    res.json(results);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get all results for a quiz
router.get("/quiz/:quizId", async (req, res) => {
  try {
    const results = await Result.find({ quizId: req.params.quizId });
    res.json(results);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get a specific result
router.get("/:id", async (req, res) => {
  try {
    const result = await Result.findById(req.params.id);
    if (!result) return res.status(404).json({ message: "Result not found" });
    res.json(result);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;
