package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Progress {

    private String userName;
    private String dungeonID;
    private String currentRoomID;
    private Map<String, String> puzzleState; 
    private long elapsedTime;
    private String slot; // Name of the save slot

    // --- Static storage for all saved progress objects ---
    private static final Map<String, Progress> savedProgress = new HashMap<>();

    /**
     * Default constructor for new game progress.
     */
    public Progress() {
        this.userName = "";
        this.dungeonID = "";
        this.currentRoomID = "";
        this.puzzleState = new HashMap<>(); 
        this.elapsedTime = 0L;
        this.slot = "";
    }

    public String getUserName() {
        return userName;
    }

    public String getDungeonID() {
        return dungeonID;
    }

    public String getCurrentRoomID() {
        return currentRoomID;
    }

    public Map<String, String> getPuzzleState() {
        return new HashMap<>(puzzleState);
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public String getSlot() {
        return slot;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDungeonID(String dungeonID) {
        this.dungeonID = dungeonID;
    }

    public void setCurrentRoomID(String currentRoomID) {
        this.currentRoomID = currentRoomID;
    }

    public void setPuzzleState(Map<String, String> puzzleState) {
        this.puzzleState = new HashMap<>(puzzleState);
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    // --- Core Persistence Methods ---

    /**
     * Saves the current state of this Progress object to the specified slot.
     * @param slot The name of the save slot.
     */
    public void progress(String slot) {
        // Update the slot name within the object being saved
        this.setSlot(slot);

        // Create a copy of the current Progress object to store
        Progress progressToSave = new Progress();
        progressToSave.setUserName(this.getUserName());
        progressToSave.setDungeonID(this.getDungeonID());
        progressToSave.setCurrentRoomID(this.getCurrentRoomID());
        progressToSave.setPuzzleState(this.getPuzzleState()); // getPuzzleState returns a copy
        progressToSave.setElapsedTime(this.getElapsedTime());
        progressToSave.setSlot(slot); // Ensure the slot name is consistent

        savedProgress.put(slot, progressToSave);
        System.out.println("Progress saved to slot: '" + slot + "' for user: " + this.userName);
    }

    public static Progress load(String slot) {
        Progress storedProgress = savedProgress.get(slot);
        if (storedProgress != null) {
            Progress loadedProgress = new Progress();
            loadedProgress.setUserName(storedProgress.getUserName());
            loadedProgress.setDungeonID(storedProgress.getDungeonID());
            loadedProgress.setCurrentRoomID(storedProgress.getCurrentRoomID());
            loadedProgress.setPuzzleState(storedProgress.getPuzzleState()); // getPuzzleState returns a copy
            loadedProgress.setElapsedTime(storedProgress.getElapsedTime());
            loadedProgress.setSlot(storedProgress.getSlot()); // Set the slot name to what was loaded

            System.out.println("Progress loaded from slot: '" + slot + "' for user: " + loadedProgress.getUserName());
            return loadedProgress;
        }
        System.out.println("No progress found for slot: '" + slot + "'.");
        return null; 
    }

    /**
     * Lists all available save slots.
     * @return An unmodifiable list of slot names.
     */

    public static List<String> listSlots() {
        if (savedProgress.isEmpty()) {
            System.out.println("No saved progress slots found.");
            return Collections.emptyList();
        }
        List<String> slots = new ArrayList<>(savedProgress.keySet());
        System.out.println("Available slots: " + slots);
        return Collections.unmodifiableList(slots); // Return an unmodifiable list
    }

    /**
     * Deletes the progress saved in the specified slot.
     * @param slot The name of the slot to delete.
     */

    public static void delete(String slot) {
        if (savedProgress.remove(slot) != null) {
            System.out.println("Progress deleted for slot: '" + slot + "'.");
        } else {
            System.out.println("No progress found to delete for slot: '" + slot + "'.");
        }
    }

    /**
     * Overrides the default toString method to provide a meaningful string representation of the Progress object.
     */
    
    @Override
    public String toString() {
        return "Progress{" +
               "userName='" + userName + '\'' +
               ", dungeonID='" + dungeonID + '\'' +
               ", currentRoomID='" + currentRoomID + '\'' +
               ", puzzleState=" + puzzleState +
               ", elapsedTime=" + elapsedTime +
               ", slot='" + slot + '\'' +
               '}';
    }
}