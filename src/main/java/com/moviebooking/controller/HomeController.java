package com.moviebooking.controller;

import com.moviebooking.ui.BookingSession;
import com.moviebooking.ui.SceneNavigator;
import com.moviebooking.ui.Screens;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {

    @FXML
    private Button browseButton;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleBrowse(ActionEvent event) {
        BookingSession.get().reset();
        SceneNavigator.goTo(Screens.MOVIE_LIST);
    }

    @FXML
    private void initialize() {
        statusLabel.setText("No account needed — just pick a show and book.");
        browseButton.setDefaultButton(true);
    }
}
