const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
require("dotenv").config();

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use("/api/quizzes", require("./routes/quiz"));
app.use("/api/results", require("./routes/result"));
app.use("/api/users", require("./routes/users"));

// MongoDB Connection
const MONGODB_URI =
  process.env.MONGODB_URI ||
  "mongodb+srv://deepeshnavani:Deepeshzzz1203@cluster0.uhsxm6j.mongodb.net/quizapp?retryWrites=true&w=majority";
mongoose
  .connect(MONGODB_URI)
  .then(() => console.log("Connected to MongoDB"))
  .catch((err) => console.error("Could not connect to MongoDB", err));

app.get("/", (req, res) => {
  res.send("QuizApp Backend API is running");
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
