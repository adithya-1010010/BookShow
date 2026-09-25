package com.moviebooking.service;

import com.moviebooking.model.Show;

public class WeekendPricing implements PricingStrategy {

    private static final double SURCHARGE = 1.2;

    private final double surcharge;

    public WeekendPricing() {
        this(SURCHARGE);
    }

    public WeekendPricing(double surcharge) {
        this.surcharge = surcharge;
    }

    @Override
    public double priceFor(Show show, int seatCount) {
        return show.getMovie().getBasePrice() * seatCount * surcharge;
    }
}
