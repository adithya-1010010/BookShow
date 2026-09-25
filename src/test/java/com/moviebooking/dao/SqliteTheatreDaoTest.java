package com.moviebooking.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviebooking.db.TestDatabase;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Theatre;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SqliteTheatreDaoTest {

    @TempDir
    private Path tempDirectory;

    private TheatreDao theatreDao;

    @BeforeEach
    void setUp() throws SQLException {
        TestDatabase.initSeeded(tempDirectory.resolve("theatre-dao-test.db"));
        theatreDao = new SqliteTheatreDao();
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabase.close();
    }

    @Test
    void findByIdReturnsTheSeededTheatre() {
        Movie movie = new SqliteMovieDao().findAll().get(0);
        long theatreId = new SqliteShowDao().findByMovieId(movie.getId()).get(0).getTheatre().getId();

        Optional<Theatre> theatre = theatreDao.findById(theatreId);

        assertTrue(theatre.isPresent());
        assertEquals("CineNova Grand", theatre.get().getName());
        assertEquals("City Center", theatre.get().getLocation());
        assertEquals(3, theatre.get().getScreenCount());
    }

    @Test
    void findByIdReturnsEmptyForUnknownTheatre() {
        assertTrue(theatreDao.findById(999_999L).isEmpty());
    }
}
