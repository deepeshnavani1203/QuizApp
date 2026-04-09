const express = require('express');
const router = express.Router();
const Quiz = require('../models/Quiz');

// Get all quizzes
router.get('/', async (req, res) => {
    try {
        const quizzes = await Quiz.find().select('title category description timeLimit');
        res.json(quizzes);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// Get specific quiz with shuffled questions and options
router.get('/:id', async (req, res) => {
    try {
        const quiz = await Quiz.findById(req.id);
        if (!quiz) return res.status(404).json({ message: 'Quiz not found' });

        // Shuffle questions
        const shuffledQuestions = quiz.questions.sort(() => Math.random() - 0.5).map(q => {
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
                topic: q.topic
            };
        });

        res.json({
            id: quiz._id,
            title: quiz.title,
            timeLimit: quiz.timeLimit,
            questions: shuffledQuestions
        });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

module.exports = router;
