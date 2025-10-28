package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a dungeon containing rooms, a timer, and a difficulty level.
 */
public class Dungeon {

    private final UUID uuid = UUID.randomUUID();
    private String name;
    private ArrayList<Room> rooms;
    private Timer timer;
    private Difficulty difficulty;
    private Room currentRoom;
    @SuppressWarnings("FieldMayBeFinal")
    private Room startingRoom;
    private Room previousRoom;

    /** Creates a dungeon with the given name, rooms, timer, and difficulty. */
    public Dungeon(String name,
                   ArrayList<Room> rooms,
                   double baseMaxAllowedTimeMillis,
                   Difficulty difficulty,
                   Room startingRoom) {
        this.name = name;
        this.rooms = (rooms == null) ? new ArrayList<>() : rooms;
        this.timer = new Timer();
        this.difficulty = (difficulty == null) ? Difficulty.NORMAL : difficulty;
        this.startingRoom = startingRoom;
        this.currentRoom = null;
        this.previousRoom = null;
    }

    /** Returns the dungeon name. */
    public String getName() { return name; }

    /** Sets the dungeon name. */
    public void setName(String n) { this.name = n; }

    /** Returns the list of rooms in this dungeon. */
    public List<Room> getRooms() { return rooms; }

    /** Sets the rooms for this dungeon. */
    public void setRooms(ArrayList<Room> rooms) { this.rooms = rooms; }

    /** Returns the dungeon timer. */
    public Timer getTimer() { return timer; }

    /** Sets the dungeon timer. */
    public void setTimer(Timer t) { this.timer = t; }

    /** Returns the dungeon difficulty. */
    public Difficulty getDifficulty() { return difficulty; }

    /** Sets the dungeon difficulty. */
    public void setDifficulty(Difficulty d) { this.difficulty = d; }

    /** Returns the unique ID for this dungeon. */
    public UUID getUUID() { return uuid; }

    /** Returns the current room the player is in. */
    public Room getCurrentRoom() { return currentRoom; }

    /** Returns the starting room of the dungeon. */
    public Room getStartingRoom() { return startingRoom; }

    /** Returns the previously visited room. */
    public Room getPreviousRoom() { return previousRoom; }

    /** Changes the current room to the specified one, if it exists in the dungeon. */
    public void changeRoom(Room next) {
        if (next == null) return;
        if (!rooms.contains(next)) return;
        this.previousRoom = this.currentRoom;
        this.currentRoom = next;
    }

    /** Returns a string representation of the dungeon. */
    @Override
    public String toString() {
        return "Dungeon{" + "uuid=" + uuid + ", name='" + name + '\'' +
               ", rooms=" + (rooms == null ? 0 : rooms.size()) +
               ", difficulty=" + difficulty + '}';
    }

    /** Compares two dungeons based on their UUID. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dungeon d)) return false;
        return Objects.equals(uuid, d.uuid);
    }

    /** Returns the hash code based on the UUID. */
    @Override
    public int hashCode() { return Objects.hash(uuid); }
}