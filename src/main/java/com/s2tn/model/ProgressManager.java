package com.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages saving, loading, and deleting player progress.
 * Stores all saved progress in memory using save slots.
 */
public class ProgressManager {

    private static final Map<String, Progress> savedProgress = new HashMap<>();

    /**
     * Saves the given progress using its assigned slot or "autosave" if none is set.
     *
     * @param progress the progress to save
     */
    public void saveProgress(Progress progress) {
        if (progress == null) return;
        String slot = (progress.getSlot() == null || progress.getSlot().isBlank())
                ? "autosave" : progress.getSlot();
        save(slot, progress);
    }

    /**
     * Loads the most recent autosave progress for a user.
     *
     * @param userName the username to load progress for
     * @return the loaded progress, or null if none exists
     */
    public Progress loadProgress(String userName) {
        return load("autosave");
    }

    /**
     * Saves a copy of the given progress to the specified slot.
     *
     * @param slot the save slot name
     * @param progress the progress data to save
     */
    public void save(String slot, Progress progress) {
        if (slot == null || slot.isBlank() || progress == null) return;

        Progress copy = new Progress();
        copy.setUserName(progress.getUserName());
        copy.setDungeonID(progress.getDungeonID());
        copy.setCurrentRoomID(progress.getCurrentRoomID());
        copy.setPuzzleState(new HashMap<>(progress.getPuzzleState()));  // shallow copy OK (values are enums)
        copy.setElapsedTime(progress.getElapsedTime());
        copy.setSlot(slot);

        savedProgress.put(slot, copy);
        System.out.println("Progress saved to slot: " + slot);
    }

    /**
     * Loads progress data from a specified slot.
     *
     * @param slot the save slot name
     * @return a copy of the saved progress, or null if not found
     */
    public Progress load(String slot) {
        if (slot == null || slot.isBlank()) return null;
        Progress saved = savedProgress.get(slot);
        if (saved == null) {
            System.out.println("No progress found for slot: " + slot);
            return null;
        }
        
        Progress copy = new Progress();
        copy.setUserName(saved.getUserName());
        copy.setDungeonID(saved.getDungeonID());
        copy.setCurrentRoomID(saved.getCurrentRoomID());
        copy.setPuzzleState(new HashMap<>(saved.getPuzzleState()));
        copy.setElapsedTime(saved.getElapsedTime());
        copy.setSlot(saved.getSlot());

        System.out.println("Progress loaded from slot: " + slot);
        return copy;
    }

    /**
     * Returns a list of all saved slot names.
     *
     * @return list of available save slots
     */
    public List<String> listSlots() {
        return new ArrayList<>(savedProgress.keySet());
    }

    /**
     * Deletes the progress saved in the specified slot, if it exists.
     *
     * @param slot the save slot name
     */
    public void delete(String slot) {
        if (slot == null || slot.isBlank()) return;
        if (savedProgress.remove(slot) != null) {
            System.out.println("Progress deleted for slot: " + slot);
        } else {
            System.out.println("No progress found to delete for slot: " + slot);
        }
    }
}
