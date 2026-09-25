package com.moviebooking.ui;

import java.io.IOException;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class SceneNavigator {

    private static Stage stage;

    private SceneNavigator() {
    }

    public static void attach(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void goTo(String screenPath) {
        if (stage == null) {
            throw new IllegalStateException("SceneNavigator.attach() must be called before navigation");
        }
        Scene scene = buildScene(screenPath);
        stage.setScene(scene);
        stage.show();
        fadeIn(scene.getRoot());
    }

    public static Parent loadRoot(String screenPath) {
        try {
            FXMLLoader loader = new FXMLLoader(resource(screenPath));
            return loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load screen: " + screenPath, exception);
        }
    }

    private static Scene buildScene(String screenPath) {
        URL stylesheet = resource(Screens.STYLESHEET);
        Scene scene = new Scene(loadRoot(screenPath), Screens.WIDTH, Screens.HEIGHT);
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        return scene;
    }

    private static URL resource(String path) {
        return SceneNavigator.class.getResource(path);
    }

    private static void fadeIn(Parent root) {
        FadeTransition transition = new FadeTransition(Duration.millis(220), root);
        transition.setFromValue(0.0);
        transition.setToValue(1.0);
        transition.play();
    }
}
