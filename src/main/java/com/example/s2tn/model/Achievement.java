package com.example.s2tn.model;

import java.util.UUID;

/**
 * Represents an achievement a user can earn in the game.
 */
public class Achievement {

    /** Unique ID for this achievement */
    private UUID achievementId;

    /** Name of the achievement */
    private String name;

    /** Description of the achievement */
    private String description;

    /** Dungeon this achievement is linked to */
    private UUID dungeonId;

    /** Puzzle this achievement is linked to */
    private UUID puzzleId;

    /** Points awarded for unlocking this achievement */
    private int points;

    /** True if the achievement has been unlocked */
    private boolean unlocked;

    /**
     * Creates a new achievement.
     * @param name the name of the achievement
     * @param description the description of the achievement
     * @param dungeonId the ID of the related dungeon
     * @param puzzleId the ID of the related puzzle
     * @param points the points for unlocking it
     */
    public Achievement(String name, String description, UUID dungeonId, UUID puzzleId, int points) {
        this.achievementId = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.dungeonId = dungeonId;
        this.puzzleId = puzzleId;
        this.points = points;
        this.unlocked = false;
    }

    /**
     * Marks the achievement as unlocked.
     */
    public void unlock() {
        this.unlocked = true;
    }

    /**
     * Checks if the achievement is unlocked.
     * @return true if unlocked, false otherwise
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /** @return the achievement ID */
    public UUID getAchievementId() { return achievementId; }

    /** @return the name */
    public String getName() { return name; }

    /** @param name sets the name */
    public void setName(String name) { this.name = name; }

    /** @return the description */
    public String getDescription() { return description; }

    /** @param description sets the description */
    public void setDescription(String description) { this.description = description; }

    /** @return the dungeon ID */
    public UUID getDungeonId() { return dungeonId; }

    /** @param dungeonId sets the dungeon ID */
    public void setDungeonId(UUID dungeonId) { this.dungeonId = dungeonId; }

    /** @return the puzzle ID */
    public UUID getPuzzleId() { return puzzleId; }

    /** @param puzzleId sets the puzzle ID */
    public void setPuzzleId(UUID puzzleId) { this.puzzleId = puzzleId; }

    /** @return the points value */
    public int getPoints() { return points; }

    /** @param points sets the points value */
    public void setPoints(int points) { this.points = points; }

    /**
     * Returns a short text version of the achievement.
     * @return string summary of the achievement
     */
    @Override
    public String toString() {
        return "Achievement{" +
               "name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", unlocked=" + unlocked +
               '}';
    }
}
