package com.s2tn.model;

/**
 * Represents a word scramble puzzle where the player must unscramble letters to form a valid word.
 */
public class WordScramble {

    private String scrambledWord;
    private String solution;
    private String hint;

    /** Default constructor for creating an empty word scramble puzzle. */
    public WordScramble() {
        // this.scrambledWord = "ESACEP";
        // this.solution = "escape";
        // this.hint = "Think about what players do when the clock is ticking.";
    }

    /** Displays the scrambled word to the console. */
    public void displayScramble() {
        System.out.println("Unscramble the letters: " + scrambledWord);
    }

    /**
     * Checks if the player's answer matches the correct solution.
     *
     * @param answer the player's input
     * @return true if the answer is correct, false otherwise
     */
    public boolean checkAnswer(String answer) {
        return answer != null && answer.equalsIgnoreCase(solution);
    }

    /** Returns the hint associated with this puzzle. */
    public String getHint() {
        return hint;
    }

    /** Sets the scrambled word for this puzzle. */
    public void setScrambledWord(String s) { this.scrambledWord = s; }

    /** Returns the scrambled word. */
    public String getScrambledWord() { return scrambledWord; }

    /** Sets the correct solution for the puzzle. */
    public void setSolution(String s) { this.solution = s; }

    /** Returns the solution to the puzzle. */
    public String getSolution() { return solution; }

    /**
     * Placeholder for setting a title — currently not implemented.
     *
     * @param title the title to set
     * @throws UnsupportedOperationException always, since this method is not yet implemented
     */
    public void setTitle(String title) {
        throw new UnsupportedOperationException("Unimplemented method 'setTitle'");
    }
}
