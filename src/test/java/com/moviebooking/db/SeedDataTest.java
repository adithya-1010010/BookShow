package com.moviebooking.db;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SeedDataTest {

    @TempDir
    private Path tempDirectory;

    @BeforeEach
    void initializeDatabase() throws SQLException {
        DatabaseManager.close();
        DatabaseManager.init(tempDirectory.resolve("moviebooking-seed-test.db"));
    }

    @AfterEach
    void closeDatabase() throws SQLException {
        DatabaseManager.close();
    }

    @Test
    void seedingIsIdempotentAndCreatesTheExpectedDataset() throws SQLException {
        LocalDate firstShowDate = LocalDate.now().plusDays(1);

        assertDoesNotThrow(SeedData::seedIfEmpty);
        assertExpectedSeedData(firstShowDate);

        assertDoesNotThrow(SeedData::seedIfEmpty);
        assertExpectedSeedData(firstShowDate);
    }

    private static void assertExpectedSeedData(LocalDate firstShowDate) throws SQLException {
        Connection connection = DatabaseManager.getConnection();

        assertEquals(3, rowCount(connection, "SELECT COUNT(*) FROM movie"));
        assertEquals(1, rowCount(connection, "SELECT COUNT(*) FROM theatre"));
        assertEquals(6, rowCount(connection, "SELECT COUNT(*) FROM show"));
        assertEquals(180, rowCount(connection, "SELECT COUNT(*) FROM seat"));
        assertEquals(0, rowCount(connection, "SELECT COUNT(*) FROM booking"));
        assertEquals(0, rowCount(connection, "SELECT COUNT(*) FROM booking_seat"));

        assertEquals(
                List.of(
                        "The Last Horizon|Sci-Fi|180.0|128",
                        "Midnight Echoes|Thriller|160.0|112",
                        "Beyond the Blue|Adventure|140.0|136"
                ),
                queryStrings(
                        connection,
                        """
                        SELECT title || '|' || genre || '|' || base_price || '|' || duration_minutes
                        FROM movie ORDER BY id
                        """
                )
        );
        assertEquals(
                List.of("CineNova Grand|City Center|3"),
                queryStrings(
                        connection,
                        "SELECT name || '|' || location || '|' || screen_count FROM theatre ORDER BY id"
                )
        );
        assertEquals(
                List.of(
                        showSeed(firstShowDate, 0, 10, "The Last Horizon", 1),
                        showSeed(firstShowDate, 0, 14, "Midnight Echoes", 2),
                        showSeed(firstShowDate, 1, 11, "Beyond the Blue", 1),
                        showSeed(firstShowDate, 1, 17, "The Last Horizon", 2),
                        showSeed(firstShowDate, 2, 13, "Midnight Echoes", 3),
                        showSeed(firstShowDate, 2, 19, "Beyond the Blue", 1)
                ),
                queryStrings(
                        connection,
                        """
                        SELECT m.title || '|' || t.name || '|' || s.show_datetime || '|' || s.screen_number
                        FROM show s
                        JOIN movie m ON m.id = s.movie_id
                        JOIN theatre t ON t.id = s.theatre_id
                        ORDER BY s.id
                        """
                )
        );
        assertSeatLayout(connection);
    }

    private static String showSeed(
            LocalDate firstShowDate,
            int dayOffset,
            int hour,
            String movieTitle,
            int screenNumber) {
        return movieTitle
                + "|CineNova Grand|"
                + firstShowDate.plusDays(dayOffset)
                + "T"
                + String.format(Locale.ROOT, "%02d:00:00", hour)
                + "|"
                + screenNumber;
    }

    private static void assertSeatLayout(Connection connection) throws SQLException {
        String sql = """
                SELECT COUNT(*) || '|' || COUNT(DISTINCT show_id) || '|' || MIN(booked) || '|'
                    || MAX(booked) || '|' || COUNT(DISTINCT row_label) || '|' || MIN(column_number) || '|'
                    || MAX(column_number)
                FROM seat
                """;
        assertEquals(
                List.of("180|6|0|0|5|1|6"),
                queryStrings(connection, sql)
        );

        String seatsPerShowSql = """
                SELECT MIN(seat_count) || '|' || MAX(seat_count)
                FROM (
                    SELECT show_id, COUNT(*) AS seat_count
                    FROM seat
                    GROUP BY show_id
                )
                """;
        assertEquals(
                List.of("30|30"),
                queryStrings(connection, seatsPerShowSql)
        );
    }

    private static List<String> queryStrings(Connection connection, String sql) throws SQLException {
        List<String> values = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                values.add(resultSet.getString(1));
            }
        }
        return values;
    }

    private static int rowCount(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            assertTrue(resultSet.next());
            return resultSet.getInt(1);
        }
    }
}
