package com.example.s2tn.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Facade {

    private Account user;
    private Dungeon dungeon;

// --------Account---------------------------------------------------------------------------------------

    public Account signUp(String username, String password) {
        if (username == null || password == null) return null;
        UserList ul = UserList.getInstance();
        if (ul.getUser(username) != null) return null;
        Account a = new Account(username, password);
        a.setScore(0);
        a.setRank(0);
        if (!ul.addUser(a)) return null;
        new DataWriter().saveUsers();
        this.user = a;
        return a;
    }

    public Account login(String username, String password) {
        UserList ul = UserList.getInstance();
        Account a = ul.getUser(username);
        if (a != null && a.login(username, password)) {
            this.user = a;
            return a;
        }
        return null;
    }

    public void logout() {
        this.user = null;
    }

    public Account getAccount() {
        return user;
    }

// ----------------Progress-----------------------------------------------------------------------------

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

// --------------------Dungeon/Map----------------------------------------------------------------------

    public Dungeon selectDungeon(UUID dungeonId) {
        return new Dungeon(dungeon, null);
    }

    public void chooseDifficulty(String level) {
        switch (level.toLowerCase()){
            case "easy":
                dungeon.setDifficulty(Difficulty.EASY);
                break;
            case "normal":
                dungeon.setDifficulty(Difficulty.NORMAL);
                break;
            case "hard":
                dungeon.setDifficulty(Difficulty.HARD);
                break;
        }
    }

    public void startDungeon(UUID dungeonId) {
    }

    public void restartDungeon() {

    }

    public void exitDungeon() {
        dungeon.getTimer().stop();
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
        for(Room findRoom : dungeon.getRooms()){
            if(findRoom.getRoomID() == roomID){
                dungeon.changeRoom(findRoom);
            }
        }
    }

    public void nextRoom() {
        //I'm not sure what this would ever mean as there is likely no "next" vaild room, just a list of rooms to pick from
    }

    public void previousRoom() {
        dungeon.changeRoom(dungeon.getPreviousRoom());
    }

// -------------------Puzzle(s)-------------------------------------------------------------------------

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
        attemptCodePuzzle(code);
    }

    public void moveInMaze(UUID puzzleId, String direction) {
    }

    public boolean slideShape(UUID puzzleId, Object shape, String direction, int steps) {
        return false;
    }

    public void alignShapes(UUID puzzleId, List<Object> shapes) {
    }

    public boolean answerRiddle(UUID puzzleId, String answer) {
        Riddle riddle = new Riddle();
        riddle.displayRiddle();
        return riddle.checkAnswer(answer);
    }

    public boolean answerScramble(UUID puzzleId, String answer) {
        WordScramble scramble = new WordScramble();
        scramble.displayScramble();
        return scramble.checkAnswer(answer);
    }

    public boolean attemptCodePuzzle(String code) {
        CodePuzzle cp = new CodePuzzle();
        return cp.submit(code);
    }

// -----------------------Start/Stop Time-------------------------------------------------------

    public void pauseTimer() {
        if(dungeon.getTimer().isRunning()) {
            dungeon.getTimer().stop();
        }
    }

    public void resumeTimer() {
        if(!dungeon.getTimer().isRunning()) {
            dungeon.getTimer().unPause();
        }
    }
    // probably get rid of this one
    public void openLeaderboard(Account user) {
    }

// ------------------also progress/leaderboard/map? unsure------------------------------

    private void unlockExit(UUID fromRoom, UUID toRoom) {
        for(Room findRoom : dungeon.getRooms()){
            if(findRoom.getRoomID() == fromRoom){
                for(Room isLocked : findRoom.getExits()){
                    if(isLocked.getRoomID() == toRoom){
                     isLocked.unlock(isLocked);
                    }
                }
            }
        }
    }

    private void markRoomExplored(UUID fromRoom, UUID toRoom) {
        for(Room findRoom : dungeon.getRooms()){
            if(findRoom.getRoomID() == fromRoom){
                for(Room unexplored : findRoom.getExits()){
                    if(unexplored.getRoomID() == toRoom){
                        dungeon.getMap().markExplored(unexplored);
                    }
                }
            }
        }
    }

    private void markRoomComplete(UUID roomId) {
        for(Room findRoom : dungeon.getRooms()){
           if(findRoom.getRoomID() == roomId){
               dungeon.getMap().markComplete(findRoom);
           }
        }
    }

    private void submitScore(String userName, int score, long elapsedTime) {
    }

// ------------------------------------------------------------------------------------------------------
}
