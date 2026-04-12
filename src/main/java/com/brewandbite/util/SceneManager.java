package com.brewandbite.util;

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

    /**
     * Loads an FXML file from the classpath and replaces the current scene.
     *
     * @param fxmlPath classpath path, e.g. {@code "/com/brewandbite/view/CustomerView.fxml"}
     * @param title    window title
     */
    public static void switchTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene  scene = new Scene(root);

            String css = "/com/brewandbite/css/style.css";
            if (SceneManager.class.getResource(css) != null) {
                scene.getStylesheets().add(SceneManager.class.getResource(css).toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load scene: " + fxmlPath, e);
        }
    }
}
