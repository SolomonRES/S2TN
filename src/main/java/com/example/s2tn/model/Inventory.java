package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Inventory {

    private final Map<String, Integer> counts = new LinkedHashMap<>();

    public void add(String key) {
        if (key == null || key.isBlank()) return;
        counts.put(key, getCount(key) + 1);
    }

    public void add(Item item) {
        if (item == null) return;
        add(item.getKey());
    }

    public boolean has(String key) {
        return getCount(key) > 0;
    }

    public int getCount(String key) {
        Integer n = counts.get(key);
        return n == null ? 0 : n;
    }

    public boolean use(String key) {
        int n = getCount(key);
        if (n <= 0) return false;
        if (n == 1) counts.remove(key);
        else counts.put(key, n - 1);
        return true;
    }

    public boolean removeAll(String key) {
        return counts.remove(key) != null;
    }

    public List<String> listKeys() {
        return Collections.unmodifiableList(new ArrayList<>(counts.keySet()));
    }

    public int size() {
        return counts.size();
    }

    public void clear() {
        counts.clear();
    }

    @Override
    public String toString() {
        return "Inventory" + counts;
    }
}
