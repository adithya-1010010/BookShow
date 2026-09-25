package com.moviebooking.dao;

import com.moviebooking.model.Seat;
import java.sql.Connection;
import java.util.List;

public interface SeatDao {

    List<Seat> findByShowId(long showId);

    int markBooked(List<Long> seatIds, Connection connection);
}
