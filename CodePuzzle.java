package com.example.s2tn.model;

import java.util.HashSet;
import java.util.Set;

public class CodePuzzle {
    private Set<String> acceptedCodes;

    public CodePuzzle() {
        this.acceptedCodes = new HashSet<>();
        acceptedCodes.add("victory");
    }

    public boolean submit(String code) {
        if (code == null || code.isEmpty()) return false;
        return acceptedCodes.contains(code.trim().toLowerCase());
    }
}
