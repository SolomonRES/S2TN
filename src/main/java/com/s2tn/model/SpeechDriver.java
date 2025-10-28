package com.s2tn.model;

import com.s2tn.Speak;

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