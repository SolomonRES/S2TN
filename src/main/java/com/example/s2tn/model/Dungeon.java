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
    }
    public double setMaxTimeAllowed(Difficulty difficulty, double baseMaxAllowedTime){
        switch (difficulty){
            case EASY -> {
                return baseMaxAllowedTime*2;
            }
            case NORMAL -> {
                return baseMaxAllowedTime;
            }
            case HARD -> {
                return baseMaxAllowedTime*0.5;
            }
        }
        return baseMaxAllowedTime;
    }
    public void addTimePenalty(Timer timer){
        timer.addPenalty(15000);
    }
    public String changeRoom(Exit exit){
        // Set the current room in the dungeon
        boolean canExit = true;
        if(currentRoom == exit.getExitFrom()){
            for(Puzzle areSolved : exit.getRequiredPuzzles()){
                if(areSolved.getState() != PuzzleState.SOLVED){
                    canExit = false;
                }
            }
        }
        if(canExit){
            this.currentRoom = exit.getExitTo();
            return "";
        }else{
            //Print out the locked text
            return exit.getLockText();
        }
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }
}
