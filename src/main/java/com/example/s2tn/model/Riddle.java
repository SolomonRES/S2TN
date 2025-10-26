package com.example.s2tn.model;

import java.time.Duration;

public class Riddle extends Puzzle {
    private String question;
    private String answer;
    private String hint;

    public Riddle() {
        this(
            "Riddle",
            "I follow you all the time and copy your every move, but you can’t touch me or catch me. What am I?",
            "shadow",
            "You see it when the sun is out."
        );
    }

    public Riddle(String title, String question, String answer, String hint) {
        setTitle(title == null ? "Riddle" : title);
        this.question = question == null ? "" : question;
        this.answer = answer == null ? "" : answer;
        this.hint = hint;
        setState(PuzzleState.INIT);
        setMaxHints(hint == null ? 0 : 1);
    }

    public String getQuestion() { return question; }
    public String getHint() { return hint; }

    @Override
    public ValidationResult enterInput(String input) {
        if (input == null || input.isBlank()) {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat("No answer entered.", PuzzleState.IN_PROGRESS);
        }

        boolean ok = input.trim().equalsIgnoreCase(answer.trim());
        if (ok) {
            setState(PuzzleState.SOLVED);
            return ValidationResult.correct("You solved the riddle!", PuzzleState.SOLVED);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.incorrect("Not quite—try again.", PuzzleState.IN_PROGRESS);
        }
    }

    @Override
    protected boolean checkSpecificAchievementCondition(
            Achievement achievement, Duration duration, int hintsUsed, int currentScore) {
        return getState() == PuzzleState.SOLVED && hintsUsed == 0;
    }
}
