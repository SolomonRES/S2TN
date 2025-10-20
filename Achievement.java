package com.example.s2tn.model;

import java.util.UUID;

public class Achievement {

    private UUID achievementId;
    private String name;
    private String description;
    private UUID dungeonId; // ID of the dungeon this achievement is related to
    private UUID puzzleId; // ID of the puzzle this achievement is related to
    private int points; // Points awarded for unlocking this achievement
    private boolean unlocked; // True if the achievement has been unlocked

    // Constructor
    public Achievement(String name, String description, UUID dungeonId, UUID puzzleId, int points) {
        this.achievementId = UUID.randomUUID(); // Generate a unique ID for the achievement
        this.name = name;
        this.description = description;
        this.dungeonId = dungeonId;
        this.puzzleId = puzzleId;
        this.points = points;
        this.unlocked = false; // Achievements start locked
    }

    public void unlock() {
        this.unlocked = true;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    // Getters and Setters for all fields
    public UUID getAchievementId() { return achievementId; }
    // public void setAchievementId(UUID achievementId) { this.achievementId = achievementId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getDungeonId() { return dungeonId; }
    public void setDungeonId(UUID dungeonId) { this.dungeonId = dungeonId; }

    public UUID getPuzzleId() { return puzzleId; }
    public void setPuzzleId(UUID puzzleId) { this.puzzleId = puzzleId; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    @Override
    public String toString() {
        return "Achievement{" +
               "name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", unlocked=" + unlocked +
               '}';
    }
}