package com.s2tn.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataConstantsTest {

    private static final String S2TN_DIR = "S2TN";
    private static final String JSON_DIR = "json";
    private static final String USERS_FILE = "users.json";
    private static final String ROOMS_FILE = "rooms.json";

    @Test
    @DisplayName("usersPath() should return the correct path for users.json")
    void testUsersPath() {
        Path expectedPath = Paths.get(S2TN_DIR, JSON_DIR, USERS_FILE);
        Path actualPath = DataConstants.usersPath();

        assertNotNull(actualPath, "The usersPath should not return null.");
        assertEquals(expectedPath, actualPath, "The path for users.json is incorrect.");
        assertTrue(actualPath.endsWith(Paths.get(JSON_DIR, USERS_FILE)), 
                   "The usersPath should end with json/users.json.");
    }

    @Test
    @DisplayName("dungeonPath() should return the correct path for rooms.json")
    void testDungeonPath() {
        Path expectedPath = Paths.get(S2TN_DIR, JSON_DIR, ROOMS_FILE);
        Path actualPath = DataConstants.dungeonPath();

        assertNotNull(actualPath, "The dungeonPath should not return null.");
        assertEquals(expectedPath, actualPath, "The path for rooms.json is incorrect.");
        assertTrue(actualPath.endsWith(Paths.get(JSON_DIR, ROOMS_FILE)), 
                   "The dungeonPath should end with json/rooms.json.");
    }

    @Test
    @DisplayName("lbPath() should return the correct path for leaderboard users.json")
    void testLbPath() {
        Path expectedPath = Paths.get(S2TN_DIR, JSON_DIR, USERS_FILE);
        Path actualPath = DataConstants.lbPath();

        assertNotNull(actualPath, "The lbPath should not return null.");
        assertEquals(expectedPath, actualPath, "The path for lb users.json is incorrect.");
        assertTrue(actualPath.endsWith(Paths.get(JSON_DIR, USERS_FILE)), 
                   "The lbPath should end with json/users.json.");
    }
}