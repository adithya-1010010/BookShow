package com.moviebooking.dao;

import com.moviebooking.model.Movie;
import java.util.List;

public interface MovieDao {

    List<Movie> findAll();
}
