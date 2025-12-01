package com.s2tn;

import com.s2tn.model.Facade;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LandingController {

    @FXML private ScrollPane challengeScroll;
    @FXML private HBox challengeStrip;
    @FXML private ImageView logoImage;

    @FXML private Button playGuestButton;
    @FXML private Button loginButton;
    @FXML private Button signupButton;

    private final Facade facade = App.getFacade();

    private static class Challenge {
        String imageFile;
        String name;
        String description;
        String difficulty;

        Challenge(String imageFile, String name, String description, String difficulty) {
            this.imageFile = imageFile;
            this.name = name;
            this.description = description;
            this.difficulty = difficulty;
        }
    }

    private final Challenge[] challenges = new Challenge[] {
            new Challenge("hallOfTorches.png", "Hall of Torches",
                    "Ancient flames light the way", "Beginner"),
            new Challenge("goldenVault.png", "The Golden Vault",
                    "Treasures behind gilded doors", "Intermediate"),
            new Challenge("sentinelTomb.png", "Sentinel's Tomb",
                    "Ancient guardians rest here", "Advanced")
    };

    @FXML
    public void initialize() {
        // logo
        Image logo = new Image(
                getClass().getResourceAsStream("/com/s2tn/assets/logo.png")
        );
        logoImage.setImage(logo);

        buildChallengeCards();
        startTicker();
    }

    private void buildChallengeCards() {
        // duplicate challenges a few times for loop
        for (int r = 0; r < 3; r++) {
            for (Challenge c : challenges) {
                challengeStrip.getChildren().add(createChallengeCard(c));
            }
        }
    }

    private StackPane createChallengeCard(Challenge c) {
        Image img = new Image(
                getClass().getResourceAsStream("/com/s2tn/assets/" + c.imageFile)
        );
        ImageView iv = new ImageView(img);
        iv.setFitWidth(280);
        iv.setFitHeight(180);
        iv.setPreserveRatio(false);

        Label difficulty = new Label(c.difficulty);
        difficulty.getStyleClass().add("difficulty-pill");
        switch (c.difficulty) {
            case "Beginner" -> difficulty.getStyleClass().add("pill-beginner");
            case "Intermediate" -> difficulty.getStyleClass().add("pill-intermediate");
            default -> difficulty.getStyleClass().add("pill-advanced");
        }

        Label name = new Label(c.name);
        name.getStyleClass().add("challenge-name");

        Label description = new Label(c.description);
        description.getStyleClass().add("challenge-desc");

        VBox overlayContent = new VBox(4, difficulty, name, description);
        overlayContent.getStyleClass().add("challenge-overlay-content");

        StackPane overlay = new StackPane(overlayContent);
        overlay.getStyleClass().add("challenge-overlay");

        StackPane card = new StackPane(iv, overlay);
        card.getStyleClass().add("challenge-card");

        return card;
    }

    private void startTicker() {
        // ticker by adjusting hvalue of ScrollPane
        Timeline ticker = new Timeline(
                new KeyFrame(Duration.millis(40), e -> {
                    double h = challengeScroll.getHvalue();
                    double delta = 0.001; // speed
                    double next = h + delta;
                    if (next >= 1.0) {
                        next = 0.0;
                    }
                    challengeScroll.setHvalue(next);
                })
        );
        ticker.setCycleCount(Animation.INDEFINITE);
        ticker.play();
    }

    // navigation hooks (wire to facade later, this is just a placeholder for now)
    @FXML
    private void playGuest() {
        System.out.println("Play as Guest clicked");
        // e.g. App.setRoot("map");
    }

    @FXML
    private void login() {
        System.out.println("Login clicked");
    }

    @FXML
    private void signup() {
        System.out.println("Signup clicked");
    }
}
