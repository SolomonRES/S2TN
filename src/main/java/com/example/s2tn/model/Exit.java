package com.example.s2tn.model;

import java.util.ArrayList;

public class Exit {
    private String direction;
    private Room exitFrom;
    private Room exitTo;
    private boolean isLocked;
    private ArrayList<Puzzle> requiredPuzzles;
    private Hint hint;
    private String lockText;

    public Exit(String direction, Room exitFrom, Room exitTo, boolean isLocked, ArrayList<Puzzle> requiredPuzzles, Hint hint, String lockText){
        this.direction = direction;
        this.exitFrom = exitFrom;
        this.exitTo = exitTo;
        this.isLocked = isLocked;
        this.requiredPuzzles = requiredPuzzles;
        this.hint = hint;
        this.lockText = lockText;
    }

    public String getDirection() {
        return direction;
    }

    public String getLockText() {
        return lockText;
    }

    public Hint getHint() {
        return hint;
    }

    public boolean isLocked() {
        return isLocked;
    }
    public void unlock(){
        this.isLocked = false;
    }

    public Room getExitTo() {
        return exitTo;
    }

    public ArrayList<Puzzle> getRequiredPuzzles() {
        return requiredPuzzles;
    }

    public Room getExitFrom() {
        return exitFrom;
    }
}
