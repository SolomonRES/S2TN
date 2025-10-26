package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressManager {

    private static final Map<String, Progress> savedProgress = new HashMap<>();

    public void saveProgress(Progress progress) {
        if (progress == null) return;
        String slot = (progress.getSlot() == null || progress.getSlot().isBlank())
                ? "autosave" : progress.getSlot();
        save(slot, progress);
    }

    public Progress loadProgress(String userName) {
        return load("autosave");
    }

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

    public List<String> listSlots() {
        return new ArrayList<>(savedProgress.keySet());
    }

    public void delete(String slot) {
        if (slot == null || slot.isBlank()) return;
        if (savedProgress.remove(slot) != null) {
            System.out.println("Progress deleted for slot: " + slot);
        } else {
            System.out.println("No progress found to delete for slot: " + slot);
        }
    }
}
