package com.moviebooking.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviebooking.db.TestDatabase;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SqliteSeatDaoTest {

    @TempDir
    private Path tempDirectory;

    private ShowDao showDao;
    private SeatDao seatDao;

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.initSeeded(tempDirectory.resolve("seat-dao-test.db"));
        showDao = new SqliteShowDao();
        seatDao = new SqliteSeatDao();
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabase.close();
    }

    @Test
    void findByShowIdReturnsTheFullSeatGrid() {
        Show show = firstShow();

        List<Seat> seats = seatDao.findByShowId(show.getId());

        assertEquals(30, seats.size());
        assertEquals("A", seats.get(0).getRowLabel());
        assertEquals(1, seats.get(0).getColumnNumber());
        assertEquals("E", seats.get(29).getRowLabel());
        assertEquals(6, seats.get(29).getColumnNumber());
        assertTrue(seats.stream().allMatch(seat -> seat.getShowId() == show.getId()));
    }

    @Test
    void freshlySeatedSeatsAreAllAvailable() {
        List<Seat> seats = seatDao.findByShowId(firstShow().getId());

        assertTrue(seats.stream().noneMatch(Seat::isBooked));
        assertTrue(seats.stream().allMatch(Seat::isAvailable));
    }

    @Test
    void findByShowIdIsScopedToASingleShow() {
        List<Seat> firstShowSeats = seatDao.findByShowId(firstShow().getId());
        List<Seat> otherShowSeats = seatDao.findByShowId(secondShow().getId());

        assertTrue(firstShowSeats.stream()
                .noneMatch(seat -> otherShowSeats.stream().anyMatch(other -> other.getId() == seat.getId())));
    }

    @Test
    void findByShowIdReturnsEmptyForUnknownShow() {
        assertTrue(seatDao.findByShowId(999_999L).isEmpty());
    }

    private Show firstShow() {
        Movie movie = new SqliteMovieDao().findAll().get(0);
        return showDao.findByMovieId(movie.getId()).get(0);
    }

    private Show secondShow() {
        Movie movie = new SqliteMovieDao().findAll().get(0);
        return showDao.findByMovieId(movie.getId()).get(1);
    }
}
