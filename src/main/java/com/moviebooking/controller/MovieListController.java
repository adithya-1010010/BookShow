package com.moviebooking.controller;

import com.moviebooking.model.Movie;
import com.moviebooking.ui.AppServices;
import com.moviebooking.ui.BookingSession;
import com.moviebooking.ui.Currency;
import com.moviebooking.ui.SceneNavigator;
import com.moviebooking.ui.Screens;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MovieListController {

    @FXML
    private FlowPane cardContainer;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label footerLabel;

    @FXML
    private void initialize() {
        java.util.List<Movie> movies = AppServices.movies().listMovies();
        subtitleLabel.setText("Select a movie to see its shows");
        for (Movie movie : movies) {
            cardContainer.getChildren().add(buildCard(movie));
        }
        footerLabel.setText(movies.size() + (movies.size() == 1 ? " movie available" : " movies available"));
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.goTo(Screens.HOME);
    }

    private static VBox buildCard(Movie movie) {
        Label genreChip = new Label(movie.getGenre().toUpperCase());
        genreChip.getStyleClass().add("chip-text");

        StackPane poster = new StackPane();
        poster.getStyleClass().add("poster-block");
        poster.setPrefHeight(120);
        poster.setMinWidth(200);
        Label glyph = new Label(initials(movie.getTitle()));
        glyph.getStyleClass().add("poster-glyph");
        poster.getChildren().add(glyph);

        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);

        Label price = new Label(Currency.format(movie.getBasePrice()) + " / seat");
        price.getStyleClass().add("muted-strong");

        Label duration = new Label(movie.getDurationMins() + " min");
        duration.getStyleClass().add("muted");

        Button viewShows = new Button("View Shows");
        viewShows.getStyleClass().add("primary-button");
        viewShows.setMaxWidth(Double.MAX_VALUE);
        viewShows.setOnAction(event -> openShows(movie));

        VBox card = new VBox(10.0, poster, title, genreChip, price, duration, viewShows);
        card.getStyleClass().add("card");
        card.setPrefWidth(232);
        card.setMinHeight(Region.USE_PREF_SIZE);
        return card;
    }

    private static void openShows(Movie movie) {
        BookingSession.get().setMovie(movie);
        SceneNavigator.goTo(Screens.SHOW_SELECTION);
    }

    private static String initials(String title) {
        String[] words = title.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty() && builder.length() < 2) {
                builder.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return builder.toString();
    }
}
