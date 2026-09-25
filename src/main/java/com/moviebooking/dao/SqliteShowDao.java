package com.moviebooking.dao;

import com.moviebooking.db.DatabaseManager;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Show;
import com.moviebooking.model.Theatre;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SqliteShowDao implements ShowDao {

    private static final String SELECT_BASE =
            "SELECT s.id, s.show_datetime, s.screen_number, "
                    + "m.id AS movie_id, m.title, m.genre, m.base_price, m.duration_minutes, "
                    + "t.id AS theatre_id, t.name AS theatre_name, t.location, t.screen_count "
                    + "FROM show s "
                    + "JOIN movie m ON m.id = s.movie_id "
                    + "JOIN theatre t ON t.id = s.theatre_id ";

    private static final String SELECT_BY_MOVIE =
            SELECT_BASE + "WHERE s.movie_id = ? ORDER BY s.show_datetime, s.screen_number";

    private static final String SELECT_BY_ID = SELECT_BASE + "WHERE s.id = ?";

    @Override
    public List<Show> findByMovieId(long movieId) {
        List<Show> shows = new ArrayList<>();
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(SELECT_BY_MOVIE)) {
            statement.setLong(1, movieId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    shows.add(mapRow(resultSet));
                }
                return shows;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load shows for movie " + movieId, exception);
        }
    }

    @Override
    public Optional<Show> findById(long showId) {
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(SELECT_BY_ID)) {
            statement.setLong(1, showId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load show " + showId, exception);
        }
    }

    private static Show mapRow(ResultSet resultSet) throws SQLException {
        Movie movie = new Movie(
                resultSet.getLong("movie_id"),
                resultSet.getString("title"),
                resultSet.getString("genre"),
                resultSet.getDouble("base_price"),
                resultSet.getInt("duration_minutes")
        );
        Theatre theatre = new Theatre(
                resultSet.getLong("theatre_id"),
                resultSet.getString("theatre_name"),
                resultSet.getString("location"),
                resultSet.getInt("screen_count")
        );
        return new Show(
                resultSet.getLong("id"),
                movie,
                theatre,
                LocalDateTime.parse(resultSet.getString("show_datetime")),
                resultSet.getInt("screen_number")
        );
    }
}
