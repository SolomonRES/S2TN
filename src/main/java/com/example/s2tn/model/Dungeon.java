package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(ArrayList<Room> rooms) { this.rooms = rooms; }

    public Timer getTimer() { return timer; }
    public void setTimer(Timer t) { this.timer = t; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty d) { this.difficulty = d; }

    public UUID getUUID() { return uuid; }

    public Room getCurrentRoom() { return currentRoom; }
    public Room getStartingRoom() { return startingRoom; }
    public Room getPreviousRoom() { return previousRoom; }

    public void changeRoom(Room next) {
        if (next == null) return;
        if (!rooms.contains(next)) return;
        this.previousRoom = this.currentRoom;
        this.currentRoom = next;
    }

    @Override
    public String toString() {
        return "Dungeon{" + "uuid=" + uuid + ", name='" + name + '\'' +
               ", rooms=" + (rooms == null ? 0 : rooms.size()) +
               ", difficulty=" + difficulty + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dungeon d)) return false;
        return Objects.equals(uuid, d.uuid);
    }

    @Override
    public int hashCode() { return Objects.hash(uuid); }
}
