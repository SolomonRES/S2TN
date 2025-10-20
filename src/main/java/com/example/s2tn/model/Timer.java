package com.example.s2tn.model;

public class Timer {
    private long startTime;
    private long stopTime;
    private boolean running;

    public Timer(){
        startTime = 0;
        stopTime = 0;
        running = false;
    }
    public void start(){
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }
    public void stop(){
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }
    public void unPause(){
        this.stopTime = 0;
        this.running = true;
    }
    public void addPenalty(long penalty){
        this.startTime -= penalty;
    }
    public long elapsedTime(){
        return stopTime - startTime;
    }

    public boolean isRunning() {
        return running;
    }
}
