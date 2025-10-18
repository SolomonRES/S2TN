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

    // difficulty (default NORMAL)
    private Difficulty difficulty = Difficulty.NORMAL;

    public Puzzle() {
        this.puzzleID = UUID.randomUUID();
        this.title = "";
        this.state = PuzzleState.NOT_STARTED;
        this.maxHints = 0;
        this.hints = new ArrayList<>();
        this.allowedTime = Duration.ZERO;
        this.variationSeed = 0L;
        this.scoreValue = 0;
        this.timeToBeat = 0.0;
        this.startTime = 0.0;
    }

    // subclasses implement their own input handling
    public abstract ValidationResult enterInput(String input);

    public Hint requestHint(int level) {
        if (level >= 0 && level < hints.size()) return hints.get(level);
        return null;
    }

    public void checkForAchievement(double startTime, double time) {
        // no-op for now
    }

    // getters / setters
    public UUID getPuzzleID() { return puzzleID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public PuzzleState getState() { return state; }
    public void setState(PuzzleState state) { this.state = state; }

    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public Difficulty getDifficulty() { return difficulty; }

    // helper for difficulty move cap
    public boolean canMakeMove(int movesMade) {
        int cap = difficulty.getMaxMoves();
        return cap == 0 || movesMade < cap; // 0 == unlimited
    }

    // state helpers
    public void start() { this.state = PuzzleState.IN_PROGRESS; }
    public void markSolved() { this.state = PuzzleState.SOLVED; }
    public void markFailed() { this.state = PuzzleState.FAILED; }
}
