package com.example.s2tn.model;

public class WordScramble {

    private String scrambledWord;
    private String solution;
    private String hint;

    public WordScramble() {
        // this.scrambledWord = "ESACEP";
        // this.solution = "escape";
        // this.hint = "Think about what players do when the clock is ticking.";
    }

    public void displayScramble() {
        System.out.println("Unscramble the letters: " + scrambledWord);
    }

    public boolean checkAnswer(String answer) {
        return answer != null && answer.equalsIgnoreCase(solution);
    }

    public String getHint() {
        return hint;
    }

    public void setScrambledWord(String s) { this.scrambledWord = s; }
    public String getScrambledWord() { return scrambledWord; }

    public void setSolution(String s) { this.solution = s; }
    public String getSolution() { return solution; }

    public void setTitle(String title) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setTitle'");
    }
}
