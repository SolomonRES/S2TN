package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Facade {

    private Account user;
    private Dungeon dungeon;

// ------------------------------------------------------------------------------------------------------

    // account and session
    public Account signUp(String username, String password) {
        return new Account();
    }

    public Account login(String username, String password) {
        return new Account();
    }

    public void logout() {
    }

    public Account getAccount() {
        return user;
    }

// ------------------------------------------------------------------------------------------------------

    // saves and progress
    public void saveProgress() {
    }

    public Progress loadProgress(String userId) {
        return new Progress();
    }

    public List<String> listSaves() {
        return new ArrayList<>();
    }

    public Progress loadSave(String slot) {
        return new Progress();
    }

    public void deleteSave(String slot) {
    }

    public void resetProgress() {
    }

    public Progress getCurrentStatus() {
        return new Progress();
    }

// ------------------------------------------------------------------------------------------------------

    // dungeon management
    public Dungeon selectDungeon(UUID dungeonId) {
        return new Dungeon(dungeon, null);
    }

    public void chooseDifficulty(String level) {
    }

    public void startDungeon(UUID dungeonId) {
    }

    public void restartDungeon() {
    }

    public void exitDungeon() {
    }

    public void completeDungeon() {
    }

    public Map<?, ?> getMap() {
        return new HashMap<>();
    }

    public List<Room> viewRooms() {
        return new ArrayList<>();
    }

    public void changeRoom(UUID roomID) {
    }

    public void nextRoom() {
    }

    public void previousRoom() {
    }

// ------------------------------------------------------------------------------------------------------

    // puzzle interactions
    public void attemptPuzzle(UUID puzzleId, String input) {
    }

    public Hint requestHint(UUID puzzleId, int level) {
        return new Hint();
    }

    public void skipPuzzle(UUID puzzleId) {
    }

    public void solvePuzzle(UUID puzzleId) {
    }

    public void submitCode(UUID puzzleId, String code) {
    }

    public void moveInMaze(UUID puzzleId, String direction) {
    }

    public boolean slideShape(UUID puzzleId, Object shape, String direction, int steps) {
        return false;
    }

    public void alignShapes(UUID puzzleId, List<Object> shapes) {
    }

    public boolean answerRiddle(UUID puzzleId, String answer) {
        return false;
    }

    public boolean answerScramble(UUID puzzleId, String answer) {
        return false;
    }

// ------------------------------------------------------------------------------------------------------
    
    // time and leaderboard
    public void pauseTimer() {
    }

    public void resumeTimer() {
    }

    public void openLeaderboard(Account user) {
    }

// ------------------------------------------------------------------------------------------------------
    
    // room markers and scoring, 
    private void unlockExit(UUID fromRoom, UUID toRoom) {
    }

    private void markRoomExplored(UUID fromRoom, UUID toRoom) {
    }

    private void markRoomComplete(UUID roomId) {
    }

    private void submitScore(String userName, int score, long elapsedTime) {
    }

// ------------------------------------------------------------------------------------------------------
}
