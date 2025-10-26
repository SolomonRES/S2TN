package com.example.s2tn.model;

import java.time.Duration;

/**
 * Represents a riddle puzzle that players can solve by entering the correct answer.
 */
public class Riddle extends Puzzle {

    /** The question of the riddle. */
    private String question;

    /** The correct answer to the riddle. */
    private String answer;

    /** A hint to help the player solve the riddle. */
    private String hint;

    /**
     * Default constructor.
     * Initializes a default riddle with preset question, answer, and hint.
     */
    public Riddle() {
        this(
            "Riddle",
            "I follow you all the time and copy your every move, but you can’t touch me or catch me. What am I?",
            "shadow",
            "You see it when the sun is out."
        );
    }

    /**
     * Creates a new Riddle with the given details.
     * @param title  the title of the puzzle
     * @param question the riddle question
     * @param answer the correct answer
     * @param hint a hint for the player (can be null)
     */
    public Riddle(String title, String question, String answer, String hint) {
        setTitle(title == null ? "Riddle" : title);
        this.question = question == null ? "" : question;
        this.answer = answer == null ? "" : answer;
        this.hint = hint;
        setState(PuzzleState.INIT);
        setMaxHints(hint == null ? 0 : 1);
    }

    /**
     * Returns the riddle question.
     * @return the question text
     */
    public String getQuestion() { return question; }

    /**
     * Returns the hint for this riddle.
     * @return the hint text
     */
    public String getHint() { return hint; }

    /**
     * Processes player input and checks if it matches the riddle answer.
     * Updates the puzzle state accordingly.
     * @param input the player's answer
     * @return a ValidationResult showing if the input is correct or not
     */
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

    /**
     * Checks if the player meets the achievement condition for this riddle.
     * Condition: riddle solved without using hints.
     * @param achievement the achievement to check
     * @param duration how long it took to solve
     * @param hintsUsed number of hints used
     * @param currentScore the player's current score
     * @return true if the achievement condition is met, false otherwise
     */
    @Override
    protected boolean checkSpecificAchievementCondition(
            Achievement achievement, Duration duration, int hintsUsed, int currentScore) {
        return getState() == PuzzleState.SOLVED && hintsUsed == 0;
    }
}
