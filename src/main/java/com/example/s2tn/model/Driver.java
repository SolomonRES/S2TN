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
        new Driver().start();
    }
}
