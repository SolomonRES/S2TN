package com.s2tn.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProgressManagerTest {

    private ProgressManager progressManager;
    private Progress testProgress1;
    private Progress testProgress2;

    // Helper enum for PuzzleState (assuming it's defined elsewhere or as a nested type)
    enum PuzzleState { SOLVED, UNSOLVED, IN_PROGRESS }

    @BeforeEach
    void setUp() {
        // Clear static data before each test to ensure isolation
        ProgressManager.clearAllProgressForTesting();
        progressManager = new ProgressManager();

        // Initialize test progress objects
        testProgress1 = new Progress();
        testProgress1.setUserName("Alice");
        testProgress1.setDungeonID(UUID.randomUUID());
        testProgress1.setCurrentRoomID(UUID.randomUUID());
        testProgress1.setElapsedTime(12345L);
        Map<String, PuzzleState> puzzles1 = new HashMap<>();
        puzzles1.put("EntrancePuzzle", PuzzleState.SOLVED);
        puzzles1.put("CorridorRiddle", PuzzleState.UNSOLVED);
        testProgress1.setPuzzleState(puzzles1);
        // Do NOT set testProgress1.setSlot() here for tests that expect 'autosave' or specific slots

        testProgress2 = new Progress();
        testProgress2.setUserName("Bob");
        testProgress2.setDungeonID(UUID.randomUUID());
        testProgress2.setCurrentRoomID(UUID.randomUUID());
        testProgress2.setElapsedTime(67890L);
        Map<String, PuzzleState> puzzles2 = new HashMap<>();
        puzzles2.put("BossDoorLock", PuzzleState.IN_PROGRESS);
        testProgress2.setPuzzleState(puzzles2);
    }

    @AfterEach
    void tearDown() {
        // No need to clear static map here again if @BeforeEach does it.
        // Can be used for other cleanup if needed, but for simple tests, often not.
        progressManager = null;
        testProgress1 = null;
        testProgress2 = null;
    }

    @Test
    @DisplayName("saveProgress should use 'autosave' if progress slot is null or blank")
    void saveProgress_AutosaveDefault() {
        testProgress1.setSlot(null); // Explicitly ensure slot is null
        progressManager.saveProgress(testProgress1);

        Progress loaded = progressManager.load("autosave");
        assertNotNull(loaded, "Progress should be saved to 'autosave'.");
        assertEquals("Alice", loaded.getUserName(), "Loaded progress username should match.");
        assertEquals("autosave", loaded.getSlot(), "Loaded progress slot should be 'autosave'.");
        assertTrue(progressManager.listSlots().contains("autosave"), "Autosave slot should be listed.");
    }

    @Test
    @DisplayName("saveProgress should use the provided slot if progress slot is set")
    void saveProgress_SpecifiedSlot() {
        String customSlot = "myCustomSlot";
        testProgress1.setSlot(customSlot); // Set a custom slot
        progressManager.saveProgress(testProgress1);

        Progress loaded = progressManager.load(customSlot);
        assertNotNull(loaded, "Progress should be saved to the custom slot.");
        assertEquals("Alice", loaded.getUserName(), "Loaded progress username should match.");
        assertEquals(customSlot, loaded.getSlot(), "Loaded progress slot should be the custom slot.");
        assertFalse(progressManager.listSlots().contains("autosave"), "Autosave slot should not be present initially.");
    }

    @Test
    @DisplayName("saveProgress should ignore null progress object")
    void saveProgress_NullInput() {
        int initialSlotCount = progressManager.listSlots().size();
        progressManager.saveProgress(null);
        assertEquals(initialSlotCount, progressManager.listSlots().size(), "No new slots should be created for null progress.");
    }

    @Test
    @DisplayName("loadProgress should load the 'autosave' slot (regardless of user parameter)")
    void loadProgress_LoadsAutosave() {
        progressManager.save("autosave", testProgress1); // Manually save to autosave

        Progress loadedAlice = progressManager.loadProgress("Alice"); // UserName parameter is ignored by ProgressManager's loadProgress
        assertNotNull(loadedAlice, "loadProgress should return the autosaved progress.");
        assertEquals("Alice", loadedAlice.getUserName(), "Loaded progress should be for Alice.");
        assertEquals(testProgress1.getDungeonID(), loadedAlice.getDungeonID());
        assertEquals("autosave", loadedAlice.getSlot());

        Progress loadedBob = progressManager.loadProgress("Bob"); // Should still load Alice's autosave
        assertNotNull(loadedBob, "loadProgress should return the autosaved progress for Bob too.");
        assertEquals("Alice", loadedBob.getUserName(), "Loaded progress should still be Alice's autosave.");
    }

    @Test
    @DisplayName("loadProgress should return null if no 'autosave' exists")
    void loadProgress_NoAutosave() {
        Progress loaded = progressManager.loadProgress("anyUser");
        assertNull(loaded, "loadProgress should return null when 'autosave' slot is empty.");
    }

    @Test
    @DisplayName("save should correctly store progress under a given slot name")
    void save_NewSlot() {
        String slotName = "myNewGame";
        progressManager.save(slotName, testProgress1);

        Progress loaded = progressManager.load(slotName);
        assertNotNull(loaded, "Progress should be retrievable from the new slot.");
        assertEquals("Alice", loaded.getUserName());
        assertEquals(testProgress1.getDungeonID(), loaded.getDungeonID());
        assertEquals(testProgress1.getElapsedTime(), loaded.getElapsedTime());
        assertEquals(slotName, loaded.getSlot(), "Saved progress object should also have its slot set.");
        assertEquals(testProgress1.getPuzzleState().get("EntrancePuzzle"), loaded.getPuzzleState().get("EntrancePuzzle"));
        assertTrue(progressManager.listSlots().contains(slotName), "New slot should be in the list of available slots.");
    }

    @Test
    @DisplayName("save should overwrite existing progress for the same slot name")
    void save_OverwriteExistingSlot() {
        String slotName = "existingSlot";
        progressManager.save(slotName, testProgress1); // Save Alice's progress
        assertEquals("Alice", progressManager.load(slotName).getUserName());

        progressManager.save(slotName, testProgress2); // Overwrite with Bob's progress
        Progress loaded = progressManager.load(slotName);

        assertNotNull(loaded, "Overwritten progress should still be retrievable.");
        assertEquals("Bob", loaded.getUserName(), "Overwritten progress should reflect the new data.");
        assertEquals(testProgress2.getDungeonID(), loaded.getDungeonID());
        assertEquals(1, progressManager.listSlots().size(), "Overwriting should not increase the number of slots.");
    }

    @Test
    @DisplayName("save should handle null/blank slot names or null progress gracefully")
    void save_InvalidInputs() {
        int initialSlotCount = progressManager.listSlots().size();

        progressManager.save(null, testProgress1);
        progressManager.save("", testProgress1);
        progressManager.save("   ", testProgress1);
        progressManager.save("validSlot", null); // Should not save anything to "validSlot"

        assertEquals(initialSlotCount, progressManager.listSlots().size(), "No new slots should be created for invalid save calls.");
        assertNull(progressManager.load("validSlot"), "Attempt to load 'validSlot' after null progress save should yield null.");
    }

    @Test
    @DisplayName("load should retrieve a deep copy of the saved progress")
    void load_ReturnsDeepCopy() {
        String slotName = "deepCopySlot";
        progressManager.save(slotName, testProgress1);
        Progress loadedProgress = progressManager.load(slotName);

        assertNotNull(loadedProgress);
        assertNotSame(testProgress1, loadedProgress, "Loaded Progress object should be a different instance.");
        assertEquals(testProgress1.getUserName(), loadedProgress.getUserName(), "Usernames should be equal.");
        assertEquals(testProgress1.getDungeonID(), loadedProgress.getDungeonID(), "Dungeon IDs should be equal.");
        assertEquals(testProgress1.getElapsedTime(), loadedProgress.getElapsedTime(), "Elapsed times should be equal.");
        assertEquals(testProgress1.getSlot(), loadedProgress.getSlot(), "Slot names should be equal.");

        // Verify puzzle state map is also a copy
        assertNotNull(loadedProgress.getPuzzleState());
        assertNotSame(testProgress1.getPuzzleState(), loadedProgress.getPuzzleState(), "Puzzle state map should be a different instance.");
        assertEquals(testProgress1.getPuzzleState().size(), loadedProgress.getPuzzleState().size());
        assertEquals(testProgress1.getPuzzleState().get("EntrancePuzzle"), loadedProgress.getPuzzleState().get("EntrancePuzzle"));

        // Modify the loaded copy and ensure the original (in manager) is unaffected
        loadedProgress.setUserName("ModifiedAlice");
        loadedProgress.getPuzzleState().put("newPuzzle", PuzzleState.SOLVED);

        Progress originalAfterModification = progressManager.load(slotName);
        assertEquals("Alice", originalAfterModification.getUserName(), "Original username in manager should be unchanged.");
        assertFalse(originalAfterModification.getPuzzleState().containsKey("newPuzzle"), "Original puzzle state should be unchanged.");
    }

    @Test
    @DisplayName("load should return null for a non-existent slot")
    void load_NonExistentSlot() {
        assertNull(progressManager.load("imaginarySlot"), "Loading a non-existent slot should return null.");
    }

    @Test
    @DisplayName("load should return null for null or blank slot names")
    void load_InvalidSlotName() {
        assertNull(progressManager.load(null), "Loading with null slot should return null.");
        assertNull(progressManager.load(""), "Loading with empty slot should return null.");
        assertNull(progressManager.load("   "), "Loading with blank slot should return null.");
    }

    @Test
    @DisplayName("listSlots should return a list of all current save slot names")
    void listSlots_Populated() {
        progressManager.save("gameSlot1", testProgress1);
        progressManager.save("gameSlot2", testProgress2);
        progressManager.save("autosave", testProgress1); 

        List<String> slots = progressManager.listSlots();
        assertNotNull(slots, "List of slots should not be null.");
        assertEquals(3, slots.size(), "Three slots should be present.");
        assertTrue(slots.contains("gameSlot1"), "List should contain 'gameSlot1'.");
        assertTrue(slots.contains("gameSlot2"), "List should contain 'gameSlot2'.");
        assertTrue(slots.contains("autosave"), "List should contain 'autosave'.");
        assertNotSame(progressManager.listSlots(), slots, "listSlots should return a new list instance, not the internal one.");
    }

    @Test
    @DisplayName("listSlots should return an empty list if no progress is saved")
    void listSlots_Empty() {
        List<String> slots = progressManager.listSlots();
        assertNotNull(slots, "List of slots should not be null even when empty.");
        assertTrue(slots.isEmpty(), "List of slots should be empty if no progress is saved.");
    }

    @Test
    @DisplayName("delete should remove an existing progress slot")
    void delete_ExistingSlot() {
        String slotToDelete = "toDelete";
        progressManager.save(slotToDelete, testProgress1);
        assertTrue(progressManager.listSlots().contains(slotToDelete), "Slot should exist before deletion.");

        progressManager.delete(slotToDelete);
        assertNull(progressManager.load(slotToDelete), "Deleted slot should no longer be loadable.");
        assertFalse(progressManager.listSlots().contains(slotToDelete), "Deleted slot should not be in the list.");
        assertTrue(progressManager.listSlots().isEmpty(), "No other slots, so list should be empty.");
    }

    @Test
    @DisplayName("delete should do nothing for a non-existent slot")
    void delete_NonExistentSlot() {
        progressManager.save("existingOne", testProgress1);
        int initialSlotCount = progressManager.listSlots().size();

        progressManager.delete("nonExisting");
        assertEquals(initialSlotCount, progressManager.listSlots().size(), "Deleting non-existent slot should not change slot count.");
        assertNotNull(progressManager.load("existingOne"), "Existing slot should remain unaffected.");
    }

    @Test
    @DisplayName("delete should do nothing for null or blank slot names")
    void delete_InvalidSlotName() {
        progressManager.save("anotherExisting", testProgress1);
        int initialSlotCount = progressManager.listSlots().size();

        progressManager.delete(null);
        progressManager.delete("");
        progressManager.delete("   ");

        assertEquals(initialSlotCount, progressManager.listSlots().size(), "Deleting with invalid slot names should not change slot count.");
    }
}