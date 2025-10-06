package com.example.s2tn.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public abstract class Puzzle {

    private UUID puzzleID;
    private String title;
    private PuzzleState state;
    private int maxHints;
    private ArrayList<Hint> hints;
    private Duration allowedTime;
    private long variationSeed;
    private int scoreValue;
    private double timeToBeat;
    private double startTime;

    public Puzzle() {
        this.puzzleID = UUID.randomUUID();
        this.title = "";
        this.state = null;
        this.maxHints = 0;
        this.hints = new ArrayList<>();
        this.allowedTime = Duration.ZERO;
        this.variationSeed = 0L;
        this.scoreValue = 0;
        this.timeToBeat = 0.0;
        this.startTime = 0.0;
    }

    public abstract void enterInput(String input); // this was for validation, I'll fix it later

    public Hint requestHint(int level) {
        return null;
    }

    public void reset() {
    }

    public void checkForAchievement(double startTime, double time) {
    }
}
