package com.moviebooking.ui;

import com.moviebooking.model.Movie;
import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;
import java.util.ArrayList;
import java.util.List;

public final class BookingSession {

    private static final BookingSession INSTANCE = new BookingSession();

    private Movie movie;
    private Show show;
    private final List<Seat> selectedSeats = new ArrayList<>();
    private String bookingCode;
    private String bookingTotal;
    private String bookingSeatSummary;

    private BookingSession() {
    }

    public static BookingSession get() {
        return INSTANCE;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
        selectedSeats.clear();
    }

    public List<Seat> getSelectedSeats() {
        return List.copyOf(selectedSeats);
    }

    public void setSelectedSeats(List<Seat> seats) {
        selectedSeats.clear();
        selectedSeats.addAll(seats);
    }

    public void addSelectedSeat(Seat seat) {
        selectedSeats.add(seat);
    }

    public void removeSelectedSeat(Seat seat) {
        selectedSeats.removeIf(selected -> selected.getId() == seat.getId());
    }

    public void clearSelectedSeats() {
        selectedSeats.clear();
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getBookingTotal() {
        return bookingTotal;
    }

    public void setBookingTotal(String bookingTotal) {
        this.bookingTotal = bookingTotal;
    }

    public String getBookingSeatSummary() {
        return bookingSeatSummary;
    }

    public void setBookingSeatSummary(String bookingSeatSummary) {
        this.bookingSeatSummary = bookingSeatSummary;
    }

    public void reset() {
        movie = null;
        show = null;
        selectedSeats.clear();
        bookingCode = null;
        bookingTotal = null;
        bookingSeatSummary = null;
    }
}
