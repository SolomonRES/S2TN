package com.s2tn.model;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AchievementTest {

    private Achievement achievement;
    private UUID testDungeonId;
    private UUID testPuzzleId;

    @BeforeEach
    void setUp() {
        testDungeonId = UUID.randomUUID();
        testPuzzleId = UUID.randomUUID();
        achievement = new Achievement("First Step", "You started the game.", testDungeonId, testPuzzleId, 10);
    }

    @AfterEach
    void tearDown() {
        achievement = null;
        testDungeonId = null;
        testPuzzleId = null;
    }

    @Test
    @DisplayName("Constructor should initialize all fields correctly and be locked by default")
    void testConstructor() {
        assertEquals("First Step", achievement.getName());
        assertEquals("You started the game.", achievement.getDescription());
        assertEquals(testDungeonId, achievement.getDungeonId());
        assertEquals(testPuzzleId, achievement.getPuzzleId());
        assertEquals(10, achievement.getPoints());
        assertFalse(achievement.isUnlocked(), "Achievement should be locked by default.");
    }

    @Test
    @DisplayName("Constructor should handle null name and description by setting them to null")
    void testConstructorWithNullStrings() {
        Achievement nullAchievement = new Achievement(null, null, testDungeonId, testPuzzleId, 5);
        assertNull(nullAchievement.getName());
        assertNull(nullAchievement.getDescription());
    }

    @Test
    @DisplayName("Constructor should handle null UUIDs")
    void testConstructorWithNullUUIDs() {
        Achievement nullUUIDAchievement = new Achievement("Test", "Desc", null, null, 5);
        assertNull(nullUUIDAchievement.getDungeonId());
        assertNull(nullUUIDAchievement.getPuzzleId());
    }

    @Test
    @DisplayName("unlock() should change the unlocked status to true")
    void unlock() {
        assertFalse(achievement.isUnlocked());
        achievement.unlock();
        assertTrue(achievement.isUnlocked(), "Achievement should be unlocked after calling unlock().");
    }

    @Test
    @DisplayName("isUnlocked() should return the correct unlocked status")
    void isUnlocked() {
        assertFalse(achievement.isUnlocked(), "Initially, achievement should be locked.");
        achievement.unlock();
        assertTrue(achievement.isUnlocked(), "After unlock(), achievement should be unlocked.");
    }

    @Test
    @DisplayName("getName() and setName() should work correctly")
    void testGetNameAndSetName() {
        assertEquals("First Step", achievement.getName());
        achievement.setName("New Name");
        assertEquals("New Name", achievement.getName());
        achievement.setName(null);
        assertNull(achievement.getName());
    }

    @Test
    @DisplayName("getDescription() and setDescription() should work correctly")
    void testGetDescriptionAndSetDescription() {
        assertEquals("You started the game.", achievement.getDescription());
        achievement.setDescription("New Description");
        assertEquals("New Description", achievement.getDescription());
        achievement.setDescription(null);
        assertNull(achievement.getDescription());
    }

    @Test
    @DisplayName("getDungeonId() and setDungeonId() should work correctly")
    void testGetDungeonIdAndSetDungeonId() {
        assertEquals(testDungeonId, achievement.getDungeonId());
        UUID newDungeonId = UUID.randomUUID();
        achievement.setDungeonId(newDungeonId);
        assertEquals(newDungeonId, achievement.getDungeonId());
        achievement.setDungeonId(null);
        assertNull(achievement.getDungeonId());
    }

    @Test
    @DisplayName("getPuzzleId() and setPuzzleId() should work correctly")
    void testGetPuzzleIdAndSetPuzzleId() {
        assertEquals(testPuzzleId, achievement.getPuzzleId());
        UUID newPuzzleId = UUID.randomUUID();
        achievement.setPuzzleId(newPuzzleId);
        assertEquals(newPuzzleId, achievement.getPuzzleId());
        achievement.setPuzzleId(null);
        assertNull(achievement.getPuzzleId());
    }

    @Test
    @DisplayName("getPoints() and setPoints() should work correctly and clamp negative values to zero")
    void testGetPointsAndSetPoints() {
        assertEquals(10, achievement.getPoints());
        achievement.setPoints(100);
        assertEquals(100, achievement.getPoints());
        achievement.setPoints(0);
        assertEquals(0, achievement.getPoints());
        achievement.setPoints(-50); // Should be clamped to 0
        assertEquals(0, achievement.getPoints(), "Points should not be negative.");
    }

    @Test
    @DisplayName("toString() should return a meaningful string representation")
    void testToString() {
        String expected = "Achievement{name='First Step', description='You started the game.', unlocked=false}";
        assertEquals(expected, achievement.toString());

        achievement.unlock();
        String expectedUnlocked = "Achievement{name='First Step', description='You started the game.', unlocked=true}";
        assertEquals(expectedUnlocked, achievement.toString());
    }

    @Test
    @DisplayName("Demonstrate that equals() and hashCode() are not overridden (critical for Account.addAchievements)")
    void testEqualsAndHashCodeDefaultBehavior() {
        // Create an achievement with the exact same properties but a different object instance
        Achievement sameLogicalAchievement = new Achievement("First Step", "You started the game.", testDungeonId, testPuzzleId, 10);

        // By default, equals() checks for object identity (same memory address)
        assertNotSame(achievement, sameLogicalAchievement); // They are different objects
        assertNotEquals(achievement, sameLogicalAchievement, "Without overriding equals(), these logically identical objects are considered different.");

        // This implies that Account.addAchievements(sameLogicalAchievement) would add a duplicate
        // if 'achievement' was already in the list, which is likely not the desired behavior.
    }
}