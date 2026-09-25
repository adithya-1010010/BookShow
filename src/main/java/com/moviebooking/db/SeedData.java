package com.moviebooking.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class SeedData {

    private static final int SEAT_COLUMNS = 6;
    private static final List<String> ROW_LABELS = List.of("A", "B", "C", "D", "E");

    private SeedData() {
    }

    public static synchronized void seedIfEmpty() throws SQLException {
        Connection connection = DatabaseManager.getConnection();
        if (rowCount(connection, "SELECT COUNT(*) FROM movie") > 0) {
            return;
        }

        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            try {
                insertSeedData(connection);
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private static void insertSeedData(Connection connection) throws SQLException {
        long lastHorizonId = insertMovie(connection, "The Last Horizon", "Sci-Fi", 180.0, 128);
        long midnightEchoesId = insertMovie(connection, "Midnight Echoes", "Thriller", 160.0, 112);
        long beyondTheBlueId = insertMovie(connection, "Beyond the Blue", "Adventure", 140.0, 136);
        long theatreId = insertTheatre(connection, "CineNova Grand", "City Center", 3);

        LocalDate firstShowDate = LocalDate.now().plusDays(1);
        insertShowAndSeats(connection, lastHorizonId, theatreId, firstShowDate.atTime(10, 0), 1);
        insertShowAndSeats(connection, midnightEchoesId, theatreId, firstShowDate.atTime(14, 0), 2);
        insertShowAndSeats(connection, beyondTheBlueId, theatreId, firstShowDate.plusDays(1).atTime(11, 0), 1);
        insertShowAndSeats(connection, lastHorizonId, theatreId, firstShowDate.plusDays(1).atTime(17, 0), 2);
        insertShowAndSeats(connection, midnightEchoesId, theatreId, firstShowDate.plusDays(2).atTime(13, 0), 3);
        insertShowAndSeats(connection, beyondTheBlueId, theatreId, firstShowDate.plusDays(2).atTime(19, 0), 1);
    }

    private static long insertMovie(
            Connection connection,
            String title,
            String genre,
            double basePrice,
            int durationMinutes) throws SQLException {
        String sql = "INSERT INTO movie (title, genre, base_price, duration_minutes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.setString(2, genre);
            statement.setDouble(3, basePrice);
            statement.setInt(4, durationMinutes);
            statement.executeUpdate();
            return generatedId(statement);
        }
    }

    private static long insertTheatre(
            Connection connection,
            String name,
            String location,
            int screenCount) throws SQLException {
        String sql = "INSERT INTO theatre (name, location, screen_count) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, location);
            statement.setInt(3, screenCount);
            statement.executeUpdate();
            return generatedId(statement);
        }
    }

    private static void insertShowAndSeats(
            Connection connection,
            long movieId,
            long theatreId,
            LocalDateTime showDateTime,
            int screenNumber) throws SQLException {
        String showSql = "INSERT INTO show (movie_id, theatre_id, show_datetime, screen_number) VALUES (?, ?, ?, ?)";
        long showId;
        try (PreparedStatement statement = connection.prepareStatement(showSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, movieId);
            statement.setLong(2, theatreId);
            statement.setString(3, showDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            statement.setInt(4, screenNumber);
            statement.executeUpdate();
            showId = generatedId(statement);
        }

        String seatSql = "INSERT INTO seat (show_id, row_label, column_number, booked) VALUES (?, ?, ?, 0)";
        try (PreparedStatement statement = connection.prepareStatement(seatSql)) {
            for (String rowLabel : ROW_LABELS) {
                for (int columnNumber = 1; columnNumber <= SEAT_COLUMNS; columnNumber++) {
                    statement.setLong(1, showId);
                    statement.setString(2, rowLabel);
                    statement.setInt(3, columnNumber);
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        }
    }

    private static long generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (!generatedKeys.next()) {
                throw new SQLException("Seed insert did not return a generated ID");
            }
            return generatedKeys.getLong(1);
        }
    }

    private static int rowCount(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                throw new SQLException("Count query did not return a result");
            }
            return resultSet.getInt(1);
        }
    }
}
