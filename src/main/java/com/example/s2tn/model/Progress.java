package com.example.s2tn.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Progress {

    private String userName;
    private UUID dungeonID;
    private UUID currentRoomID;
    private Map<String, PuzzleState> puzzleState = new HashMap<>();
    private long elapsedTime;  // milli
    private String slot;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public UUID getDungeonID() { return dungeonID; }
    public void setDungeonID(UUID dungeonID) { this.dungeonID = dungeonID; }

    public UUID getCurrentRoomID() { return currentRoomID; }
    public void setCurrentRoomID(UUID currentRoomID) { this.currentRoomID = currentRoomID; }

    public Map<String, PuzzleState> getPuzzleState() { return puzzleState; }
    public void setPuzzleState(Map<String, PuzzleState> puzzleState) {
        this.puzzleState = (puzzleState == null) ? new HashMap<>() : puzzleState;
    }

    public long getElapsedTime() { return elapsedTime; }
    public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
}
