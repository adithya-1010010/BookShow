# Database Design — Overview

SQLite, single local file (e.g. `data/moviebooking.db`), created/managed by `DatabaseManager`.

## Tables
- `movie` — movie catalog
- `theatre` — theatre catalog
- `show` — a screening: movie + theatre + datetime + screen number
- `seat` — a bookable seat, scoped to a specific show
- `booking` — a confirmed booking (customer + show + total cost + generated ID)
- `booking_seat` — join table: which seats belong to which booking

## How Duplicate Seat Booking Is Prevented
1. `seat.booked` is a boolean column, defaulting to 0 (false).
2. Booking a seat is an atomic transaction: `UPDATE seat SET booked = 1 WHERE id = ? AND booked = 0`, checked against affected-row-count.
3. If the affected row count is 0 for any seat in the batch, the whole transaction is rolled back and `BookingService` throws `SeatUnavailableException` — no partial booking can occur.
4. This means the check-then-act race is closed at the database level (via the `WHERE booked = 0` guard), not just checked in application code beforehand.

See `schema.md` for full DDL and `data-flow.md` for how data moves through the layers.
