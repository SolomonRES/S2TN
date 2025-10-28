package com.s2tn.model;

import java.time.Duration;

/**
 * Represents a riddle puzzle where the player must answer a question correctly.
 */
public class Riddle extends Puzzle {
    private String question;
    private String answer;
    private String hint;

    /** Creates an empty riddle with default values. */
    public Riddle() {
        // this(
        //     "Riddle",
        //     "I follow you all the time and copy your every move, but you can’t touch me or catch me. What am I?",
        //     "shadow",
        //     "You see it when the sun is out."
        // );
    }

    /** Creates a riddle with a title, question, and answer. */
    public Riddle(String title, String question, String answer) {
        this(title, question, answer, null);
    }

    /** Creates a riddle with a title, question, answer, and optional hint. */
    public Riddle(String title, String question, String answer, String hint) {
        setTitle(title == null ? "Riddle" : title);
        this.question = question == null ? "" : question;
        this.answer = answer == null ? "" : answer;
        this.hint = hint;
        setState(PuzzleState.INIT);
        setMaxHints(hint == null ? 0 : 1);
    }

    /** Returns the riddle question. */
    public String getQuestion() { return question; }

    /** Returns the hint for this riddle. */
    public String getHint() { return hint; }

    /**
     * Validates the player's answer and updates the puzzle state.
     *
     * @param input the player's answer
     * @return a validation result indicating correctness and new state
     */
    @Override
    public ValidationResult enterInput(String input) {
        if (input == null) {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat("No answer entered.", PuzzleState.IN_PROGRESS);
        }
        boolean ok = input.trim().equalsIgnoreCase(answer.trim());
        if (ok) {
            setState(PuzzleState.SOLVED);
            return ValidationResult.valid("Correct code.", PuzzleState.SOLVED);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat("Incorrect code.", PuzzleState.IN_PROGRESS);
        }
    }

    /**
     * Checks if the achievement condition is met for this riddle.
     * The condition is satisfied if solved without using any hints.
     */
    @Override
    protected boolean checkSpecificAchievementCondition(
            Achievement achievement, Duration duration, int hintsUsed, int currentScore) {
        return getState() == PuzzleState.SOLVED && hintsUsed == 0;
    }
}
