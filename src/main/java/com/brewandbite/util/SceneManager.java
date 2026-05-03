package com.brewandbite.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Centralises JavaFX scene switching. Any controller can call
 * {@link #switchTo(String, String)} without holding a reference to
 * the primary stage.
 */
public class SceneManager {

    private static Stage primaryStage;

    private SceneManager() {}

    public static void setPrimaryStage(Stage stage) { primaryStage = stage; }
    public static Stage getPrimaryStage() { return primaryStage; }

    public static void switchTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            String css = "/com/brewandbite/css/style.css";
            if (SceneManager.class.getResource(css) != null) {
                scene.getStylesheets().add(SceneManager.class.getResource(css).toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            Platform.runLater(() -> WindowManager.clampStageToVisibleBounds(primaryStage));

        } catch (IOException e) {
            throw new RuntimeException("Cannot load scene: " + fxmlPath, e);
        }
    }
}