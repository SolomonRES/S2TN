package com.example.s2tn.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Puzzle {

    private final UUID puzzleID = UUID.randomUUID();
    private String title;
    private PuzzleState state = PuzzleState.INIT;

    private final ArrayList<Hint> hints = new ArrayList<>();
    private int maxHints = 0;

    private String rewardItem;
    private boolean requiresItem = false;
    private String requiredItemKey;

    public UUID getPuzzleID() { return puzzleID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public PuzzleState getState() { return state; }
    public void setState(PuzzleState state) { this.state = state == null ? PuzzleState.INIT : state; }

    public void addHint(Hint h) { if (h != null) hints.add(h); }
    public List<Hint> getHints() { return hints; }
    public void setMaxHints(int n) { this.maxHints = Math.max(0, n); }
    public int getMaxHints() { return maxHints; }

    public String getRewardItem() { return rewardItem; }
    public void setRewardItem(String rewardItem) { this.rewardItem = rewardItem; }

    public boolean isRequiresItem() { return requiresItem; }
    public void setRequiresItem(boolean requiresItem) { this.requiresItem = requiresItem; }

    public String getRequiredItemKey() { return requiredItemKey; }
    public void setRequiredItemKey(String requiredItemKey) { this.requiredItemKey = requiredItemKey; }

    public abstract ValidationResult enterInput(String input);

    protected boolean checkSpecificAchievementCondition(Achievement achievement, Duration duration, int hintsUsed,
            int currentScore) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkSpecificAchievementCondition'");
    }
}
