package com.example.s2tn.model;
public class Riddle {

    private String question;
    private String answer;
    private String hint;

    public Riddle() {
        this.question = "I follow you all the time and copy your every move, but you can’t touch me or catch me. What am I?";
        this.answer = "shadow";
        this.hint = "You see it when the sun is out.";
    }

    public void displayRiddle() {
        System.out.println(question);
    }

    public boolean checkAnswer(String userInput) {
        if (userInput == null) return false;
        return userInput.trim().equalsIgnoreCase(answer.trim());
    }

    public String getHint() {
        return hint;
    }
}
