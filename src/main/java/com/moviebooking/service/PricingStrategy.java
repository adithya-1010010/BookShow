package com.moviebooking.service;

import com.moviebooking.model.Show;

public interface PricingStrategy {

    double priceFor(Show show, int seatCount);
}
