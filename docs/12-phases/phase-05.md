# Phase 05 — Business Logic (Pricing + Booking Service)

**Goal:** Core booking rules — cost calculation and duplicate-seat prevention — fully working and unit-tested, independent of any UI.
**Depends on:** Phase 4.

## Files to Create
- `service/PricingStrategy.java` (interface)
- `service/StandardPricing.java`, `service/WeekendPricing.java`
- `service/BookingService.java`
- `service/MovieService.java`
- `service/SeatUnavailableException.java`, `service/ValidationException.java`
- `util/BookingIdGenerator.java`

## Implementation Tasks
1. Implement `PricingStrategy` and both concrete strategies per `07-oop/polymorphism.md`.
2. Decide and document the exact rule for selecting a strategy per show (e.g., Saturday/Sunday show date -> WeekendPricing, else StandardPricing) — record this decision in `memory.md` if not already finalized there.
3. Implement `BookingIdGenerator.generate()` — format decided in `memory.md` (default: `BK-<yyyyMMddHHmmss>-<3-digit-seq>` unless already overridden).
4. Implement `BookingService.createBooking(Show show, List<Seat> seats, Customer customer)`:
   - Re-check seat availability via `SeatDao` immediately before writing (per `05-flows/seat-booking-flow.md`).
   - If any seat unavailable -> throw `SeatUnavailableException`, no partial write.
   - Calculate total via the active `PricingStrategy`.
   - Call `BookingDao.insertBooking(...)` inside one transaction.
   - Return the resulting `Booking` object (with generated ID).
5. Implement `MovieService.listMovies()` and `listShowsForMovie(movieId)` as thin wrappers over the relevant DAOs.

## OOP Concepts Introduced
Polymorphism (PricingStrategy implementations used interchangeably by BookingService).

## Testing
JUnit:
- TC-003, TC-004: correct total for 1 and multiple seats.
- TC-005: booking an already-booked seat throws `SeatUnavailableException`, no DB changes occur.
- TC-006: N generated booking IDs are all unique.
- TC-012: correct strategy applied based on show date.

## Expected Result
All booking business rules work correctly and are fully covered by JUnit tests, with zero UI involved.

## Completion Checklist
```
[ ] Valid booking succeeds and returns a Booking with a unique bookingId
[ ] Booking an already-booked seat is rejected with no partial state
[ ] Multi-seat cost calculation is correct
[ ] Pricing strategy selection is correct and documented in memory.md
[ ] All JUnit tests pass
```

## Documentation Updates
`memory.md`: Phase 5 -> COMPLETE; record final Booking ID format and pricing-strategy selection rule as confirmed decisions.

## Git Checkpoint
`git add . && git commit -m "phase-05: implement pricing and booking business logic" && git push`
