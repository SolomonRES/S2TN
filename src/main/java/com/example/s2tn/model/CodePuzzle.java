package com.example.s2tn.model;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a puzzle where the player must enter a correct code to solve it.
 */
public class CodePuzzle extends Puzzle {

    /** Stores all accepted codes for solving the puzzle. */
    private final Set<String> acceptedCodes = new HashSet<>();

    /** Hint message displayed to the player. */
    private String hint = "A single triumphant word will open the console.";

    /**
     * Default constructor.
     * Initializes with default title, state, and one accepted code.
     */
    public CodePuzzle() {
        setTitle("Code Puzzle");
        setState(PuzzleState.INIT);
        setMaxHints(0);
        acceptedCodes.add("victory");
    }

    /**
     * Creates a new CodePuzzle with a custom title and accepted codes.
     * @param title the puzzle title
     * @param codes the set of accepted codes
     */
    public CodePuzzle(String title, Set<String> codes) {
        setTitle(title == null ? "Code Puzzle" : title);
        setState(PuzzleState.INIT);
        setMaxHints(0);
        if (codes != null) {
            for (String c : codes) {
                if (c != null) acceptedCodes.add(c.trim().toLowerCase());
            }
        }
    }

    /**
     * Adds a new accepted code.
     * @param code the code to add
     */
    public void addAcceptedCode(String code) {
        if (code != null) acceptedCodes.add(code.trim().toLowerCase());
    }

    /**
     * Gets the current hint for the puzzle.
     * @return the hint text
     */
    public String getHint() {
        return hint;
    }

    /**
     * Sets a new hint for the puzzle.
     * @param hint the hint text to set
     */
    public void setHint(String hint) {
        this.hint = (hint == null) ? "" : hint;
    }

    // this is for the facade/driver
    /**
     * Handles player input and validates it against accepted codes.
     * Updates the puzzle state based on correctness.
     * @param input the player's entered code
     * @return a ValidationResult showing if the input is correct, incorrect, or invalid
     */
    @Override
    public ValidationResult enterInput(String input) {
        if (input == null || input.isBlank()) {
            return ValidationResult.invalidFormat("Enter a code.", PuzzleState.IN_PROGRESS);
        }
        boolean ok = acceptedCodes.contains(input.trim().toLowerCase());
        if (ok) {
            setState(PuzzleState.SOLVED);
            return ValidationResult.correct("Correct code.", PuzzleState.SOLVED);
        } else {
            setState(PuzzleState.IN_PROGRESS);
            return ValidationResult.incorrect("Incorrect code.", PuzzleState.IN_PROGRESS);
        }
    }

    // This is for the abstract puzzle
    /**
     * Checks if the player meets the achievement condition for this puzzle.
     * Condition: solved with no hints and within 10 seconds.
     * @param achievement the achievement to check
     * @param duration the time taken to solve
     * @param hintsUsed number of hints used
     * @param currentScore current player score
     * @return true if the achievement condition is met, false otherwise
     */
    @Override
    protected boolean checkSpecificAchievementCondition(
            Achievement achievement, Duration duration, int hintsUsed, int currentScore) {
        return getState() == PuzzleState.SOLVED
                && hintsUsed == 0
                && duration != null
                && duration.getSeconds() <= 10;
    }
}
