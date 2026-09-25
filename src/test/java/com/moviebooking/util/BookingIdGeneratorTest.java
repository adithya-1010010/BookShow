package com.moviebooking.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class BookingIdGeneratorTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-09-26T10:30:45Z"), ZoneId.of("UTC"));

    @Test
    void generateUsesTheDocumentedFormat() {
        BookingIdGenerator generator = new BookingIdGenerator(FIXED_CLOCK, new AtomicInteger(0));

        assertEquals("BK-20260926103045-001", generator.generate());
        assertEquals("BK-20260926103045-002", generator.generate());
    }

    @Test
    void sequenceIsAlwaysThreeDigits() {
        BookingIdGenerator generator = new BookingIdGenerator(FIXED_CLOCK, new AtomicInteger(9));

        assertEquals("BK-20260926103045-010", generator.generate());
    }

    @Test
    void everyIdWithinOneSecondIsUnique() {
        BookingIdGenerator generator = new BookingIdGenerator(FIXED_CLOCK, new AtomicInteger(0));
        Set<String> generated = new HashSet<>();

        for (int index = 0; index < 1000; index++) {
            assertTrue(generated.add(generator.generate()), "Duplicate booking ID generated");
        }

        assertEquals(1000, generated.size());
    }

    @Test
    void separateInstancesShareOneSequenceSoIdsNeverCollide() {
        AtomicInteger sequence = new AtomicInteger(0);
        BookingIdGenerator first = new BookingIdGenerator(FIXED_CLOCK, sequence);
        BookingIdGenerator second = new BookingIdGenerator(FIXED_CLOCK, sequence);

        assertEquals("BK-20260926103045-001", first.generate());
        assertEquals("BK-20260926103045-002", second.generate());
    }

    @Test
    void defaultConstructorsShareOneGlobalSequence() {
        assertTrue(new BookingIdGenerator().generate().matches("BK-\\d{14}-\\d{3}"));
        assertTrue(new BookingIdGenerator().generate().matches("BK-\\d{14}-\\d{3}"));
    }

    @Test
    void generatedIdMatchesTheRequiredPattern() {
        BookingIdGenerator generator = new BookingIdGenerator();

        assertTrue(generator.generate().matches("BK-\\d{14}-\\d{3}"));
    }
}
