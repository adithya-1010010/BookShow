package com.moviebooking.service;

import java.time.DayOfWeek;

public final class PricingStrategySelector {

    private final PricingStrategy standardPricing;
    private final PricingStrategy weekendPricing;

    public PricingStrategySelector() {
        this(new StandardPricing(), new WeekendPricing());
    }

    public PricingStrategySelector(PricingStrategy standardPricing, PricingStrategy weekendPricing) {
        this.standardPricing = standardPricing;
        this.weekendPricing = weekendPricing;
    }

    public PricingStrategy forDayOfWeek(DayOfWeek dayOfWeek) {
        boolean weekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        return weekend ? weekendPricing : standardPricing;
    }
}
