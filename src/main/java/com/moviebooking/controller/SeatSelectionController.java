package com.moviebooking.controller;

import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;
import com.moviebooking.ui.AppServices;
import com.moviebooking.ui.BookingSession;
import com.moviebooking.ui.Currency;
import com.moviebooking.ui.SceneNavigator;
import com.moviebooking.ui.Screens;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

public class SeatSelectionController {

    private static final DateTimeFormatter SHOW_FORMAT =
            DateTimeFormatter.ofPattern("EEE, d MMM 'at' h:mm a");
    private static final int LABEL_COLUMN = 0;

    @FXML
    private GridPane seatGrid;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label countLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button continueButton;

    @FXML
    private void initialize() {
        Show show = BookingSession.get().getShow();
        if (show == null) {
            SceneNavigator.goTo(Screens.SHOW_SELECTION);
            return;
        }

        titleLabel.setText(show.getMovie().getTitle());
        subtitleLabel.setText(show.getTheatre().getName() + "  •  Screen " + show.getScreenNumber()
                + "  •  " + show.getDateTime().format(SHOW_FORMAT));

        List<Seat> seats = AppServices.bookings().getSeatsForShow(show.getId());
        render(seats);
        updateSummary(show);
    }

    private void render(List<Seat> seats) {
        seatGrid.getChildren().clear();

        Map<String, List<Seat>> byRow = seats.stream().collect(Collectors.groupingBy(
                Seat::getRowLabel,
                java.util.LinkedHashMap::new,
                Collectors.toList()));

        int rowIndex = 0;
        for (Map.Entry<String, List<Seat>> entry : byRow.entrySet()) {
            Label rowLabel = new Label(entry.getKey());
            rowLabel.getStyleClass().add("section-label");
            seatGrid.add(rowLabel, LABEL_COLUMN, rowIndex);

            List<Seat> rowSeats = entry.getValue().stream()
                    .sorted(java.util.Comparator.comparingInt(Seat::getColumnNumber))
                    .toList();
            for (int index = 0; index < rowSeats.size(); index++) {
                Seat seat = rowSeats.get(index);
                ToggleButton button = buildSeatButton(seat);
                seatGrid.add(button, LABEL_COLUMN + 1 + index, rowIndex);
            }
            rowIndex++;
        }
    }

    private ToggleButton buildSeatButton(Seat seat) {
        ToggleButton button = new ToggleButton(String.valueOf(seat.getColumnNumber()));
        button.getStyleClass().add("seat");
        button.setUserData(seat);
        button.setDisable(seat.isBooked());
        button.setOnAction(event -> toggleSeat(seat, button));
        return button;
    }

    private void toggleSeat(Seat seat, ToggleButton button) {
        BookingSession session = BookingSession.get();
        errorLabel.setText("");
        if (button.isSelected()) {
            session.addSelectedSeat(seat);
        } else {
            session.removeSelectedSeat(seat);
        }
        updateSummary(session.getShow());
    }

    private void updateSummary(Show show) {
        List<Seat> selected = BookingSession.get().getSelectedSeats();
        int count = selected.size();
        countLabel.setText(count + (count == 1 ? " seat selected" : " seats selected"));
        if (count == 0) {
            totalLabel.setText("Pick at least one seat to continue");
        } else {
            totalLabel.setText("Total " + Currency.format(
                    AppServices.bookings().calculateTotal(show, count)));
        }
        continueButton.setDisable(count == 0);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        BookingSession.get().clearSelectedSeats();
        SceneNavigator.goTo(Screens.SHOW_SELECTION);
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        if (BookingSession.get().getSelectedSeats().isEmpty()) {
            errorLabel.setText("Select at least one seat to continue");
            return;
        }
        SceneNavigator.goTo(Screens.CUSTOMER_DETAILS);
    }
}
