package com.moviebooking;

import com.moviebooking.dao.MovieDao;
import com.moviebooking.dao.SeatDao;
import com.moviebooking.dao.ShowDao;
import com.moviebooking.dao.SqliteBookingDao;
import com.moviebooking.dao.SqliteMovieDao;
import com.moviebooking.dao.SqliteSeatDao;
import com.moviebooking.dao.SqliteShowDao;
import com.moviebooking.db.DatabaseManager;
import com.moviebooking.db.SeedData;
import com.moviebooking.service.BookingService;
import com.moviebooking.service.MovieService;
import com.moviebooking.ui.AppServices;
import com.moviebooking.ui.SceneNavigator;
import com.moviebooking.ui.Screens;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        DatabaseManager.init();
        SeedData.seedIfEmpty();

        MovieDao movieDao = new SqliteMovieDao();
        ShowDao showDao = new SqliteShowDao();
        SeatDao seatDao = new SqliteSeatDao();
        AppServices.initialize(
                new MovieService(movieDao, showDao),
                new BookingService(seatDao, new SqliteBookingDao(seatDao)));

        SceneNavigator.attach(stage);
        SceneNavigator.goTo(Screens.HOME);
    }

    @Override
    public void stop() throws SQLException {
        DatabaseManager.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
