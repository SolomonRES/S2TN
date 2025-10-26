package com.example.s2tn.model;

import java.time.Duration;

/**
 * Represents a word scramble puzzle where players must unscramble a word.
 */
public class WordScramble extends Puzzle {

    /** The scrambled version of the word. */
    private String scrambledWord;

    /** The correct solution to the puzzle. */
    private String solution;

    /** A hint to help the player solve the puzzle. */
    private String hint;

    /** Default constructor sets up a single word scramble puzzle. */
    public WordScramble() {
        setTitle("Word Scramble");
        this.scrambledWord = "ESACEP";
        this.solution = "escape";
        this.hint = "Think about what players do when the clock is ticking.";
        setState(PuzzleState.INIT);
        setMaxHints(1);
    }

    /** Returns the hint for this puzzle. */
    public String getHint() { return hint; }

    /** Displays the scrambled word. */
    public void displayScramble() {
        System.out.println("Unscramble this word: " + scrambledWord);
    }

    /** 
     * Handles the player's input for the word scramble puzzle. 
     * @param input the player's answer
     * @return ValidationResult representing if the answer is correct or not
     */
    @Override
    public ValidationResult enterInput(String input) {
        if (input == null || input.isBlank()) {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.invalidFormat("Enter a word.", PuzzleState.IN_PROGRESS);
        }

        boolean correct = input.trim().equalsIgnoreCase(solution.trim());
        if (correct) {
            setState(PuzzleState.SOLVED);
            return ValidationResult.correct("You solved the word scramble!", PuzzleState.SOLVED);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.incorrect("Incorrect — try again.", PuzzleState.IN_PROGRESS);
        }
    }

    /** 
     * Checks if the player met the achievement condition for this puzzle.
     * Example: solved without hints and under 15 seconds.
     */
    @Override
    protected boolean checkSpecificAchievementCondition(
            Achievement achievement, Duration duration, int hintsUsed, int currentScore) {
        return getState() == PuzzleState.SOLVED && hintsUsed == 0 && duration.getSeconds() <= 15;
    }
}
