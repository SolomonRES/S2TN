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
        return 0;
    }
    public void addTimePenalty(){
        //Will add time to clock for failures
    }
    public void changeRoom(Exit exit){
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
        }else{
            //Print out the locked text
        }
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }
}
