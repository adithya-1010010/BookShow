package com.moviebooking.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public final class DatabaseManager {

    private static final Path DEFAULT_DATABASE_PATH = Path.of("data", "moviebooking.db");
    private static final List<String> SCHEMA_STATEMENTS = List.of(
            "CREATE TABLE IF NOT EXISTS movie ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT NOT NULL, "
                    + "genre TEXT NOT NULL, "
                    + "base_price REAL NOT NULL, "
                    + "duration_minutes INTEGER NOT NULL"
                    + ")",
            "CREATE TABLE IF NOT EXISTS theatre ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "location TEXT, "
                    + "screen_count INTEGER NOT NULL"
                    + ")",
            "CREATE TABLE IF NOT EXISTS show ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "movie_id INTEGER NOT NULL REFERENCES movie(id), "
                    + "theatre_id INTEGER NOT NULL REFERENCES theatre(id), "
                    + "show_datetime TEXT NOT NULL, "
                    + "screen_number INTEGER NOT NULL"
                    + ")",
            "CREATE TABLE IF NOT EXISTS seat ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "show_id INTEGER NOT NULL REFERENCES show(id), "
                    + "row_label TEXT NOT NULL, "
                    + "column_number INTEGER NOT NULL, "
                    + "booked INTEGER NOT NULL DEFAULT 0, "
                    + "UNIQUE(show_id, row_label, column_number)"
                    + ")",
            "CREATE TABLE IF NOT EXISTS booking ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "booking_code TEXT NOT NULL UNIQUE, "
                    + "show_id INTEGER NOT NULL REFERENCES show(id), "
                    + "customer_name TEXT NOT NULL, "
                    + "customer_phone TEXT NOT NULL, "
                    + "total_cost REAL NOT NULL, "
                    + "created_at TEXT NOT NULL"
                    + ")",
            "CREATE TABLE IF NOT EXISTS booking_seat ("
                    + "booking_id INTEGER NOT NULL REFERENCES booking(id), "
                    + "seat_id INTEGER NOT NULL REFERENCES seat(id), "
                    + "PRIMARY KEY (booking_id, seat_id)"
                    + ")"
    );

    private static Connection connection;
    private static Path activeDatabasePath;

    private DatabaseManager() {
    }

    public static void init() throws SQLException {
        init(DEFAULT_DATABASE_PATH);
    }

    static synchronized void init(Path databasePath) throws SQLException {
        Path normalizedPath = databasePath.toAbsolutePath().normalize();
        if (!usesOpenConnection(normalizedPath)) {
            close();
            open(normalizedPath);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            for (String schemaStatement : SCHEMA_STATEMENTS) {
                statement.executeUpdate(schemaStatement);
            }
        } catch (SQLException exception) {
            try {
                close();
            } catch (SQLException closeException) {
                exception.addSuppressed(closeException);
            }
            throw exception;
        }
    }

    public static synchronized Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("DatabaseManager.init() must be called before getConnection()");
        }
        return connection;
    }

    public static synchronized void close() throws SQLException {
        try {
            if (connection != null) {
                connection.close();
            }
        } finally {
            connection = null;
            activeDatabasePath = null;
        }
    }

    private static boolean usesOpenConnection(Path normalizedPath) throws SQLException {
        return connection != null
                && !connection.isClosed()
                && normalizedPath.equals(activeDatabasePath);
    }

    private static void open(Path normalizedPath) throws SQLException {
        try {
            Path parent = normalizedPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException exception) {
            throw new SQLException("Unable to create the database directory", exception);
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + normalizedPath);
        activeDatabasePath = normalizedPath;
    }
}
