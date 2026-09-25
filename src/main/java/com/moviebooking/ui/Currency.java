package com.moviebooking.ui;

import java.text.NumberFormat;
import java.util.Locale;

public final class Currency {

    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);

    private Currency() {
    }

    public static String format(double amount) {
        return FORMATTER.format(amount);
    }
}
