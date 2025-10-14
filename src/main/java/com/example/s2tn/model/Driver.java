package com.example.s2tn.model;

import java.util.Scanner;

public class Driver {
    private final Facade facade;
    private boolean running;

    public Driver() {
        this.facade = new Facade();
        this.running = false;
    }

    public void start() {
        running = true;
        run();
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (running) {
            System.out.print("> ");
            String line = sc.nextLine();
            if (line == null) break;
            switch (line.trim().toLowerCase()) {
                case "exit":
                case "quit":
                    stop();
                    break;
                case "start":
                    System.out.println("game start");
                    break;
                default:
                    System.out.println("unknown command");
            }
        }
        sc.close();
    }

    public void stop() {
        running = false;
    }

    public static void main(String[] args) {
    
    // this is temporary comment out when needed
    System.out.println("cwd: " + java.nio.file.Paths.get("").toAbsolutePath()); // where the app is running

    var loader = new com.example.s2tn.model.DataLoader();
    var users  = loader.getUsers();
    System.out.println("Loaded users: " + users.size());

    var list = com.example.s2tn.model.UserList.getInstance();
    var a = new com.example.s2tn.model.Account();
    a.setUserName("Nishant");
    a.setPasswordHash("pw2");
    a.setScore(10);
    a.setRank(99);
    list.addUser(a);

    new com.example.s2tn.model.DataWriter().saveUsers();
    System.out.println("Saved users to: " + com.example.s2tn.model.DataConstants.usersPath().toAbsolutePath());

    var ul = com.example.s2tn.model.UserList.getInstance();
    ul.loadFromFile();
        int start = ul.getAll().size();

        // add
        var t = new com.example.s2tn.model.Account();
        t.setUserName("lance");
        t.setPasswordHash("asdf");
        t.setScore(10);
        t.setRank(1);
        System.out.println("add: " + ul.addUser(t));
        new com.example.s2tn.model.DataWriter().saveUsers();

        // read
        ul.loadFromFile();
        var got = ul.getUser("lance");
        System.out.println("read: " + (got != null));

        // update 
        if (got != null) {
            got.setScore(42);
            System.out.println("update: " + ul.updateUser(got));
            new com.example.s2tn.model.DataWriter().saveUsers();
        }

        // score
        ul.loadFromFile();
        var chk = ul.getUser("lance");
        System.out.println("score: " + (chk != null ? chk.getScore() : "n/a"));

        // remove 
        if (chk != null) {
            System.out.println("remove: " + ul.removeUser(chk.getAccountID()));
            new com.example.s2tn.model.DataWriter().saveUsers();
        }

        // CHECK COUNT
        ul.loadFromFile();
        System.out.println("check count: " + (ul.getAll().size() == start));
    }
}
