package com.moviebooking;

import com.moviebooking.db.DatabaseManager;
import com.moviebooking.db.SeedData;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        DatabaseManager.init();
        SeedData.seedIfEmpty();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/moviebooking/views/Home.fxml"));
        Parent root = loader.load();
        stage.setTitle("Movie Ticket Booking System");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() throws SQLException {
        DatabaseManager.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
