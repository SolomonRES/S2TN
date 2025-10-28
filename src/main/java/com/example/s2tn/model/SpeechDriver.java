package com.example.s2tn.model;

import com.example.s2tn.Speak;

public class SpeechDriver implements TextToSpeech {
    public static void main(String[] args){
        String message = "Hello, how are you today?";
        Speak.speak(message);
    }

    @Override
    public void speak(String text) {
        Speak.speak(text);
    }
}