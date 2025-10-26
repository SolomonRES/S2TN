package com.example.s2tn;

import com.example.s2tn.model.TextToSpeech;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Speak {
    private static final String VOICE_NAME = "kevin16";

    public static void speak(String text){
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        VoiceManager voiceManger = VoiceManager.getInstance();
        Voice voice = voiceManger.getVoice(VOICE_NAME);

        if(voice == null){
            System.err.println("Voice not found: " + VOICE_NAME);
            return;
        }

        voice.allocate();
        voice.speak(text);
        voice.deallocate();
    }
}