package com.s2tn.model;

import java.util.UUID;

/**
 * Represents an achievement earned by completing a puzzle or dungeon.
 */
public class Achievement {

    private String name;
    private String description;
    private UUID dungeonId; // ID of the dungeon this achievement is related to
    private UUID puzzleId; // ID of the puzzle this achievement is related to
    private int points; // Points awarded for unlocking this achievement
    private boolean unlocked; // True if the achievement has been unlocked

    /**
     * Constructor for the Achievement class.
     * 
     * @param name the name of the achievement
     * @param description a short description of the achievement
     * @param dungeonId the ID of the related dungeon
     * @param puzzleId the ID of the related puzzle
     * @param points the points awarded for unlocking the achievement
     */
    public Achievement(String name, String description, UUID dungeonId, UUID puzzleId, int points) {
        this.name = name;
        this.description = description;
        this.dungeonId = dungeonId;
        this.puzzleId = puzzleId;
        this.points = points;
        this.unlocked = false; // Achievements start locked
    }

    /**
     * Unlocks this achievement.
     */
    public void unlock() {
        this.unlocked = true;
    }

    /**
     * Checks if the achievement is unlocked.
     * 
     * @return true if unlocked, false otherwise
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    // Getters and Setters for all fields
    // public void setAchievementId(UUID achievementId) { this.achievementId = achievementId; }

    /**
     * Gets the name of the achievement.
     * 
     * @return the achievement name
     */
    public String getName() { return name; }

    /**
     * Sets the name of the achievement.
     * 
     * @param name the new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the description of the achievement.
     * 
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * Sets the description of the achievement.
     * 
     * @param description the new description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the ID of the related dungeon.
     * 
     * @return the dungeon ID
     */
    public UUID getDungeonId() { return dungeonId; }

    /**
     * Sets the ID of the related dungeon.
     * 
     * @param dungeonId the new dungeon ID
     */
    public void setDungeonId(UUID dungeonId) { this.dungeonId = dungeonId; }

    /**
     * Gets the ID of the related puzzle.
     * 
     * @return the puzzle ID
     */
    public UUID getPuzzleId() { return puzzleId; }

    /**
     * Sets the ID of the related puzzle.
     * 
     * @param puzzleId the new puzzle ID
     */
    public void setPuzzleId(UUID puzzleId) { this.puzzleId = puzzleId; }

    /**
     * Gets the number of points awarded for this achievement.
     * 
     * @return the points
     */
    public int getPoints() { return points; }

    /**
     * Sets the number of points for this achievement.
     * 
     * @param points the new point value
     */
    public void setPoints(int points) { this.points = points; }

    /**
     * Returns a string representation of the achievement.
     * 
     * @return a formatted string with achievement details
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
