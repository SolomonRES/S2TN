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
                        println("Goodbye.");
                        running = false;
                    }
                    default -> println("Invalid selection.");
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
                    var created = facade.signUp(u, p);
                    println(created != null ? "Sign up successful." : "Sign up failed.");
                }
                case 2 -> {
                    String u = ask(in, "Username: ");
                    String p = ask(in, "Password: ");
                    boolean ok = facade.login(u, p);
                    println(ok ? "Login successful." : "Login failed.");
                }
                case 3 -> {
                    facade.logout();
                    println("Logged out.");
                }
                case 0 -> back = true;
                default -> println("Invalid selection.");
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
                    println("Difficulty set.");
                }
                case 2 -> {
                    List<Dungeon> ds = DungeonList.getInstance().getAll();
                    if (ds.isEmpty()) {
                        println("No dungeons loaded yet. Start one to trigger loading or ensure rooms.json is present.");
                    } else {
                        println("Available dungeons:");
                        for (int i = 0; i < ds.size(); i++) {
                            Dungeon d = ds.get(i);
                            String name = (d.getName() != null) ? d.getName() : ("Dungeon " + (i + 1));
                            String diff = (d.getDifficulty() != null) ? d.getDifficulty().name() : "UNKNOWN";
                            println(" " + (i + 1) + ") " + name + " [" + diff + "]");
                        }
                    }
                }
                case 3 -> {
                    List<Dungeon> ds = DungeonList.getInstance().getAll();
                    if (ds.isEmpty()) {

                        facade.startDungeon(null);
                        ds = DungeonList.getInstance().getAll();
                        if (ds.isEmpty()) {
                            println("No dungeons available. Check rooms.json.");
                            break;
                        }

                        facade.exitDungeon();
                    }
                    int idx = askInt(in, "Enter dungeon number (1.." + ds.size() + "): ");
                    if (idx < 1 || idx > ds.size()) { println("Invalid selection."); break; }
                    Dungeon chosen = ds.get(idx - 1);
                    UUID started = facade.enterDungeon(chosen.getUUID());
                    println(started != null ? "Started: " + chosen.getName() : "Failed to start dungeon.");
                }
                case 4 -> {
                    facade.exitDungeon();
                    println("Exited current dungeon.");
                }
                case 0 -> back = true;
                default -> println("Invalid selection.");
            }
        }
    }

    private static void roomsMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Rooms —");
            println("1) Room count");
            println("2) Current room");
            println("3) Enter room by number");
            println("4) Next room");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> {
                    var rooms = facade.viewRooms();
                    println("Rooms available: " + (rooms == null ? 0 : rooms.size()));
                }
                case 2 -> {
                    UUID id = facade.enterRoom(null);
                    println(id != null ? "Current room ID: " + id : "No current room.");
                }
                case 3 -> {
                    var rooms = facade.viewRooms();
                    if (rooms == null || rooms.isEmpty()) { println("No rooms. Start a dungeon first."); break; }
                    int r = askInt(in, "Room number (1.." + rooms.size() + "): ");
                    if (r < 1 || r > rooms.size()) { println("Invalid room number."); break; }
                    UUID rid = facade.enterRoom(rooms.get(r - 1).getRoomID());
                    println(rid != null ? "Entered room: " + rid : "Failed to enter room.");
                }
                case 4 -> {
                    boolean moved = facade.nextRoom();
                    println(moved ? "Moved to next room." : "No next room.");
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
            println("1) Attempt generic solve(s) in current room");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> {
                    boolean a = facade.attemptCodePuzzle("auto");
                    boolean b = facade.attemptCodePuzzle("auto-2");
                    println((a || b) ? "Attempted puzzle solve(s)." : "No puzzle attempt available.");
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
            println("— Timer —");
            println("1) Pause");
            println("2) Resume");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> { facade.pauseTimer(); println("Timer paused."); }
                case 2 -> { facade.resumeTimer(); println("Timer resumed."); }
                case 0 -> back = true;
                default -> println("Invalid selection.");
            }
        }
    }

    private static void saveMenu(Scanner in, Facade facade) {
        boolean back = false;
        while (!back) {
            println("");
            println("— Save —");
            println("1) Save users (triggered by signUp automatically)");
            println("0) Back");
            int ch = askInt(in, "Select: ");
            switch (ch) {
                case 1 -> {

                    println("Users are saved when signing up. No additional action implemented.");
                }
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
