package com.moviebooking.model;

import java.time.LocalDateTime;
import java.util.List;

public final class Booking {

    private final long id;
    private final String bookingCode;
    private final Show show;
    private final Customer customer;
    private final List<Seat> seats;
    private final double totalCost;
    private final LocalDateTime createdAt;

    public Booking(
            long id,
            String bookingCode,
            Show show,
            Customer customer,
            List<Seat> seats,
            double totalCost,
            LocalDateTime createdAt) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.show = show;
        this.customer = customer;
        this.seats = List.copyOf(seats);
        this.totalCost = totalCost;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public Show getShow() {
        return show;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
