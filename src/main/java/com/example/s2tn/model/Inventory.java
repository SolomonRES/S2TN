package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a player's inventory, storing items and their quantities.
 */
public class Inventory {

    private final Map<String, Integer> counts = new LinkedHashMap<>();

    /** Adds one instance of an item to the inventory by key. */
    public void add(String key) {
        if (key == null || key.isBlank()) return;
        counts.put(key, getCount(key) + 1);
    }

    /** Adds one instance of an item to the inventory using an {@link Item} object. */
    public void add(Item item) {
        if (item == null) return;
        add(item.getKey());
    }

    /** Returns true if the inventory contains at least one of the specified item. */
    public boolean has(String key) {
        return getCount(key) > 0;
    }

    /** Returns the current quantity of the specified item. */
    public int getCount(String key) {
        Integer n = counts.get(key);
        return n == null ? 0 : n;
    }

    /** Uses (removes) one instance of the specified item, if available. */
    public boolean use(String key) {
        int n = getCount(key);
        if (n <= 0) return false;
        if (n == 1) counts.remove(key);
        else counts.put(key, n - 1);
        return true;
    }

    /** Removes all instances of the specified item from the inventory. */
    public boolean removeAll(String key) {
        return counts.remove(key) != null;
    }

    /** Returns an unmodifiable list of all item keys in the inventory. */
    public List<String> listKeys() {
        return Collections.unmodifiableList(new ArrayList<>(counts.keySet()));
    }

    /** Returns the total number of unique item types in the inventory. */
    public int size() {
        return counts.size();
    }

    /** Removes all items from the inventory. */
    public void clear() {
        counts.clear();
    }

    /** Returns a string representation of the inventory contents. */
    @Override
    public String toString() {
        return "Inventory" + counts;
    }
}
