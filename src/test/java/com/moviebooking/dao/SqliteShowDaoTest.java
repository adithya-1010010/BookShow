package com.moviebooking.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviebooking.db.TestDatabase;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Show;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SqliteShowDaoTest {

    @TempDir
    private Path tempDirectory;

    private MovieDao movieDao;
    private ShowDao showDao;

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.initSeeded(tempDirectory.resolve("show-dao-test.db"));
        movieDao = new SqliteMovieDao();
        showDao = new SqliteShowDao();
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabase.close();
    }

    @Test
    void findByMovieIdReturnsOnlyThatMoviesShows() {
        Movie lastHorizon = movieByTitle("The Last Horizon");
        Movie beyondTheBlue = movieByTitle("Beyond the Blue");

        List<Show> lastHorizonShows = showDao.findByMovieId(lastHorizon.getId());
        List<Show> beyondTheBlueShows = showDao.findByMovieId(beyondTheBlue.getId());

        assertEquals(2, lastHorizonShows.size());
        assertEquals(2, beyondTheBlueShows.size());
        assertTrue(lastHorizonShows.stream()
                .allMatch(show -> show.getMovie().getId() == lastHorizon.getId()));
        assertTrue(beyondTheBlueShows.stream()
                .allMatch(show -> show.getMovie().getId() == beyondTheBlue.getId()));
    }

    @Test
    void findByMovieIdJoinsMovieAndTheatreDetails() {
        Show firstShow = showDao.findByMovieId(movieByTitle("The Last Horizon").getId()).get(0);

        assertEquals("The Last Horizon", firstShow.getMovie().getTitle());
        assertEquals(180.0, firstShow.getMovie().getBasePrice());
        assertEquals("CineNova Grand", firstShow.getTheatre().getName());
        assertEquals("City Center", firstShow.getTheatre().getLocation());
        assertEquals(3, firstShow.getTheatre().getScreenCount());
        assertEquals(1, firstShow.getScreenNumber());
        assertEquals(LocalDate.now().plusDays(1), firstShow.getDateTime().toLocalDate());
        assertEquals(10, firstShow.getDateTime().getHour());
        assertEquals(0, firstShow.getDateTime().getMinute());
    }

    @Test
    void findByIdReturnsTheShowAndEmptyWhenMissing() {
        Show known = showDao.findByMovieId(movieByTitle("Midnight Echoes").getId()).get(0);

        Optional<Show> found = showDao.findById(known.getId());

        assertTrue(found.isPresent());
        assertEquals(known.getId(), found.get().getId());
        assertEquals(known.getDateTime(), found.get().getDateTime());
        assertEquals(known.getTheatre().getId(), found.get().getTheatre().getId());
        assertTrue(showDao.findById(999_999L).isEmpty());
    }

    @Test
    void findByMovieIdReturnsEmptyForUnknownMovie() {
        assertTrue(showDao.findByMovieId(999_999L).isEmpty());
    }

    private Movie movieByTitle(String title) {
        return movieDao.findAll().stream()
                .filter(movie -> movie.getTitle().equals(title))
                .findFirst()
                .orElseThrow();
    }
}
