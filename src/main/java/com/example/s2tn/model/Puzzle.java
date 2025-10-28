package com.example.s2tn.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract base class representing a puzzle in the game.
 * Each puzzle has an ID, title, state, hints, and optional item requirements or rewards.
 */
public abstract class Puzzle {

    private final UUID puzzleID = UUID.randomUUID();
    private String title;
    private PuzzleState state = PuzzleState.INIT;

    private final ArrayList<Hint> hints = new ArrayList<>();
    private int maxHints = 0;

    private String rewardItem;
    private boolean requiresItem = false;
    private String requiredItemKey;

    /** Returns the unique ID of this puzzle. */
    public UUID getPuzzleID() { return puzzleID; }

    /** Returns the puzzle's title. */
    public String getTitle() { return title; }

    /** Sets the puzzle's title. */
    public void setTitle(String title) { this.title = title; }

    /** Returns the current state of the puzzle. */
    public PuzzleState getState() { return state; }

    /** Sets the current state of the puzzle. */
    public void setState(PuzzleState state) { this.state = state == null ? PuzzleState.INIT : state; }

    /** Adds a hint to the puzzle. */
    public void addHint(Hint h) { if (h != null) hints.add(h); }

    /** Returns a list of all hints for this puzzle. */
    public List<Hint> getHints() { return hints; }

    /** Sets the maximum number of hints available for this puzzle. */
    public void setMaxHints(int n) { this.maxHints = Math.max(0, n); }

    /** Returns the maximum number of hints allowed. */
    public int getMaxHints() { return maxHints; }

    /** Returns the item rewarded upon solving this puzzle. */
    public String getRewardItem() { return rewardItem; }

    /** Sets the item rewarded upon solving this puzzle. */
    public void setRewardItem(String rewardItem) { this.rewardItem = rewardItem; }

    /** Returns true if the puzzle requires an item to solve. */
    public boolean isRequiresItem() { return requiresItem; }

    /** Sets whether the puzzle requires an item to solve. */
    public void setRequiresItem(boolean requiresItem) { this.requiresItem = requiresItem; }

    /** Returns the key of the item required to solve this puzzle. */
    public String getRequiredItemKey() { return requiredItemKey; }

    /** Sets the key of the item required to solve this puzzle. */
    public void setRequiredItemKey(String requiredItemKey) { this.requiredItemKey = requiredItemKey; }

    /**
     * Handles user input and returns a {@link ValidationResult}.
     *
     * @param input the player's input
     * @return the validation result
     */
    public abstract ValidationResult enterInput(String input);

    /**
     * Checks a specific achievement condition related to this puzzle.
     * (Currently unimplemented.)
     *
     * @param achievement the achievement to check
     * @param duration the time taken
     * @param hintsUsed number of hints used
     * @param currentScore player's current score
     * @return true if condition met, false otherwise
     */
    protected boolean checkSpecificAchievementCondition(Achievement achievement, Duration duration, int hintsUsed,
            int currentScore) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkSpecificAchievementCondition'");
    }
}
