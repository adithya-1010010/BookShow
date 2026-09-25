package com.moviebooking.controller;

import com.moviebooking.model.Show;
import com.moviebooking.ui.AppServices;
import com.moviebooking.ui.BookingSession;
import com.moviebooking.ui.Currency;
import com.moviebooking.ui.SceneNavigator;
import com.moviebooking.ui.Screens;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ShowSelectionController {

    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("EEE, d MMM");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");

    @FXML
    private VBox showContainer;

    @FXML
    private ToggleGroup showGroup;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label hintLabel;

    @FXML
    private Button continueButton;

    @FXML
    private void initialize() {
        showGroup = new ToggleGroup();

        var movie = BookingSession.get().getMovie();
        if (movie == null) {
            SceneNavigator.goTo(Screens.MOVIE_LIST);
            return;
        }

        titleLabel.setText(movie.getTitle());
        subtitleLabel.setText(movie.getGenre() + "  •  " + Currency.format(movie.getBasePrice()) + " per seat");

        List<Show> shows = AppServices.movies().listShowsForMovie(movie.getId());
        if (shows.isEmpty()) {
            hintLabel.setText("No shows scheduled for this movie right now.");
            return;
        }

        for (Show show : shows) {
            showContainer.getChildren().add(buildRow(show));
        }
        showGroup.selectToggle(null);
        continueButton.setDisable(true);
        hintLabel.setText("Select one show to continue");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.goTo(Screens.MOVIE_LIST);
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        if (showGroup.getSelectedToggle() == null) {
            return;
        }
        Show selected = (Show) showGroup.getSelectedToggle().getUserData();
        BookingSession.get().setShow(selected);
        SceneNavigator.goTo(Screens.SEAT_SELECTION);
    }

    private ToggleButton buildRow(Show show) {
        ToggleButton row = new ToggleButton();
        row.setToggleGroup(showGroup);
        row.setUserData(show);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setPrefWidth(600);
        row.getStyleClass().add("show-row");
        row.setOnAction(event -> continueButton.setDisable(false));

        Label time = new Label(show.getDateTime().format(TIME_FORMAT));
        time.getStyleClass().add("show-time");

        Label day = new Label(show.getDateTime().format(DAY_FORMAT));
        day.getStyleClass().add("show-date");

        Label theatre = new Label(show.getTheatre().getName());
        theatre.getStyleClass().add("muted-strong");

        Label screen = new Label("Screen " + show.getScreenNumber());
        screen.getStyleClass().add("muted");

        VBox when = new VBox(2.0, time, day);
        when.setMinWidth(160);

        VBox where = new VBox(2.0, theatre, screen);
        where.setMinWidth(220);

        HBox content = new HBox(24.0, when, where);
        content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        content.setMaxWidth(Double.MAX_VALUE);

        row.setGraphic(content);
        return row;
    }
}
