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
    private ArrayList<Interactable> inventory;
    public Dungeon(Dungeon dungeon, Difficulty difficulty){

    }
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
        inventory = new ArrayList<>();
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
            previousRoom = currentRoom;
            this.currentRoom = room;
        }
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public Room getCurrentRoom() {
        return this.currentRoom;
    }

    public Map getMap() {
        return this.map;
    }

    public Room getStartingRoom() {
        return startingRoom;
    }

    public Room getPreviousRoom() {
        return previousRoom;
    }

    public Timer getTimer() {
        return timer;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        setMaxTimeAllowed(difficulty, baseMaxAllowedTime);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}
