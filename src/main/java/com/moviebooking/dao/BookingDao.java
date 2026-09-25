package com.moviebooking.dao;

import com.moviebooking.model.Booking;
import java.util.List;

public interface BookingDao {

    Booking insertBooking(Booking booking, List<Long> seatIds);
}
