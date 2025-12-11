package com.s2tn;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

public class SettingsController {

    @FXML private Slider sliderMasterVolume;
    @FXML private CheckBox chkTextToSpeech;
    @FXML private Button btnchangePassword;
    @FXML private Button btnResetData;
    @FXML private Button btnExportData;
    @FXML private Button btnDeleteAccount;

    @FXML
    private void initialize() {
        chkTextToSpeech.setSelected(Speak.isEnabled());

        chkTextToSpeech.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != Speak.isEnabled()) {
                Speak.toggle();
            }

            if (Speak.isEnabled()) {
                Speak.speak("Voice enabled.");
            } else {
                Speak.toggle();
                Speak.speak("Voice disabled.");
                Speak.toggle();
            }
        });
    }

    @FXML
    private void onBack(ActionEvent e) {
        App.transitionTo("guestmap");
    }
}
