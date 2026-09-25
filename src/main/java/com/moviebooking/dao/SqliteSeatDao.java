package com.moviebooking.dao;

import com.moviebooking.db.DatabaseManager;
import com.moviebooking.model.Seat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SqliteSeatDao implements SeatDao {

    private static final String SELECT_BY_SHOW =
            "SELECT id, show_id, row_label, column_number, booked FROM seat "
                    + "WHERE show_id = ? ORDER BY row_label, column_number";

    private static final String MARK_BOOKED = "UPDATE seat SET booked = 1 WHERE id = ? AND booked = 0";

    @Override
    public List<Seat> findByShowId(long showId) {
        List<Seat> seats = new ArrayList<>();
        try (PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(SELECT_BY_SHOW)) {
            statement.setLong(1, showId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    seats.add(mapRow(resultSet));
                }
                return seats;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to load seats for show " + showId, exception);
        }
    }

    @Override
    public int markBooked(List<Long> seatIds, Connection connection) {
        if (seatIds.isEmpty()) {
            return 0;
        }
        int marked = 0;
        try (PreparedStatement statement = connection.prepareStatement(MARK_BOOKED)) {
            for (Long seatId : seatIds) {
                statement.setLong(1, seatId);
                statement.addBatch();
            }
            for (int updateCount : statement.executeBatch()) {
                if (updateCount > 0 || updateCount == Statement.SUCCESS_NO_INFO) {
                    marked++;
                }
            }
            return marked;
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to mark seats as booked", exception);
        }
    }

    private static Seat mapRow(ResultSet resultSet) throws SQLException {
        return new Seat(
                resultSet.getLong("id"),
                resultSet.getLong("show_id"),
                resultSet.getString("row_label"),
                resultSet.getInt("column_number"),
                resultSet.getInt("booked") == 1
        );
    }
}
