package com.moviebooking.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviebooking.db.TestDatabase;
import com.moviebooking.model.Movie;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SqliteMovieDaoTest {

    @TempDir
    private Path tempDirectory;

    private MovieDao movieDao;

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.initSeeded(tempDirectory.resolve("movie-dao-test.db"));
        movieDao = new SqliteMovieDao();
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabase.close();
    }

    @Test
    void findAllReturnsEverySeededMovie() {
        List<Movie> movies = movieDao.findAll();

        assertEquals(3, movies.size());
        assertEquals(
                List.of("Beyond the Blue", "Midnight Echoes", "The Last Horizon"),
                movies.stream().map(Movie::getTitle).toList());
        assertTrue(movies.stream().allMatch(movie -> movie.getId() > 0));
    }

    @Test
    void findAllMapsEveryColumnToTheModel() {
        Movie movie = movieDao.findAll().stream()
                .filter(candidate -> candidate.getTitle().equals("The Last Horizon"))
                .findFirst()
                .orElseThrow();

        assertEquals("Sci-Fi", movie.getGenre());
        assertEquals(180.0, movie.getBasePrice());
        assertEquals(128, movie.getDurationMins());
    }
}
