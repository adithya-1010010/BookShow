package com.moviebooking.ui;

import com.moviebooking.service.BookingService;
import com.moviebooking.service.MovieService;

public final class AppServices {

    private static MovieService movieService;
    private static BookingService bookingService;

    private AppServices() {
    }

    public static void initialize(MovieService movies, BookingService bookings) {
        movieService = movies;
        bookingService = bookings;
    }

    public static MovieService movies() {
        if (movieService == null) {
            throw new IllegalStateException("AppServices.initialize() must be called before use");
        }
        return movieService;
    }

    public static BookingService bookings() {
        if (bookingService == null) {
            throw new IllegalStateException("AppServices.initialize() must be called before use");
        }
        return bookingService;
    }
}
