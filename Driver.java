package com.example.s2tn.model;

public class Driver {
    // commenting out to test data writer and loader
    
    // private final Facade facade;
    // private boolean running;

    // public Driver() {
    //     this.facade = new Facade();
    //     this.running = false;
    // }

    // public void start() {
    //     running = true;
    //     run();
    // }

    // public void run() {
    //     Scanner sc = new Scanner(System.in);
    //     while (running) {
    //         System.out.print("> ");
    //         String line = sc.nextLine();
    //         if (line == null) break;
    //         switch (line.trim().toLowerCase()) {
    //             case "exit":
    //             case "quit":
    //                 stop();
    //                 break;
    //             case "start":
    //                 System.out.println("game start");
    //                 break;
    //             default:
    //                 System.out.println("unknown command");
    //         }
    //     }
    //     sc.close();
    // }

    // public void stop() {
    //     running = false;
    // }

    public static void main(String[] args) {
    
        System.out.println("cwd: " + java.nio.file.Paths.get("").toAbsolutePath()); // where the app is running
        
        new DataLoader().loadUsers();
        System.out.println("Loaded users: " + UserList.getInstance().getAll().size());
        
        var svc = new UserService();
        var a = new Account();

        a.setUserName("Lance");
        a.setPasswordHash("jkl;");
        a.setScore(10);
        a.setRank(99);
        System.out.println("added user: " + svc.addUser(a));
        
        var got = svc.getByUserName("Lance");
        if (got != null) {
            got.setScore(42);
            System.out.println("updated score: " + svc.updateUser(got));
        }

        if (got != null) {
            System.out.println("removed user: " + svc.removeUser(got.getAccountID()));
        }

        System.out.println("users: " + UserList.getInstance().getAll().size());
        
    }
}
