package com.s2tn;

import com.s2tn.model.Facade;
import com.s2tn.model.Maze;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private static Stage primaryStage;
    private static Scene mainScene;
    private static Facade facade;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        facade = new Facade();

        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/s2tn/landing.fxml")
        );
        Parent root = loader.load();

        // this is for one scene 
        mainScene = new Scene(root, 1280, 720);
        mainScene.setFill(Color.TRANSPARENT);

        // Global CSS
        mainScene.getStylesheets().add(
                App.class.getResource("/com/s2tn/styles.css").toExternalForm()
        );

        stage.getIcons().add(
                new Image(App.class.getResourceAsStream("/com/s2tn/assets/logo.png"))
        );

        stage.setTitle("Escape Room Software™ | S²TN");
        stage.setScene(mainScene);

        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        stage.show();
    }

    public static Facade getFacade() {
        return facade;
    }

    public static void setRoot(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/com/s2tn/" + fxmlName + ".fxml")
            );
            Parent newRoot = loader.load();
            mainScene.setRoot(newRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transitionTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource("/com/s2tn/" + fxmlName + ".fxml")
            );
            Parent newRoot = loader.load();
            Parent oldRoot = mainScene.getRoot();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(180), oldRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {

                mainScene.setRoot(newRoot);

                newRoot.setTranslateX(40);
                newRoot.setOpacity(0);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(260), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);

                TranslateTransition slideIn = new TranslateTransition(Duration.millis(260), newRoot);
                slideIn.setFromX(40);
                slideIn.setToX(0);

                fadeIn.play();
                slideIn.play();
            });

            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
