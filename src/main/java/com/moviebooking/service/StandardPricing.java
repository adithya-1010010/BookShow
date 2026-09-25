package com.moviebooking.service;

import com.moviebooking.model.Show;

public class StandardPricing implements PricingStrategy {

    @Override
    public double priceFor(Show show, int seatCount) {
        return show.getMovie().getBasePrice() * seatCount;
    }
}
