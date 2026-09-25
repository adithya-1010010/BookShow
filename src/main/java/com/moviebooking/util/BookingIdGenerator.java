package com.moviebooking.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingIdGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger SHARED_SEQUENCE = new AtomicInteger();

    private final Clock clock;
    private final AtomicInteger sequence;

    public BookingIdGenerator() {
        this(Clock.systemDefaultZone(), SHARED_SEQUENCE);
    }

    public BookingIdGenerator(Clock clock) {
        this(clock, SHARED_SEQUENCE);
    }

    BookingIdGenerator(Clock clock, AtomicInteger sequence) {
        this.clock = clock;
        this.sequence = sequence;
    }

    public String generate() {
        int suffix = Math.floorMod(sequence.getAndIncrement(), 1000) + 1;
        String timestamp = LocalDateTime.now(clock).format(TIMESTAMP_FORMAT);
        return "BK-" + timestamp + "-" + String.format(Locale.ROOT, "%03d", suffix);
    }
}
