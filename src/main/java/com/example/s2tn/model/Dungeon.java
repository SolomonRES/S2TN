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
    private Room previousRoom;
    private Map map;

    public java.util.UUID getUUID() { return uuid; }

    public Dungeon(String name, ArrayList<Room> rooms, double baseMaxAllowedTime, Difficulty difficulty, Room startingRoom){
        this.name = name;
        this.rooms = rooms;
        this.timer = new Timer();
        this.baseMaxAllowedTime = baseMaxAllowedTime;
        this.maxTimeAllowed = setMaxTimeAllowed(difficulty, baseMaxAllowedTime);
        this.difficulty = difficulty;
        this.startingRoom = startingRoom;
        this.currentRoom = startingRoom;
        this.uuid = UUID.randomUUID();
        this.map = new Map(this);
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.maxTimeAllowed = setMaxTimeAllowed(difficulty, baseMaxAllowedTime);
    }

    public double setMaxTimeAllowed(Difficulty difficulty, double baseMaxAllowedTime){
        switch (difficulty){
            case EASY -> { return baseMaxAllowedTime * 2; }
            case NORMAL -> { return baseMaxAllowedTime; }
            case HARD -> { return baseMaxAllowedTime * 0.5; }
        }
        return baseMaxAllowedTime;
    }

    public void addTimePenalty(Timer timer){
        timer.addPenalty(15000);
    }

    public void changeRoom(Room room){
        boolean isUnlocked = true;
        if (currentRoom != null) {
            for (Room locked : currentRoom.getLockedExits()) {
                if (locked == room) {
                    isUnlocked = false;
                    break;
                }
            }
        }
        if (isUnlocked){
            previousRoom = currentRoom;
            this.currentRoom = room;
        }
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public Timer getTimer() {
        return timer;
    }

    public Map getMap() {
        return map;
    }

    public Room getPreviousRoom() {
        return previousRoom;
    }

    public Room getCurrentRoom() {
        return currentRoom != null ? currentRoom : getStartingRoom();
    }

    public Room getStartingRoom() {
        return startingRoom != null ? startingRoom : (rooms == null || rooms.isEmpty() ? null : rooms.get(0));
    }

    public Difficulty getDifficulty() {
    return difficulty;
    }

    public String getName() {
        return name;
    }

    public double getMaxTimeAllowed() {
        return maxTimeAllowed;
    }
}
