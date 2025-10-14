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
        switch (difficulty){
            case EASY -> {
                return baseMaxAllowedTime * 2;
            }
            case NORMAL -> {
                return baseMaxAllowedTime;
            }
            case HARD -> {
                return baseMaxAllowedTime * 0.5;
            }
        }
        return  baseMaxAllowedTime;
    }
    public void addTimePenalty(Timer timer){
        timer.addPenalty(15000);
    }
    public void changeRoom(Room room){
        boolean isUnlocked = true;
        for(Room checkLock : room.getLockedExits()){
            if(checkLock == room){
                isUnlocked = false;
            }
        }
        if(isUnlocked){
            this.currentRoom = room;
        }
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
}
