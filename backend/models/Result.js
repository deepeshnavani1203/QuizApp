const mongoose = require("mongoose");

const ResultSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    quizId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Quiz",
      required: true,
    },
    score: { type: Number, required: true },
    totalQuestions: { type: Number, required: true },
    correctAnswers: { type: Number, required: true },
    wrongAnswers: { type: Number, required: true },
    timeTaken: { type: Number, required: true }, // in seconds
    suspiciousActivity: [String],
    topicPerformance: [
      {
        topic: String,
        correct: Number,
        total: Number,
      },
    ],
  },
  { timestamps: true },
);

module.exports = mongoose.model("Result", ResultSchema);
