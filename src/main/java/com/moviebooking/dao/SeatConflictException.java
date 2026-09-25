package com.moviebooking.dao;

public class SeatConflictException extends DataAccessException {

    private static final long serialVersionUID = 1L;

    public SeatConflictException(String message) {
        super(message);
    }
}
