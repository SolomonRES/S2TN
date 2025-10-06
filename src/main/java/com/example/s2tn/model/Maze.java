package com.example.s2tn.model;

public class Maze {

    private String startPosition;
    private String endPosition;
    private String playerPosition;

    public Maze() {
        this.startPosition = "";
        this.endPosition = "";
        this.playerPosition = "";
    }

    public void displayPuzzle() {
    }

    public String move(String direction) {
        return "";
    }

    public boolean checkAnswer(String userInput) {
        return false;
    }

    public String getHint() {
        return "";
    }
}
