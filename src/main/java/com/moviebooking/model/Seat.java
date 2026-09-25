package com.moviebooking.model;

public final class Seat {

    private final long id;
    private final long showId;
    private final String rowLabel;
    private final int columnNumber;
    private final boolean booked;

    public Seat(long id, long showId, String rowLabel, int columnNumber, boolean booked) {
        this.id = id;
        this.showId = showId;
        this.rowLabel = rowLabel;
        this.columnNumber = columnNumber;
        this.booked = booked;
    }

    public long getId() {
        return id;
    }

    public long getShowId() {
        return showId;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public boolean isBooked() {
        return booked;
    }

    public boolean isAvailable() {
        return !booked;
    }
}
