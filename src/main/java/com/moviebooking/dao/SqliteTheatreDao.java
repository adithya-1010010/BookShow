package com.moviebooking.dao;

import com.moviebooking.db.DatabaseManager;
import com.moviebooking.model.Theatre;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class SqliteTheatreDao implements TheatreDao {

    private static final String SELECT_BY_ID =
            "SELECT id, name, location, screen_count FROM theatre WHERE id = ?";

    @Override
    public Optional<Theatre> findById(long theatreId) {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(SELECT_BY_ID)) {
            statement.setLong(1, theatreId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load theatre " + theatreId, exception);
        }
    }

    private static Theatre mapRow(ResultSet resultSet) throws SQLException {
        return new Theatre(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("location"),
                resultSet.getInt("screen_count")
        );
    }
}
