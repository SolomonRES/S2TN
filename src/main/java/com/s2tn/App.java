package com.s2tn;

import com.s2tn.model.Facade;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;
    private static Facade facade;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        facade = new Facade();

        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/com/s2tn/landing.fxml")
        );

        Scene scene = new Scene(loader.load(), 1280, 720);

        scene.setFill(Color.TRANSPARENT);

        scene.getStylesheets().add(
                App.class.getResource("/com/s2tn/styles.css").toExternalForm()
        );

        stage.getIcons().add(
                new Image(App.class.getResourceAsStream("/com/s2tn/assets/logo.png"))
        );

        stage.setTitle("Escape Room Software™ | S²TN");
        stage.setScene(scene);
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
            Scene scene = new Scene(loader.load(), 1280, 720);

            scene.setFill(Color.TRANSPARENT);

            scene.getStylesheets().add(
                    App.class.getResource("/com/s2tn/styles.css").toExternalForm()
            );

            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
