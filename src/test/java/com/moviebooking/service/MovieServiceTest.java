package com.moviebooking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviebooking.dao.MovieDao;
import com.moviebooking.dao.ShowDao;
import com.moviebooking.dao.SqliteMovieDao;
import com.moviebooking.dao.SqliteShowDao;
import com.moviebooking.db.TestDatabase;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Show;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MovieServiceTest {

    @TempDir
    private Path tempDirectory;

    private MovieService movieService;

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.initSeeded(tempDirectory.resolve("movie-service-test.db"));
        MovieDao movieDao = new SqliteMovieDao();
        ShowDao showDao = new SqliteShowDao();
        movieService = new MovieService(movieDao, showDao);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabase.close();
    }

    @Test
    void listMoviesReturnsEverySeededMovie() {
        List<Movie> movies = movieService.listMovies();

        assertEquals(3, movies.size());
        assertEquals(
                List.of("Beyond the Blue", "Midnight Echoes", "The Last Horizon"),
                movies.stream().map(Movie::getTitle).toList());
    }

    @Test
    void listShowsForMovieReturnsThatMoviesShowsOnly() {
        Movie movie = movieService.listMovies().get(0);

        List<Show> shows = movieService.listShowsForMovie(movie.getId());

        assertEquals(2, shows.size());
        assertTrue(shows.stream().allMatch(show -> show.getMovie().getId() == movie.getId()));
    }

    @Test
    void getShowReturnsAFullyPopulatedShow() {
        Show expected = movieService.listShowsForMovie(movieService.listMovies().get(0).getId()).get(0);

        Show actual = movieService.getShow(expected.getId()).orElseThrow();

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDateTime(), actual.getDateTime());
        assertEquals(expected.getTheatre().getName(), actual.getTheatre().getName());
        assertEquals(expected.getMovie().getTitle(), actual.getMovie().getTitle());
    }

    @Test
    void getShowReturnsEmptyForUnknownShow() {
        assertTrue(movieService.getShow(999_999L).isEmpty());
    }

    @Test
    void invalidIdsAreRejectedWithAValidationException() {
        assertThrows(ValidationException.class, () -> movieService.listShowsForMovie(0L));
        assertThrows(ValidationException.class, () -> movieService.getShow(0L));
    }
}
