package com.moviebooking.dao;

import com.moviebooking.model.Theatre;
import java.util.Optional;

public interface TheatreDao {

    Optional<Theatre> findById(long theatreId);
}
