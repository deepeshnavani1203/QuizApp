const express = require("express");
const bcrypt = require("bcrypt");
const router = express.Router();
const User = require("../models/User");

// Signup
router.post("/signup", async (req, res) => {
  try {
    const { name, email, password } = req.body;

    // Check if user exists
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(409).json({ message: "User already exists" });
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = new User({
      name,
      email,
      password: hashedPassword,
      quizHistory: [],
      analytics: {
        totalQuizzesTaken: 0,
        averageScore: 0,
        strengths: [],
        weakTopics: [],
      },
    });

    const savedUser = await newUser.save();
    res.status(201).json({
      id: savedUser._id,
      name: savedUser.name,
      email: savedUser.email,
      quizHistory: savedUser.quizHistory,
      analytics: savedUser.analytics,
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Login
router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(401).json({ message: "Invalid credentials" });
    }

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ message: "Invalid credentials" });
    }

    res.json({
      message: "Login successful",
      user: {
        id: user._id,
        name: user.name,
        email: user.email,
        quizHistory: user.quizHistory,
        analytics: user.analytics,
      },
    });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get user by ID
router.get("/:id", async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) return res.status(404).json({ message: "User not found" });
    res.json(user);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get user history
router.get("/:id/history", async (req, res) => {
  try {
    const user = await User.findById(req.params.id).populate(
      "quizHistory.quizId",
      "title category",
    );
    if (!user) return res.status(404).json({ message: "User not found" });
    res.json(user.quizHistory);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// Get user analytics
router.get("/:id/analytics", async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) return res.status(404).json({ message: "User not found" });
    res.json(user.analytics);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;
