package com.moviebooking.service;

import com.moviebooking.dao.BookingDao;
import com.moviebooking.dao.SeatConflictException;
import com.moviebooking.dao.SeatDao;
import com.moviebooking.model.Booking;
import com.moviebooking.model.Customer;
import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;
import com.moviebooking.util.BookingIdGenerator;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingService {

    private final SeatDao seatDao;
    private final BookingDao bookingDao;
    private final PricingStrategySelector pricingStrategySelector;
    private final BookingIdGenerator bookingIdGenerator;

    public BookingService(SeatDao seatDao, BookingDao bookingDao) {
        this(seatDao, bookingDao, new PricingStrategySelector(), new BookingIdGenerator());
    }

    public BookingService(
            SeatDao seatDao,
            BookingDao bookingDao,
            PricingStrategySelector pricingStrategySelector,
            BookingIdGenerator bookingIdGenerator) {
        this.seatDao = seatDao;
        this.bookingDao = bookingDao;
        this.pricingStrategySelector = pricingStrategySelector;
        this.bookingIdGenerator = bookingIdGenerator;
    }

    public List<Seat> getSeatsForShow(long showId) {
        if (showId <= 0) {
            throw new ValidationException("A valid show must be selected");
        }
        return seatDao.findByShowId(showId);
    }

    public PricingStrategy pricingFor(Show show) {
        if (show == null) {
            throw new ValidationException("A show must be selected");
        }
        return pricingStrategySelector.forDayOfWeek(show.getDateTime().getDayOfWeek());
    }

    public double calculateTotal(Show show, int seatCount) {
        if (seatCount <= 0) {
            throw new ValidationException("At least one seat must be selected");
        }
        return pricingFor(show).priceFor(show, seatCount);
    }

    public Booking createBooking(Show show, List<Seat> seats, Customer customer) {
        requireBookableInput(show, seats, customer);
        requireSeatsAvailableForShow(seats, show);
        ensureSeatsStillAvailable(seats);

        double totalCost = pricingFor(show).priceFor(show, seats.size());
        Booking pendingBooking = new Booking(
                0L,
                bookingIdGenerator.generate(),
                show,
                customer,
                seats,
                totalCost,
                LocalDateTime.now().withNano(0));
        List<Long> seatIds = seats.stream().map(Seat::getId).toList();

        try {
            return bookingDao.insertBooking(pendingBooking, seatIds);
        } catch (SeatConflictException exception) {
            throw new SeatUnavailableException(
                    "One or more selected seats have already been booked. Please choose your seats again.");
        }
    }

    private static void requireBookableInput(Show show, List<Seat> seats, Customer customer) {
        if (show == null) {
            throw new ValidationException("A show must be selected before booking");
        }
        if (customer == null) {
            throw new ValidationException("Customer details are required before booking");
        }
        if (seats == null || seats.isEmpty()) {
            throw new ValidationException("At least one seat must be selected before booking");
        }
    }

    private static void requireSeatsAvailableForShow(List<Seat> seats, Show show) {
        boolean mixedShows = seats.stream().anyMatch(seat -> seat.getShowId() != show.getId());
        if (mixedShows) {
            throw new ValidationException("All selected seats must belong to the selected show");
        }
    }

    private void ensureSeatsStillAvailable(List<Seat> seats) {
        Map<Long, Boolean> currentState = seatDao.findByShowId(seats.get(0).getShowId()).stream()
                .collect(Collectors.toMap(Seat::getId, Seat::isBooked));
        Set<String> unavailable = seats.stream()
                .filter(seat -> seat.isBooked() || !Boolean.FALSE.equals(currentState.get(seat.getId())))
                .map(BookingService::describeSeat)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!unavailable.isEmpty()) {
            throw new SeatUnavailableException(
                    "Seat(s) " + String.join(", ", unavailable) + " are no longer available. Please choose again.");
        }
    }

    private static String describeSeat(Seat seat) {
        return seat.getRowLabel() + seat.getColumnNumber();
    }
}
