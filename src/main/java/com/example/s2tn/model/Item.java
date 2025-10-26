package com.example.s2tn.model;

import java.util.Objects;

public class Item {

    private final String key;
    private final String displayName;

    public Item(String key) {
        this(key, key);
    }

    public Item(String key, String displayName) {
        this.key = key == null ? "" : key;
        this.displayName = (displayName == null || displayName.isBlank()) ? this.key : displayName;
    }

    public String getKey() { return key; }
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return displayName + " [" + key + "]"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item other)) return false;
        return Objects.equals(key, other.key);
    }

    @Override
    public int hashCode() { return Objects.hash(key); }
}
