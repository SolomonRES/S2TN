package com.example.s2tn.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a player's progress within a dungeon,
 * including puzzle states, current room, and elapsed time.
 */
public class Progress {

    private String userName;
    private UUID dungeonID;
    private UUID currentRoomID;
    private Map<String, PuzzleState> puzzleState = new HashMap<>();
    private long elapsedTime;  // milli
    private String slot;

    /** Returns the username associated with this progress. */
    public String getUserName() { return userName; }

    /** Sets the username for this progress. */
    public void setUserName(String userName) { this.userName = userName; }

    /** Returns the ID of the current dungeon. */
    public UUID getDungeonID() { return dungeonID; }

    /** Sets the ID of the current dungeon. */
    public void setDungeonID(UUID dungeonID) { this.dungeonID = dungeonID; }

    /** Returns the ID of the current room. */
    public UUID getCurrentRoomID() { return currentRoomID; }

    /** Sets the ID of the current room. */
    public void setCurrentRoomID(UUID currentRoomID) { this.currentRoomID = currentRoomID; }

    /** Returns the map of puzzle states for this progress. */
    public Map<String, PuzzleState> getPuzzleState() { return puzzleState; }

    /** Sets the map of puzzle states, replacing null with an empty map. */
    public void setPuzzleState(Map<String, PuzzleState> puzzleState) {
        this.puzzleState = (puzzleState == null) ? new HashMap<>() : puzzleState;
    }

    /** Returns the elapsed time in milliseconds. */
    public long getElapsedTime() { return elapsedTime; }

    /** Sets the elapsed time in milliseconds. */
    public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }

    /** Returns the save slot identifier. */
    public String getSlot() { return slot; }

    /** Sets the save slot identifier. */
    public void setSlot(String slot) { this.slot = slot; }
}
