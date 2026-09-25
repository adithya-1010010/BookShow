package com.moviebooking.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public final class TestDatabase {

    private TestDatabase() {
    }

    public static Connection initSeeded(Path databasePath) throws SQLException {
        initEmpty(databasePath);
        SeedData.seedIfEmpty();
        return DatabaseManager.getConnection();
    }

    public static Connection initEmpty(Path databasePath) throws SQLException {
        DatabaseManager.close();
        DatabaseManager.init(databasePath);
        return DatabaseManager.getConnection();
    }

    public static void close() throws SQLException {
        DatabaseManager.close();
    }
}
