package com.moviebooking.db;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseManagerTest {

    private static final Map<String, Set<String>> EXPECTED_COLUMNS = Map.of(
            "movie", Set.of("id", "title", "genre", "base_price", "duration_minutes"),
            "theatre", Set.of("id", "name", "location", "screen_count"),
            "show", Set.of("id", "movie_id", "theatre_id", "show_datetime", "screen_number"),
            "seat", Set.of("id", "show_id", "row_label", "column_number", "booked"),
            "booking", Set.of(
                    "id",
                    "booking_code",
                    "show_id",
                    "customer_name",
                    "customer_phone",
                    "total_cost",
                    "created_at"
            ),
            "booking_seat", Set.of("booking_id", "seat_id")
    );

    private static final List<String> TABLE_COUNT_QUERIES = List.of(
            "SELECT COUNT(*) FROM movie",
            "SELECT COUNT(*) FROM theatre",
            "SELECT COUNT(*) FROM show",
            "SELECT COUNT(*) FROM seat",
            "SELECT COUNT(*) FROM booking",
            "SELECT COUNT(*) FROM booking_seat"
    );

    @TempDir
    private Path tempDirectory;

    @BeforeEach
    void resetDatabaseManager() throws SQLException {
        DatabaseManager.close();
    }

    @AfterEach
    void closeDatabase() throws SQLException {
        DatabaseManager.close();
    }

    @Test
    void initializationCreatesExpectedSchemaIdempotently() throws SQLException {
        assertThrows(IllegalStateException.class, DatabaseManager::getConnection);

        Path databasePath = tempDirectory.resolve("nested").resolve("moviebooking-test.db");

        assertDoesNotThrow(() -> DatabaseManager.init(databasePath));
        assertTrue(Files.isRegularFile(databasePath));

        Connection firstConnection = DatabaseManager.getConnection();
        assertExpectedSchema(firstConnection);
        assertAllTablesEmpty(firstConnection);

        assertDoesNotThrow(() -> DatabaseManager.init(databasePath));

        assertSame(firstConnection, DatabaseManager.getConnection());
        assertFalse(firstConnection.isClosed());
        assertExpectedSchema(firstConnection);
        assertAllTablesEmpty(firstConnection);
    }

    private static void assertExpectedSchema(Connection connection) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        for (Map.Entry<String, Set<String>> expectedTable : EXPECTED_COLUMNS.entrySet()) {
            String tableName = expectedTable.getKey();
            assertTrue(tableExists(metadata, tableName), "Missing table: " + tableName);
            assertEquals(
                    expectedTable.getValue(),
                    columnNames(metadata, tableName),
                    "Unexpected columns for table: " + tableName
            );
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA foreign_keys")) {
            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
        }
    }

    private static void assertAllTablesEmpty(Connection connection) throws SQLException {
        for (String query : TABLE_COUNT_QUERIES) {
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(0, resultSet.getInt(1), query);
            }
        }
    }

    private static boolean tableExists(DatabaseMetaData metadata, String tableName) throws SQLException {
        try (ResultSet resultSet = metadata.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    private static Set<String> columnNames(DatabaseMetaData metadata, String tableName) throws SQLException {
        Set<String> columns = new HashSet<>();
        try (ResultSet resultSet = metadata.getColumns(null, null, tableName, null)) {
            while (resultSet.next()) {
                columns.add(resultSet.getString("COLUMN_NAME"));
            }
        }
        return columns;
    }
}
