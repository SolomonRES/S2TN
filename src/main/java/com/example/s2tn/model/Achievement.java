package com.example.s2tn.model;

import java.util.UUID;

public class Achievement {

    private UUID achievementId;
    private String name;
    private String description;
    private UUID dungeonId;
    private UUID puzzleId;
    private int points;
    private boolean unlocked;

    public void unlock() {
        unlocked = true;
    }

    public boolean isUnlocked() {
        return unlocked;
    }
}
