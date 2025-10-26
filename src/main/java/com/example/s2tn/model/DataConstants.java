package com.example.s2tn.model;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides constant file path methods for accessing JSON data files used in the application.
 */
public class DataConstants {
    
    // public static final String userDir = "../../../json/users.json";
    // public static final String dungeonDir = "../../../json/rooms.json";
    // public static final String lbFile = "../../../json/users.json";

    /**
     * Returns the path to the users.json file.
     * 
     * @return Path to users.json
     */
    public static Path usersPath() {
        return Paths.get("S2TN", "json", "users.json");
    }

    /**
     * Returns the path to the rooms.json file (dungeon data).
     * 
     * @return Path to rooms.json
     */
    public static Path dungeonPath() {
        return Paths.get("S2TN","json", "rooms.json");
    }

    /**
     * Returns the path to the leaderboard users.json file.
     * 
     * @return Path to users.json for leaderboard data
     */
    public static Path lbPath() {
        return Paths.get("S2TN","json", "users.json");
    }
}
