package com.s2tn.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Singleton class that manages all dungeons in the system.
 */
public class DungeonList {
    private static DungeonList instance;
    private static List<Dungeon> dungeons;

    /** Initializes an empty dungeon list (private constructor for singleton). */
    private DungeonList() {
        DungeonList.dungeons = new ArrayList<>();
    }

    /** Returns the singleton instance of the DungeonList. */
    public static DungeonList getInstance() {
        if (instance == null) {
            instance = new DungeonList();
        }
        return instance;
    }

    /** Returns a copy of all dungeons. */
    public List<Dungeon> getAll() {
        return new ArrayList<>(dungeons);
    }

    /** Returns a dungeon by its unique ID, or null if not found. */
    public Dungeon getById(UUID id) {
        if (id == null) return null;
        for (Dungeon d : dungeons) {
            if (id.equals(d.getUUID())) return d;
        }
        return null;
    }

    /** Returns a dungeon by its name, or null if not found. */
    public Dungeon getByName(String name) {
        if (name == null) return null;
        for (Dungeon d : dungeons) {
            if (name.equalsIgnoreCase(d.getName())) return d;
        }
        return null;
    }

    /** Replaces all existing dungeons with a new list. */
    public void replaceAll(List<Dungeon> newOnes) {
        dungeons.clear();
        if (newOnes != null) dungeons.addAll(newOnes);
    }

    /** Returns a list of all dungeon UUIDs. */
    public List<UUID> getAllIds() {
        List<UUID> ids = new ArrayList<>();
        for (Dungeon d : dungeons) {
            ids.add(d.getUUID());
        }
        return ids;
    }

    /** Returns a dungeon by UUID, or null if not found. */
    public Dungeon getDungeon(UUID id) {
        if (id == null) return null;
        for (Dungeon d : dungeons) {
            if (id.equals(d.getUUID())) return d;
        }
        return null;
    }

    /** Returns a dungeon by string ID, or null if invalid. */
    public Dungeon getDungeon(String id) {
        try {
            return getDungeon(UUID.fromString(id));
        } catch (Exception e) {
            return null;
        }
    }

    /** Adds a new dungeon if it is not already in the list. */
    public void addDungeon(Dungeon d) {
        if (d != null && !dungeons.contains(d)) {
            dungeons.add(d);
        }
    }

    /** Removes a dungeon by ID. Returns true if successful. */
    public boolean removeDungeon(UUID id) {
        if (id == null) return false;
        return dungeons.removeIf(d -> id.equals(d.getUUID()));
    }

    /** Converts the dungeon list to JSON (currently unimplemented). */
    public String toJSON() {
        return "";
    }

    /** Loads dungeons from a JSON string (currently unimplemented). */
    public void fromJSON(String json) {
    }

    /** Clears all dungeons from the list. */
    public void clear() {
        dungeons.clear();
    }
}
