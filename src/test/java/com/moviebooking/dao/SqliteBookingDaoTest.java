package com.moviebooking.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviebooking.db.TestDatabase;
import com.moviebooking.model.Booking;
import com.moviebooking.model.Customer;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SqliteBookingDaoTest {

    private static final String SELECT_BOOKING_BY_CODE =
            "SELECT id, show_id, customer_name, customer_phone, total_cost FROM booking WHERE booking_code = ?";

    @TempDir
    private Path tempDirectory;

    private Connection connection;
    private ShowDao showDao;
    private SeatDao seatDao;
    private BookingDao bookingDao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabase.initSeeded(tempDirectory.resolve("booking-dao-test.db"));
        showDao = new SqliteShowDao();
        seatDao = new SqliteSeatDao();
        bookingDao = new SqliteBookingDao(seatDao);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabase.close();
    }

    @Test
    void insertBookingPersistsBookingAndMarksSeatsInOneTransaction() throws SQLException {
        Show show = firstShow();
        List<Seat> seats = seatDao.findByShowId(show.getId());
        Seat first = seats.get(0);
        Seat second = seats.get(1);

        Booking created = bookingDao.insertBooking(
                booking("BK-TEST-0001", show, List.of(first, second), 360.0),
                List.of(first.getId(), second.getId()));

        assertTrue(created.getId() > 0);
        assertEquals("BK-TEST-0001", created.getBookingCode());
        assertSeatBooked(first.getId(), true);
        assertSeatBooked(second.getId(), true);
        assertSeatBooked(seats.get(2).getId(), false);
        assertEquals(1, count("booking"));
        assertEquals(2, count("booking_seat"));
        assertPersistedBooking("BK-TEST-0001", show.getId(), "Asha Rao", 360.0);
    }

    @Test
    void insertBookingMarksEveryRequestedSeat() throws SQLException {
        Show show = firstShow();
        List<Seat> seats = seatDao.findByShowId(show.getId());
        List<Long> seatIds = List.of(seats.get(0).getId(), seats.get(5).getId(), seats.get(29).getId());

        bookingDao.insertBooking(booking("BK-TEST-0002", show, seats, 540.0), seatIds);

        for (Long seatId : seatIds) {
            assertSeatBooked(seatId, true);
        }
        assertEquals(3, count("booking_seat"));
    }

    @Test
    void bookingAnAlreadyBookedSeatRollsBackEveryWrite() throws SQLException {
        Show show = firstShow();
        List<Seat> seats = seatDao.findByShowId(show.getId());
        Seat taken = seats.get(0);
        Seat free = seats.get(1);
        markBookedDirectly(taken.getId());

        assertThrows(
                DataAccessException.class,
                () -> bookingDao.insertBooking(
                        booking("BK-TEST-0003", show, List.of(taken, free), 360.0),
                        List.of(taken.getId(), free.getId())));

        assertSeatBooked(free.getId(), false);
        assertEquals(0, count("booking"));
        assertEquals(0, count("booking_seat"));
    }

    @Test
    void unknownSeatIdRollsBackTheWholeTransaction() throws SQLException {
        Show show = firstShow();
        Seat free = seatDao.findByShowId(show.getId()).get(0);

        assertThrows(
                DataAccessException.class,
                () -> bookingDao.insertBooking(
                        booking("BK-TEST-0004", show, List.of(free), 180.0),
                        List.of(free.getId(), 999_999L)));

        assertSeatBooked(free.getId(), false);
        assertEquals(0, count("booking"));
        assertEquals(0, count("booking_seat"));
    }

    @Test
    void insertBookingKeepsAutoCommitRestored() throws SQLException {
        Show show = firstShow();
        List<Seat> seats = seatDao.findByShowId(show.getId());

        bookingDao.insertBooking(
                booking("BK-TEST-0005", show, List.of(seats.get(0)), 180.0),
                List.of(seats.get(0).getId()));

        assertTrue(connection.getAutoCommit());
    }

    private static Booking booking(String code, Show show, List<Seat> seats, double total) {
        return new Booking(0L, code, show, new Customer("Asha Rao", "9876543210"), seats, total,
                LocalDateTime.now().withNano(0));
    }

    private Show firstShow() {
        Movie movie = new SqliteMovieDao().findAll().get(0);
        return showDao.findByMovieId(movie.getId()).get(0);
    }

    private void markBookedDirectly(long seatId) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement("UPDATE seat SET booked = 1 WHERE id = ?")) {
            statement.setLong(1, seatId);
            statement.executeUpdate();
        }
    }

    private void assertSeatBooked(long seatId, boolean expectedBooked) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT booked FROM seat WHERE id = ?")) {
            statement.setLong(1, seatId);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(expectedBooked, resultSet.getInt(1) == 1, "seat " + seatId);
            }
        }
    }

    private void assertPersistedBooking(String code, long showId, String customerName, double total)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BOOKING_BY_CODE)) {
            statement.setString(1, code);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(showId, resultSet.getLong("show_id"));
                assertEquals(customerName, resultSet.getString("customer_name"));
                assertEquals("9876543210", resultSet.getString("customer_phone"));
                assertEquals(total, resultSet.getDouble("total_cost"));
            }
        }
    }

    private int count(String table) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + table);
             ResultSet resultSet = statement.executeQuery()) {
            assertTrue(resultSet.next());
            return resultSet.getInt(1);
        }
    }
}
