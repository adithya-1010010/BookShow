package com.moviebooking.controller;

import com.moviebooking.ui.SceneNavigator;
import com.moviebooking.ui.Screens;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SeatSelectionController {

    @FXML
    private Label placeholderLabel;

    @FXML
    private void initialize() {
        placeholderLabel.setText("Seat map is being built.");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.goTo(Screens.SHOW_SELECTION);
    }
}
