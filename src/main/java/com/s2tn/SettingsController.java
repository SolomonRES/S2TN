package com.s2tn;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

import com.s2tn.Speak;

public class SettingsController {

    @FXML private Slider sliderMasterVolume;
    @FXML private CheckBox chkTextToSpeech;

    @FXML
    private void initialize() {
        chkTextToSpeech.setSelected(Speak.isEnabled());

        chkTextToSpeech.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Speak.setEnabled(newVal);
        });

    }

    @FXML
    private void onBack(ActionEvent e) {
        App.transitionTo("landing");
    }
}
