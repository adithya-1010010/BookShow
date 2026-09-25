# Assumptions and Constraints

## Assumptions (Derived, not invented — logically required to satisfy confirmed requirements)
- A2FR-A: "OOP Module ... in JavaScript" in context.md is treated as a documentation typo; the confirmed technology stack is Java. **This must be reconfirmed by the user before Phase 1 if any doubt remains.**
- A2: Seed data size is small (a handful of movies/theatres/shows) — sufficient to demonstrate functionality, not a production catalog.
- A2: "Simple row/column seat grid" implies a 2D grid per show, addressed by (row, column) coordinates, with a boolean booked/available state.
- A2: Booking ID format was unspecified — finalized in Phase 5 as `BK-<yyyyMMddHHmmss>-<3-digit-seq>` and recorded in `memory.md`.

## Constraints (from confirmed decisions)
- Must use Java 17, JavaFX, Maven, SQLite, JUnit 5 — no substitutions.
- No admin UI, no login, no payment, no VIP seats, no ticket export — these are explicitly out of scope and must NOT be silently added.
- Database access must use the simplest appropriate approach (plain JDBC) — no ORM (e.g. no Hibernate/JPA) unless the user later confirms otherwise.

## Explicit Unknowns (must be resolved before the relevant phase, not guessed)
_None outstanding. Both previously open items (Booking ID format, weekend-pricing rule) were resolved during Phase 5 and recorded in `memory.md`._
