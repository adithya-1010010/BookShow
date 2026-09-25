# Polymorphism

## PricingStrategy Interface

```java
public interface PricingStrategy {
    double priceFor(Show show, int seatCount);
}

public class StandardPricing implements PricingStrategy {
    public double priceFor(Show show, int seatCount) {
        return show.getMovie().getBasePrice() * seatCount;
    }
}

public class WeekendPricing implements PricingStrategy {
    private static final double SURCHARGE = 1.2;
    public double priceFor(Show show, int seatCount) {
        return show.getMovie().getBasePrice() * seatCount * SURCHARGE;
    }
}
```

`BookingService` holds a `PricingStrategy` reference and calls `priceFor(...)` polymorphically — it does not need an `if/else` on show type. Which concrete strategy is used per show is decided by a simple rule (e.g., day-of-week check) at the point `BookingService` is invoked, decided during Phase 5 and recorded in `memory.md`.

**Why this is genuine:** it solves a real extensibility problem — new pricing rules can be added as new classes without modifying `BookingService`'s existing code (Open/Closed Principle), which is a legitimate use of polymorphism, not a decorative one.
