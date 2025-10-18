package com.example.s2tn.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

public abstract class Puzzle {
    private UUID puzzleID;
    private String title;
    private PuzzleState state; // Assume PuzzleState is defined
    private int maxHints;
    private ArrayList<Hint> hints;
    private Duration allowedTime;
    private long variationSeed;
    private int scoreValue;
    private double timeToBeat;
    private double startTime;

    // 🔹 New: store difficulty (default NORMAL)
    private Difficulty difficulty = Difficulty.NORMAL;

    public Puzzle() {
        this.puzzleID = UUID.randomUUID();
        this.title = "";
        // this.state = PuzzleState.NOT_STARTED; // starts with a suitable state
        this.maxHints = 0;
        this.hints = new ArrayList<>();
        this.allowedTime = Duration.ZERO;
        this.variationSeed = 0L;
        this.scoreValue = 0;
        this.timeToBeat = 0.0;
        this.startTime = 0.0;
    }

    // Abstract method to be used by subclasses
    public abstract ValidationResult enterInput(String input);

    public Hint requestHint(int level) {
        if (hints.size() > level && level >= 0) {
            return hints.get(level);
        }
        return null;
    }

    /**
     * commenting out so it compiles
     * @param startTime
     * @param time
     */

    // public void reset() {
    //     this.state = PuzzleState.NOT_STARTED;
    //     this.startTime = 0.0;
    //     // Subclasses will add their reset logic
    // }

    public void checkForAchievement(double startTime, double time) {
        // Achievement logic
    }

    // ---------------- Getters / Setters ----------------
    public UUID getPuzzleID() { return puzzleID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public PuzzleState getState() { return state; }
    public void setState(PuzzleState state) { this.state = state; }

    // 🔹 New: Difficulty accessors
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public Difficulty getDifficulty() { return difficulty; }

    // add other getters and setters if needed
}
