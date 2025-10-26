package com.example.s2tn.model;

import java.util.Objects;

/**
 * Represents an item that can exist in a player's inventory.
 * Each item has a unique key and a display name.
 */
public class Item {

    private final String key;
    private final String displayName;

    /** Creates an item with the same key and display name. */
    public Item(String key) {
        this(key, key);
    }

    /** Creates an item with a specific key and display name. */
    public Item(String key, String displayName) {
        this.key = key == null ? "" : key;
        this.displayName = (displayName == null || displayName.isBlank()) ? this.key : displayName;
    }

    /** Returns the unique key for this item. */
    public String getKey() { return key; }

    /** Returns the display name of this item. */
    public String getDisplayName() { return displayName; }

    /** Returns a string representation of the item. */
    @Override
    public String toString() { return displayName + " [" + key + "]"; }

    /** Returns true if two items share the same key. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item other)) return false;
        return Objects.equals(key, other.key);
    }

    /** Returns the hash code based on the item's key. */
    @Override
    public int hashCode() { return Objects.hash(key); }
}
