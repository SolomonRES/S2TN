package com.example.s2tn.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Puzzle {
    private UUID puzzleID;
    private String title;
    private PuzzleState state;
    private int maxHints;
    private ArrayList<Hint> hints;
    private int currentHintLevel;
    private Duration allowedTime;
    private long variationSeed;
    private int scoreValue; // Base score for solving the puzzle
    private double timeToBeat;
    private double startTime;
    private List<Achievement> possibleAchievements; // Achievements related to this puzzle


    public Puzzle() {
        this.puzzleID = UUID.randomUUID();
        this.title = "";
        // this.state = PuzzleState.NOT_STARTED;
        this.maxHints = 0;
        this.hints = new ArrayList<>();
        this.currentHintLevel = 0;
        this.allowedTime = Duration.ZERO;
        this.variationSeed = 0L;
        this.scoreValue = 0;
        this.timeToBeat = 0.0;
        this.startTime = 0.0;
        this.possibleAchievements = new ArrayList<>();
    }

    // Abstract method to be used by subclasses
    public abstract ValidationResult enterInput(String input);

    public Hint requestHint() {
        if (currentHintLevel < maxHints && currentHintLevel < hints.size()) {
            Hint requestedHint = hints.get(currentHintLevel);
            currentHintLevel++;
            return requestedHint;
        }
        return null;
    }

    public Hint requestHint(int level) {
        if (level >= 0 && level < maxHints && level < hints.size()) {
            return hints.get(level);
        }
        return null;
    }

    /**
     * Checks for achievements based on the puzzle's performance.
     * Subclasses override `checkSpecificAchievementCondition` to define achievement.
     *
     * @param duration The time taken to complete the puzzle.
     * @param hintsUsed The number of hints used.
     * @param currentScore The current score of the player for this puzzle.
     * @return A list of newly unlocked achievements.
     */

    public List<Achievement> checkForAchievement(Duration duration, int hintsUsed, int currentScore) {
        List<Achievement> newlyUnlocked = new ArrayList<>();

        for (Achievement achievement : possibleAchievements) {
            if (!achievement.isUnlocked()) { // Only check if not already unlocked
                if (checkSpecificAchievementCondition(achievement, duration, hintsUsed, currentScore)) {
                    achievement.unlock(); // Unlock the achievement
                    newlyUnlocked.add(achievement);
                }
            }
        }
        return newlyUnlocked;
    }

    // Abstract method for subclasses to implement achievement logic
    protected abstract boolean checkSpecificAchievementCondition(Achievement achievement, Duration duration, int hintsUsed, int currentScore);

    // Getters and Setters
    public UUID getPuzzleID() { return puzzleID; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public PuzzleState getState() { return state; }
    public void setState(PuzzleState state) { this.state = state; }

    public int getMaxHints() { return maxHints; }
    public void setMaxHints(int maxHints) {
        if (maxHints >= 0) { this.maxHints = maxHints; } else { this.maxHints = 0; }
    }
    
    public void addHint(Hint hint) { if (hint != null) { this.hints.add(hint); } }
    public int getCurrentHintLevel() { return currentHintLevel; }
    public void resetHints() { this.currentHintLevel = 0; }

    public List<Achievement> getPossibleAchievements() { return new ArrayList<>(possibleAchievements); }
    public void addPossibleAchievement(Achievement achievement) {
        if (achievement != null && achievement.getPuzzleId().equals(this.puzzleID)) { // Ensure achievement matches this puzzle
            this.possibleAchievements.add(achievement);
        } else if (achievement != null && achievement.getPuzzleId() == null) {
            // Option to automatically set puzzleId if not set, for convenience during setup
            achievement.setPuzzleId(this.puzzleID);
            this.possibleAchievements.add(achievement);
        } else {
            System.err.println("Warning: Attempted to add an achievement for a different puzzle ID. Achievement: " + achievement.getName() + ", Puzzle ID: " + this.puzzleID);
        }
    }
}