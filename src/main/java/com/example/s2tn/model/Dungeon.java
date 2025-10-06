package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.UUID;

public class Dungeon {
    private String name;
    private ArrayList<Room> rooms;
    private Timer timer;
    private double baseMaxAllowedTime;
    private double maxTimeAllowed;
    private Difficulty difficulty;
    private UUID uuid;
    private Room currentRoom;
    private Room startingRoom;
    public Dungeon(Dungeon dungeon, Difficulty difficulty){
        //Will load a dungeon from the database
    }
    public double setMaxTimeAllowed(Difficulty difficulty, double baseMaxAllowedTime){
        return 0;
    }
    public void addTimePenalty(){
        //Will add time to clock for failures
    }
    public void changeRoom(Room room){
        // Set the current room in the dungeon
    }
}
