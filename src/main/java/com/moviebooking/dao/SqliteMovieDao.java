package com.moviebooking.dao;

import com.moviebooking.db.DatabaseManager;
import com.moviebooking.model.Movie;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SqliteMovieDao implements MovieDao {

    private static final String SELECT_ALL =
            "SELECT id, title, genre, base_price, duration_minutes FROM movie ORDER BY title";

    @Override
    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                movies.add(mapRow(resultSet));
            }
            return movies;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load movies", exception);
        }
    }

    private static Movie mapRow(ResultSet resultSet) throws SQLException {
        return new Movie(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("genre"),
                resultSet.getDouble("base_price"),
                resultSet.getInt("duration_minutes")
        );
    }
}
