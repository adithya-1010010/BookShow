package com.moviebooking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviebooking.dao.BookingDao;
import com.moviebooking.dao.MovieDao;
import com.moviebooking.dao.SeatDao;
import com.moviebooking.dao.ShowDao;
import com.moviebooking.dao.SqliteBookingDao;
import com.moviebooking.dao.SqliteMovieDao;
import com.moviebooking.dao.SqliteSeatDao;
import com.moviebooking.dao.SqliteShowDao;
import com.moviebooking.db.TestDatabase;
import com.moviebooking.model.Booking;
import com.moviebooking.model.Customer;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;
import com.moviebooking.model.Theatre;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BookingServiceTest {

    private static final Customer CUSTOMER = new Customer("Asha Rao", "9876543210");

    @TempDir
    private Path tempDirectory;

    private Connection connection;
    private ShowDao showDao;
    private SeatDao seatDao;
    private MovieService movieService;
    private BookingService bookingService;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabase.initSeeded(tempDirectory.resolve("booking-service-test.db"));
        MovieDao movieDao = new SqliteMovieDao();
        showDao = new SqliteShowDao();
        seatDao = new SqliteSeatDao();
        BookingDao bookingDao = new SqliteBookingDao(seatDao);
        movieService = new MovieService(movieDao, showDao);
        bookingService = new BookingService(seatDao, bookingDao);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabase.close();
    }

    @Test
    void getSeatsForShowReturnsTheWholeGrid() {
        Show show = firstShow();

        assertEquals(30, bookingService.getSeatsForShow(show.getId()).size());
    }

    @Test
    void getSeatsForShowRejectsAnInvalidShowId() {
        assertThrows(ValidationException.class, () -> bookingService.getSeatsForShow(0L));
        assertThrows(ValidationException.class, () -> bookingService.getSeatsForShow(-1L));
    }

    @Test
    void createBookingForOneSeatPersistsTheBookingAndMarksTheSeat() throws SQLException {
        Show show = firstShow();
        Seat seat = firstSeat(show);

        Booking booking = bookingService.createBooking(show, List.of(seat), CUSTOMER);

        assertTrue(booking.getId() > 0);
        assertTrue(booking.getBookingCode().matches("BK-\\d{14}-\\d{3}"));
        assertEquals(bookingService.calculateTotal(show, 1), booking.getTotalCost());
        assertEquals(1, count("booking"));
        assertEquals(1, count("booking_seat"));
        assertEquals(1, bookedSeatCount(show));
        assertEquals(CUSTOMER.getName(), storedCustomerName(booking.getBookingCode()));
    }

    @Test
    void createBookingForMultipleSeatsPersistsEverySeat() throws SQLException {
        Show show = firstShow();
        List<Seat> seats = bookingService.getSeatsForShow(show.getId()).subList(0, 3);

        Booking booking = bookingService.createBooking(show, seats, CUSTOMER);

        assertEquals(3, booking.getSeats().size());
        assertEquals(bookingService.calculateTotal(show, 3), booking.getTotalCost());
        assertEquals(3, count("booking_seat"));
        assertEquals(3, bookedSeatCount(show));
    }

    @Test
    void createBookingRejectsAnAlreadyBookedSeatWithoutAnyPartialWrite() throws SQLException {
        Show show = firstShow();
        List<Seat> seats = bookingService.getSeatsForShow(show.getId());
        Seat taken = seats.get(0);
        Seat other = seats.get(1);
        bookingService.createBooking(show, List.of(taken), CUSTOMER);

        SeatUnavailableException exception = assertThrows(
                SeatUnavailableException.class,
                () -> bookingService.createBooking(show, List.of(taken, other), CUSTOMER));

        assertTrue(exception.getMessage().contains("A1"));
        assertEquals(1, count("booking"));
        assertEquals(1, count("booking_seat"));
        assertEquals(1, bookedSeatCount(show));
        assertFalse(isBooked(other), "the other seat must remain available");
    }

    @Test
    void createBookingRejectsASeatAlreadyMarkedBookedInThePassedObject() throws SQLException {
        Show show = firstShow();
        Seat available = firstSeat(show);
        Seat alreadyBooked = new Seat(
                available.getId(), available.getShowId(), available.getRowLabel(),
                available.getColumnNumber(), true);

        assertThrows(
                SeatUnavailableException.class,
                () -> bookingService.createBooking(show, List.of(alreadyBooked), CUSTOMER));
        assertEquals(0, count("booking"));
    }

    @Test
    void createBookingRejectsAnEmptySeatSelection() throws SQLException {
        Show show = firstShow();

        assertThrows(ValidationException.class, () -> bookingService.createBooking(show, List.of(), CUSTOMER));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(show, null, CUSTOMER));
        assertEquals(0, count("booking"));
    }

    @Test
    void createBookingRejectsSeatsFromADifferentShow() throws SQLException {
        Show show = firstShow();
        Show other = showDao.findByMovieId(show.getMovie().getId()).get(1);
        Seat foreignSeat = bookingService.getSeatsForShow(other.getId()).get(0);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(show, List.of(foreignSeat), CUSTOMER));
        assertEquals(0, count("booking"));
    }

    @Test
    void createBookingRejectsMissingShowOrCustomer() throws SQLException {
        Show show = firstShow();
        Seat seat = firstSeat(show);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(null, List.of(seat), CUSTOMER));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(show, List.of(seat), null));
        assertEquals(0, count("booking"));
    }

    @Test
    void calculateTotalRejectsZeroOrNegativeSeatCount() {
        assertThrows(ValidationException.class, () -> bookingService.calculateTotal(firstShow(), 0));
        assertThrows(ValidationException.class, () -> bookingService.calculateTotal(firstShow(), -3));
    }

    @Test
    void bookingIdsAreUniqueAcrossManyBookings() throws SQLException {
        Show show = firstShow();
        List<Seat> seats = bookingService.getSeatsForShow(show.getId());
        Set<String> codes = new HashSet<>();

        for (Seat seat : seats) {
            codes.add(bookingService.createBooking(show, List.of(seat), CUSTOMER).getBookingCode());
        }

        assertEquals(30, codes.size());
        assertEquals(30, count("booking"));
        assertEquals(30, bookedSeatCount(show));
    }

    @Test
    void everySeededShowGetsTheStrategyMatchingItsActualDayOfWeek() {
        for (Show show : allSeededShows()) {
            DayOfWeek dayOfWeek = show.getDateTime().getDayOfWeek();
            boolean weekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
            double expected = show.getMovie().getBasePrice() * (weekend ? 1.2 : 1.0);

            assertEquals(
                    weekend ? WeekendPricing.class : StandardPricing.class,
                    bookingService.pricingFor(show).getClass(),
                    show.getDateTime().toString());
            assertEquals(expected, bookingService.calculateTotal(show, 1), 0.001, show.getDateTime().toString());
        }
    }

    @Test
    void weekendShowsAreChargedWithTheSurcharge() {
        for (DayOfWeek dayOfWeek : List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
            Show weekendShow = showOnDay(dayOfWeek);

            assertTrue(bookingService.pricingFor(weekendShow) instanceof WeekendPricing, dayOfWeek.name());
            assertEquals(216.0, bookingService.calculateTotal(weekendShow, 1), dayOfWeek.name());
            assertEquals(648.0, bookingService.calculateTotal(weekendShow, 3), dayOfWeek.name());
        }
    }

    @Test
    void weekdayShowsAreChargedStandardPricing() {
        for (DayOfWeek dayOfWeek : List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
            Show weekdayShow = showOnDay(dayOfWeek);

            assertTrue(bookingService.pricingFor(weekdayShow) instanceof StandardPricing, dayOfWeek.name());
            assertEquals(180.0, bookingService.calculateTotal(weekdayShow, 1), dayOfWeek.name());
        }
    }

    private Show firstShow() {
        return showDao.findByMovieId(movieByTitle("The Last Horizon").getId()).get(0);
    }

    private List<Show> allSeededShows() {
        return movieService.listMovies().stream()
                .map(movie -> showDao.findByMovieId(movie.getId()))
                .flatMap(List::stream)
                .toList();
    }

    private Movie movieByTitle(String title) {
        return new SqliteMovieDao().findAll().stream()
                .filter(movie -> movie.getTitle().equals(title))
                .findFirst()
                .orElseThrow();
    }

    private Seat firstSeat(Show show) {
        return bookingService.getSeatsForShow(show.getId()).get(0);
    }

    private Show showOnDay(DayOfWeek dayOfWeek) {
        Movie movie = new Movie(1L, "The Last Horizon", "Sci-Fi", 180.0, 128);
        Theatre theatre = new Theatre(1L, "CineNova Grand", "City Center", 3);
        LocalDateTime dateTime = LocalDate.of(2026, 9, 26).with(dayOfWeek).atTime(19, 0);
        return new Show(99L, movie, theatre, dateTime, 1);
    }

    private boolean isBooked(Seat seat) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT booked FROM seat WHERE id = ?")) {
            statement.setLong(1, seat.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) == 1;
            }
        }
    }

    private int bookedSeatCount(Show show) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM seat WHERE show_id = ? AND booked = 1")) {
            statement.setLong(1, show.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private String storedCustomerName(String bookingCode) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT customer_name FROM booking WHERE booking_code = ?")) {
            statement.setString(1, bookingCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getString(1);
            }
        }
    }

    private int count(String table) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + table);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}
