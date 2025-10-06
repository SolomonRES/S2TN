package com.example.s2tn.model;

public class WordScramble {

    private String scrambledWord;
    private String solution;
    private String hint;

    public WordScramble() {
        this.scrambledWord = "";
        this.solution = "";
        this.hint = "";
    }

    public void displayScramble() {
    }

    public boolean checkAnswer(String userInput) {
        return false;
    }

    public String getHint() {
        return hint;
    }
}
