package com.moviebooking.service;

import com.moviebooking.dao.MovieDao;
import com.moviebooking.dao.ShowDao;
import com.moviebooking.model.Movie;
import com.moviebooking.model.Show;
import java.util.List;
import java.util.Optional;

public class MovieService {

    private final MovieDao movieDao;
    private final ShowDao showDao;

    public MovieService(MovieDao movieDao, ShowDao showDao) {
        this.movieDao = movieDao;
        this.showDao = showDao;
    }

    public List<Movie> listMovies() {
        return movieDao.findAll();
    }

    public List<Show> listShowsForMovie(long movieId) {
        if (movieId <= 0) {
            throw new ValidationException("A valid movie must be selected");
        }
        return showDao.findByMovieId(movieId);
    }

    public Optional<Show> getShow(long showId) {
        if (showId <= 0) {
            throw new ValidationException("A valid show must be selected");
        }
        return showDao.findById(showId);
    }
}
