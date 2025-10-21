package com.example.s2tn.model;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Driver {

    public static void main(String[] args) {
        Facade facade = new Facade();
        try (Scanner in = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                switch (mainMenu(in)) {
                    case 1 -> accountMenu(in, facade);
                    case 2 -> dungeonMenu(in, facade);
                    case 3 -> roomsMenu(in, facade);
                    case 4 -> puzzlesMenu(in, facade);
                    case 5 -> timerMenu(in, facade);
                    case 6 -> saveMenu(in, facade);
                    case 0 -> {
                        println("Goodbye");
                        running = false;
                    }
                    default -> println("Invalid selection");
                }
            }
        }
    }

    private static int mainMenu(Scanner in) {
        println("");
        println("==== Virtual Escape Room ====");
        println("1) Account");
        println("2) Dungeon");
        println("3) Rooms");
        println("4) Puzzles");
        println("5) Timer");
        println("6) Save");
        println("0) Quit");
        return askInt(in, "Select: ");
    }

    private static void accountMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Account —");
            println("1) Sign up");
            println("2) Login");
            println("3) Logout");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> {
                    String u = ask(in, "Username: ");
                    String p = ask(in, "Password: ");
                    boolean ok = facade.signUp(u, p);
                    println(ok ? "Sign up successful" : "Sign up failed");
                }
                case 2 -> {
                    String u = ask(in, "Username: ");
                    String p = ask(in, "Password: ");
                    boolean ok = facade.login(u, p);
                    println(ok ? "Login successful." : "Login failed");
                }
                case 3 -> { facade.logout(); println("Logged out"); }
                case 0 -> back = true;
                default -> println("Invalid selection");
            }
        }
    }

    private static void dungeonMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Dungeon —");
            println("1) Choose difficulty (easy/normal/hard)");
            println("2) List dungeons");
            println("3) Start dungeon by number");
            println("4) Exit current dungeon");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> {
                    String diff = ask(in, "Difficulty [easy|normal|hard]: ");
                    facade.chooseDifficulty(diff);
                    println("Difficulty set");
                }
                case 2 -> {
                    List<Dungeon> ds = facade.listDungeons();
                    if (ds == null || ds.isEmpty()) {
                        println("No dungeons loaded. Ensure rooms.json is present");
                    } else {
                        println("Available dungeons:");
                        for (int i = 0; i < ds.size(); i++) {
                            Dungeon d = ds.get(i);
                            String name = d.getName() != null ? d.getName() : ("Dungeon " + (i + 1));
                            String diff = d.getDifficulty() != null ? d.getDifficulty().name() : "UNKNOWN";
                            println(" " + (i + 1) + ") " + name + " [" + diff + "]  id=" + d.getUUID());
                        }
                    }
                }
                case 3 -> {
                    List<Dungeon> ds = facade.listDungeons();
                    if (ds == null || ds.isEmpty()) {
                        println("No dungeons available; check rooms.json");
                        break;
                    }
                    int idx = askInt(in, "Enter dungeon number (1.." + ds.size() + "): ");
                    if (idx < 1 || idx > ds.size()) { println("Invalid selection"); break; }
                    Dungeon chosen = ds.get(idx - 1);
                    boolean started = facade.startDungeon(chosen.getUUID());
                    if (!started) { println("Failed to select dungeon."); break; }
                    boolean entered = facade.enterDungeon();
                    println(entered ? "Started: " + chosen.getName() : "Failed to enter dungeon");
                }
                case 4 -> { facade.exitDungeon(); println("Exited current dungeon"); }
                case 0 -> back = true;
                default -> println("Invalid selection");
            }
        }
    }

    private static void roomsMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("= Rooms =");
            println("1) List rooms");
            println("2) Show current room ID");
            println("3) Enter room by number");
            println("4) Next room");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> {
                    var rooms = facade.viewRooms();
                    if (rooms == null || rooms.isEmpty()) {
                        println("No rooms; Start a dungeon first");
                    } else {
                        println("Rooms:");
                        for (int i = 0; i < rooms.size(); i++) {
                            Room r = rooms.get(i);
                            println(" " + (i + 1) + ") id=" + r.getRoomID()
                                    + " puzzles=" + (r.getPuzzles() == null ? 0 : r.getPuzzles().size()));
                        }
                    }
                }
                case 2 -> {
                    UUID id = facade.getCurrentRoomId();   // ensure Facade has this helper
                    println(id != null ? "Current room ID: " + id : "No current room");
                }
                case 3 -> {
                    var rooms = facade.viewRooms();
                    if (rooms == null || rooms.isEmpty()) { println("No rooms. Start a dungeon first"); break; }
                    int r = askInt(in, "Room number (1.." + rooms.size() + "): ");
                    if (r < 1 || r > rooms.size()) { println("Invalid room number"); break; }
                    boolean ok = facade.enterRoom(rooms.get(r - 1).getRoomID());
                    println(ok ? "Entered room" : "Failed to enter room");
                }
                case 4 -> {
                    boolean moved = facade.nextRoom();
                    println(moved ? "Moved to next room" : "No next room");
                }
                case 0 -> back = true;
                default -> println("Invalid selection.");
            }
        }
    }

    private static void puzzlesMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Puzzles —");
            println("1) Attempt first unsolved puzzle in CURRENT room");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> {

                    var rooms = facade.viewRooms();
                    if (rooms == null || rooms.isEmpty()) {
                        println("No rooms. Start a dungeon (Dungeon > Start) first");
                        break;
                    }
                    if (facade.getCurrentRoomId() == null) {
                        boolean ok = facade.enterRoom(rooms.get(0).getRoomID());
                        if (!ok) { println("Could not enter the first room"); break; }
                    }

                    Room cur = null;
                    for (Room r : rooms) {
                        if (r.getRoomID().equals(facade.getCurrentRoomId())) { cur = r; break; }
                    }
                    if (cur == null) { println("Current room not found"); break; }
                    var puzzles = cur.getPuzzles();
                    if (puzzles == null || puzzles.isEmpty()) { println("This room has 0 puzzles"); break; }

                    println("Puzzles in current room:");
                    Puzzle firstUnsolved = null;
                    for (int i = 0; i < puzzles.size(); i++) {
                        Puzzle p = puzzles.get(i);
                        println(" " + (i + 1) + ") id=" + p.getPuzzleID()
                                + "  title=\"" + p.getTitle() + "\"  state=" + p.getState());
                        if (firstUnsolved == null && p.getState() != PuzzleState.SOLVED) {
                            firstUnsolved = p;
                        }
                    }
                    if (firstUnsolved == null) { println("All puzzles already solved in this room"); break; }

                    String q = facade.getCurrentPuzzleQuestion();
                    if (q != null && !q.isBlank()) {
                        println("");
                        println("Question: " + q);
                    }

                    String input = ask(in, "Type your answer (or 'H' for hint): ");
                    if (input.equalsIgnoreCase("h")) {
                        String hint = facade.getCurrentPuzzleHint();
                        println(hint == null ? "No hint available" : ("Hint: " + hint));
                        
                        input = ask(in, "Your answer: ");
                    }

                    ValidationResult res = facade.attemptCodePuzzle(input);
                    if (res == null) {
                        println("No puzzle available in this room");
                    } else {
                        println((res.isValid() ? "[✓] " : "[x] ") + res.getMessage()
                                + "  New state: " + res.getNewState());
                    }
                }
                case 0 -> back = true;
                default -> println("Invalid selection.");
            }
        }
    }

    private static void timerMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("= Timer =");
            println("1) Pause");
            println("2) Resume");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> { facade.pauseTimer(); println("Timer paused"); }
                case 2 -> { facade.resumeTimer(); println("Timer resumed"); }
                case 0 -> back = true;
                default -> println("Invalid selection");
            }
        }
    }

    private static void saveMenu(Scanner in, @SuppressWarnings("unused") Facade facadeUnused) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Save —");
            println("1) Info");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> println("Users are saved when signing up. No manual save implemented.")
                ;
                case 0 -> back = true;
                default -> println("Invalid selection.");
            }
        }
    }

    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        String s = in.nextLine();
        return s == null ? "" : s.trim();
    }

    private static int askInt(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine();
            if (s == null) return -1;
            try { return Integer.parseInt(s.trim()); }
            catch (NumberFormatException e) { println("Please enter a number."); }
        }
    }

    private static void println(String s) {
        System.out.println(s == null ? "" : s);
    }
}
