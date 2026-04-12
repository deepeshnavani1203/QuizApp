const mongoose = require("mongoose");

const UserSchema = new mongoose.Schema(
  {
    name: { type: String, required: true },
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    quizHistory: [
      {
        resultId: { type: mongoose.Schema.Types.ObjectId, ref: "Result" },
        quizId: { type: mongoose.Schema.Types.ObjectId, ref: "Quiz" },
        score: Number,
        date: { type: Date, default: Date.now },
      },
    ],
    analytics: {
      weakTopics: [String],
      strengths: [String],
      totalQuizzesTaken: { type: Number, default: 0 },
      averageScore: { type: Number, default: 0 },
    },
  },
  { timestamps: true },
);

module.exports = mongoose.model("User", UserSchema);
