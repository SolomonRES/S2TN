package com.example.s2tn.model;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DataConstants {
    
    // public static final String userDir = "../../../json/users.json";
    // public static final String dungeonDir = "../../../json/rooms.json";
    // public static final String lbFile = "../../../json/users.json";

    public static Path usersPath() {
        return Paths.get("S2TN", "json", "users.json");
    }

    public static Path dungeonPath() {
        return Paths.get("S2TN","json", "rooms.json");
    }

    public static Path lbPath() {
        return Paths.get("S2TN","json", "users.json");
    }
}
