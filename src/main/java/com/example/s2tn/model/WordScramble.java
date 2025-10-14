package com.example.s2tn.model;

public class WordScramble {

    private String scrambledWord;
    private String solution;
    private String hint;

    public WordScramble() {
        this.scrambledWord = "ESACEP";
        this.solution = "escape";
        this.hint = "Think about what players do when the clock is ticking.";
    }

    public void displayScramble() {
        System.out.println("Unscramble this word: " + scrambledWord);
    }

    public boolean checkAnswer(String userInput) {
        if (userInput == null) return false;
        return userInput.trim().equalsIgnoreCase(solution.trim());
    }

    public String getHint() {
        return hint;
    }
}
