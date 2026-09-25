package com.moviebooking.dao;

import com.moviebooking.model.Show;
import java.util.List;
import java.util.Optional;

public interface ShowDao {

    List<Show> findByMovieId(long movieId);

    Optional<Show> findById(long showId);
}
