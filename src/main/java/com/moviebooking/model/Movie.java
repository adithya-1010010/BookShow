package com.moviebooking.model;

public final class Movie {

    private final long id;
    private final String title;
    private final String genre;
    private final double basePrice;
    private final int durationMins;

    public Movie(long id, String title, String genre, double basePrice, int durationMins) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.basePrice = basePrice;
        this.durationMins = durationMins;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getDurationMins() {
        return durationMins;
    }
}
