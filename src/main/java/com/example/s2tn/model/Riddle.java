package com.example.s2tn.model;

public class Riddle {

    private String question;
    private String answer;
    private String hint;

    public Riddle() {
        this.question = "";
        this.answer = "";
        this.hint = "";
    }

    public void displayRiddle() {
    }

    public boolean checkAnswer(String userInput) {
        return false;
    }

    public String getHint() {
        return hint;
    }
}
