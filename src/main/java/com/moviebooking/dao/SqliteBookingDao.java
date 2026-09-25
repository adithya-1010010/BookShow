package com.moviebooking.dao;

import com.moviebooking.db.DatabaseManager;
import com.moviebooking.model.Booking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class SqliteBookingDao implements BookingDao {

    private static final String INSERT_BOOKING =
            "INSERT INTO booking (booking_code, show_id, customer_name, customer_phone, total_cost, created_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String INSERT_BOOKING_SEAT =
            "INSERT INTO booking_seat (booking_id, seat_id) VALUES (?, ?)";

    private final SeatDao seatDao;

    public SqliteBookingDao(SeatDao seatDao) {
        this.seatDao = seatDao;
    }

    @Override
    public Booking insertBooking(Booking booking, List<Long> seatIds) {
        Connection connection = DatabaseManager.getConnection();
        boolean originalAutoCommit;
        try {
            originalAutoCommit = connection.getAutoCommit();
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to read connection auto-commit", exception);
        }

        Booking result = runInTransaction(connection, booking, seatIds);

        try {
            connection.setAutoCommit(originalAutoCommit);
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to restore connection auto-commit", exception);
        }
        return result;
    }

    private Booking runInTransaction(Connection connection, Booking booking, List<Long> seatIds) {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to start booking transaction", exception);
        }

        try {
            long bookingId = insertBookingRow(connection, booking);
            int markedSeats = seatDao.markBooked(seatIds, connection);
            if (markedSeats != seatIds.size()) {
                throw new DataAccessException(
                        "One or more seats are already booked; booking was rolled back");
            }
            insertBookingSeats(connection, bookingId, seatIds);
            connection.commit();
            return withGeneratedId(booking, bookingId);
        } catch (RuntimeException | SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            if (exception instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new DataAccessException("Unable to create booking", exception);
        }
    }

    private static long insertBookingRow(Connection connection, Booking booking) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, booking.getBookingCode());
            statement.setLong(2, booking.getShow().getId());
            statement.setString(3, booking.getCustomer().getName());
            statement.setString(4, booking.getCustomer().getPhone());
            statement.setDouble(5, booking.getTotalCost());
            statement.setString(6, booking.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Booking insert did not return a generated ID");
                }
                return generatedKeys.getLong(1);
            }
        }
    }

    private static void insertBookingSeats(Connection connection, long bookingId, List<Long> seatIds)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_BOOKING_SEAT)) {
            for (Long seatId : seatIds) {
                statement.setLong(1, bookingId);
                statement.setLong(2, seatId);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static Booking withGeneratedId(Booking booking, long bookingId) {
        return new Booking(
                bookingId,
                booking.getBookingCode(),
                booking.getShow(),
                booking.getCustomer(),
                booking.getSeats(),
                booking.getTotalCost(),
                booking.getCreatedAt()
        );
    }
}
