package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressManager {
    // This is made to store and save loading from the progress class.
    private static final Map<String, Progress> savedProgress = new HashMap<>();

    public void save(String slot, Progress progress) {
        // Here, this will make a defensive copy to make sure that modifications to the
        // original progress object don't affect the saved one.
        Progress copy = new Progress();
        copy.setUserName(progress.getUserName());
        copy.setDungeonID(progress.getDungeonID());
        copy.setCurrentRoomID(progress.getCurrentRoomID());
        copy.setPuzzleState(new HashMap<>(progress.getPuzzleState())); // Deep copy of map
        copy.setElapsedTime(progress.getElapsedTime());
        copy.setSlot(slot); // Make the slot name saved with the progress

        savedProgress.put(slot, copy);
        System.out.println("Progress saved to slot: " + slot);
    }

    public Progress load(String slot) {
        Progress loadedProgress = savedProgress.get(slot);
        if (loadedProgress != null) {
    
            Progress copy = new Progress();
            copy.setUserName(loadedProgress.getUserName());
            copy.setDungeonID(loadedProgress.getDungeonID());
            copy.setCurrentRoomID(loadedProgress.getCurrentRoomID());
            copy.setPuzzleState(new HashMap<>(loadedProgress.getPuzzleState()));
            copy.setElapsedTime(loadedProgress.getElapsedTime());
            copy.setSlot(loadedProgress.getSlot());
            System.out.println("Progress loaded from slot: " + slot);
            return copy;
        }
        System.out.println("No progress found for slot: " + slot);
        return null; // Or throw an exception
    }

    public List<String> listSlots() {
        return new ArrayList<>(savedProgress.keySet());
    }

    public void delete(String slot) {
        if (savedProgress.remove(slot) != null) {
            System.out.println("Progress deleted for slot: " + slot);
        } else {
            System.out.println("No progress found to delete for slot: " + slot);
        }
    }
}