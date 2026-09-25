# System Design

## Layers and Responsibilities

| Layer | Responsibility | Must NOT do |
|---|---|---|
| Controller (JavaFX) | Bind UI events to service calls, display results/errors | Contain business rules, SQL |
| Service | Business rules: pricing, duplicate-seat checks, booking creation | Know about JavaFX, contain SQL |
| DAO | Translate method calls into SQL, map ResultSet → model objects | Contain business rules |
| Model | Represent domain state | Contain SQL, JavaFX code |
| DB | Persist/retrieve rows | — |

## Key Design Decisions
- **Duplicate-seat prevention lives in the service layer**, re-checked against the DB at write time — not solely relied upon from UI state — satisfying NFR-004.
- **Pricing is pluggable** via `PricingStrategy` interface, so different rules (standard vs. weekend) can be swapped without touching `BookingService`'s calling code — the concrete polymorphism demonstration.
- **DatabaseManager owns the single JDBC `Connection`** for the app's lifetime (single-user, local, no need for a connection pool).

## Startup Sequence
1. `MainApp.start()` launches JavaFX.
2. `DatabaseManager.init()` creates the DB file/schema if not present.
3. `SeedData.seedIfEmpty()` inserts seed rows only if tables are empty.
4. Home scene loads.
