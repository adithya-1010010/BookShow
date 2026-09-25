package com.moviebooking.model;

import java.time.LocalDateTime;

public final class Show {

    private final long id;
    private final Movie movie;
    private final Theatre theatre;
    private final LocalDateTime dateTime;
    private final int screenNumber;

    public Show(long id, Movie movie, Theatre theatre, LocalDateTime dateTime, int screenNumber) {
        this.id = id;
        this.movie = movie;
        this.theatre = theatre;
        this.dateTime = dateTime;
        this.screenNumber = screenNumber;
    }

    public long getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getScreenNumber() {
        return screenNumber;
    }
}
