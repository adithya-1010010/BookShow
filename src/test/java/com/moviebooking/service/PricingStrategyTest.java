package com.moviebooking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moviebooking.model.Movie;
import com.moviebooking.model.Show;
import com.moviebooking.model.Theatre;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class PricingStrategyTest {

    private static final Movie MOVIE = new Movie(1L, "The Last Horizon", "Sci-Fi", 180.0, 128);
    private static final Show SHOW = new Show(1L, MOVIE, new Theatre(1L, "CineNova Grand", "City Center", 3),
            LocalDateTime.of(2026, 9, 26, 10, 0), 1);

    @Test
    void standardPricingMultipliesBasePriceBySeatCount() {
        assertEquals(180.0, new StandardPricing().priceFor(SHOW, 1));
        assertEquals(360.0, new StandardPricing().priceFor(SHOW, 2));
        assertEquals(900.0, new StandardPricing().priceFor(SHOW, 5));
    }

    @Test
    void weekendPricingAppliesTwentyPercentSurcharge() {
        WeekendPricing pricing = new WeekendPricing();

        assertEquals(216.0, pricing.priceFor(SHOW, 1));
        assertEquals(648.0, pricing.priceFor(SHOW, 3));
    }

    @Test
    void bothStrategiesAreUsableThroughTheSameInterface() {
        PricingStrategy[] strategies = {new StandardPricing(), new WeekendPricing()};

        double[] totals = new double[strategies.length];
        for (int index = 0; index < strategies.length; index++) {
            totals[index] = strategies[index].priceFor(SHOW, 2);
        }

        assertEquals(360.0, totals[0]);
        assertEquals(432.0, totals[1]);
    }

    @Test
    void selectorPicksWeekendPricingOnlyForSaturdayAndSunday() {
        PricingStrategySelector selector = new PricingStrategySelector();

        assertTrue(selector.forDayOfWeek(DayOfWeek.SATURDAY) instanceof WeekendPricing);
        assertTrue(selector.forDayOfWeek(DayOfWeek.SUNDAY) instanceof WeekendPricing);
        assertTrue(selector.forDayOfWeek(DayOfWeek.MONDAY) instanceof StandardPricing);
        assertTrue(selector.forDayOfWeek(DayOfWeek.TUESDAY) instanceof StandardPricing);
        assertTrue(selector.forDayOfWeek(DayOfWeek.WEDNESDAY) instanceof StandardPricing);
        assertTrue(selector.forDayOfWeek(DayOfWeek.THURSDAY) instanceof StandardPricing);
        assertTrue(selector.forDayOfWeek(DayOfWeek.FRIDAY) instanceof StandardPricing);
    }
}
