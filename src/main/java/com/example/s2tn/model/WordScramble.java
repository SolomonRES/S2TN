package com.example.s2tn.model;

/**
 * Represents a word scramble puzzle where players must unscramble a word.
 */
public class WordScramble {

    /** The scrambled version of the word. */
    private String scrambledWord;

    /** The correct solution to the puzzle. */
    private String solution;

    /** A hint to help the player solve the puzzle. */
    private String hint;

    /**
     * Default constructor.
     * Initializes the puzzle with a default scrambled word, solution, and hint.
     */
    public WordScramble() {
        this.scrambledWord = "ESACEP";
        this.solution = "escape";
        this.hint = "Think about what players do when the clock is ticking.";
    }

    /**
     * Displays the scrambled word to the player.
     */
    public void displayScramble() {
        System.out.println("Unscramble this word: " + scrambledWord);
    }

    /**
     * Checks if the player's input matches the solution.
     * @param userInput the player's answer
     * @return true if the answer is correct, false otherwise
     */
    public boolean checkAnswer(String userInput) {
        if (userInput == null) return false;
        return userInput.trim().equalsIgnoreCase(solution.trim());
    }

    /**
     * Returns the hint for this puzzle.
     * @return the hint text
     */
    public String getHint() {
        return hint;
    }
}
