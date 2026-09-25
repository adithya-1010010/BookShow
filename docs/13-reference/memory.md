# MEMORY.md — Persistent Project Memory (Read This First, Every Session)

> This file is the single source of truth for project state. Any AI or developer session
> (Claude, OpenCode, or human) MUST read this file before making any architectural or
> implementation decision. If this file conflicts with anything remembered from a prior
> conversation, THIS FILE WINS.

---

## Project Identity
**Movie Ticket Booking System** — Java 17 / JavaFX desktop application, academic OOP project.
Repository: https://github.com/adithya-1010010/BookShow.git
Source-of-truth requirements doc: `context.md` (repo root).

---

## Confirmed Requirements
See `docs/02-requirements/requirements.md` for the full FR/NFR table. Summary:
- Browse movies, genres, prices
- View theatres/show timings
- Select show + seats
- Enter customer details
- Calculate ticket cost
- Prevent duplicate seat bookings
- Confirm booking, generate Booking ID, show ticket details
- Data persists across restarts (SQLite)
- Data is seeded automatically, no admin UI

## Confirmed Technical Decisions
```
Java 17
JavaFX
Maven
SQLite (plain JDBC, org.xerial:sqlite-jdbc — no ORM)
JUnit 5
Single-user, local, no authentication/login
No payment processing
No VIP/seat categories
No ticket printing/export — on-screen confirmation only
No admin management UI
GitHub: https://github.com/adithya-1010010/BookShow.git
```

**Note on Authentication/Login:** The user reconfirmed before Phase 1 that this is a single-user local desktop app with **NO login or authentication**. No login page or authentication code is permitted.

## UI Decision (added this session)
The UI must be a genuinely polished, premium-feeling interface — explicitly targeting a bar above a typical academic Swing/JavaFX form, comparable in visual quality to a commercial movie-booking app. This does NOT change scope/features — same 6 screens, same flows — it raises the execution bar for styling (dark theme + accent color, card-based movie list, a well-designed seat grid with clear state legend, consistent shared stylesheet, subtle transitions). Full detail: `docs/09-ui/ui-design.md`. Do not use this as license to add new screens or features not in `requirements.md`.

## Architecture Decisions
- Layered: UI (JavaFX/FXML Controllers) -> Service -> DAO -> SQLite. Strict one-way dependency (see `docs/04-architecture/architecture.md`).
- No ORM — plain JDBC via DAOs, chosen as "simplest appropriate approach" per confirmed decision.
- Single shared JDBC `Connection` for app lifetime (justified by single-user/local constraint).
- Multi-table writes (booking creation) always wrapped in one JDBC transaction.

## OOP Decisions
- Inheritance: `Person` (abstract) -> `Customer`. Justification: shared identity fields, genuine single-level hierarchy, not forced. See `docs/07-oop/inheritance.md`.
- Polymorphism: `PricingStrategy` interface -> `StandardPricing`, `WeekendPricing`, invoked polymorphically by `BookingService`. See `docs/07-oop/polymorphism.md`.
- Do not add further inheritance/polymorphism beyond these unless a genuine new design need arises — do not force OOP concepts.

## Database Decisions
- Runtime database file: `./data/moviebooking.db`; `DatabaseManager` creates its parent directory and schema automatically.
- 6 tables: `movie`, `theatre`, `show`, `seat`, `booking`, `booking_seat`. Full DDL in `docs/08-database/schema.md`.
- The shared JDBC connection enables SQLite foreign-key enforcement with `PRAGMA foreign_keys = ON`.
- Duplicate-seat prevention enforced at the DB layer via conditional `UPDATE ... WHERE booked = 0` + transaction rollback on any failed row — not just app-level checks.
- Exact seed dataset finalized in Phase 3:
  - Movies: `The Last Horizon` (Sci-Fi, 180.0, 128 min), `Midnight Echoes` (Thriller, 160.0, 112 min), `Beyond the Blue` (Adventure, 140.0, 136 min).
  - Theatre: `CineNova Grand`, `City Center`, 3 screens.
  - Shows: 6 total, using `firstShowDate = first-run local date + 1 day`; 10:00/14:00 on day 1, 11:00/17:00 on day 2, 13:00/19:00 on day 3, with the movie/screen assignments recorded in `docs/08-database/schema.md`.
  - Seats: rows `A`-`E`, columns 1-6, 30 per show and 180 total, all start unbooked.
- Booking ID format is **FINALIZED in Phase 5**: `BK-<yyyyMMddHHmmss>-<3-digit sequence>`, e.g. `BK-20260926103045-001`. Produced by `util/BookingIdGenerator`; the sequence is a process-wide `AtomicInteger` shared by all generator instances, so IDs are unique per (timestamp, sequence) with 1000 IDs/second capacity. The `booking.booking_code` UNIQUE constraint is the final backstop and would fail the insert loudly on any collision.
- Weekend-pricing selection rule is **FINALIZED in Phase 5**: a show whose `show_datetime` falls on **Saturday or Sunday** uses `WeekendPricing` (`basePrice x seatCount x 1.2`); every other day uses `StandardPricing` (`basePrice x seatCount`). The rule lives in `service/PricingStrategySelector.forDayOfWeek(DayOfWeek)`; `BookingService` calls it and never branches on day type itself, preserving the polymorphism goal in `07-oop/polymorphism.md`.
- `WeekendPricing` has a second constructor taking an explicit surcharge (default `1.2`) so the surcharge can be varied in tests without changing production behaviour.

## Phase Status
```
Phase  0 (Documentation & Planning) — COMPLETE
Phase  1 (Project Foundation)        — COMPLETE
Phase  2 (Database Schema)           — COMPLETE
Phase  3 (Domain Model + Seed Data)  — COMPLETE
Phase  4 (DAO Layer)                 — COMPLETE
Phase  5 (Business Logic)            — COMPLETE
Phase  6 (UI: Movies/Shows)          — NOT STARTED
Phase  7 (UI: Seat Selection)        — NOT STARTED
Phase  8 (UI: Customer/Confirmation) — NOT STARTED
Phase  9 (Validation/Error Handling) — NOT STARTED
Phase 10 (Final Testing/Polish)      — NOT STARTED
```

## Important Discoveries
- The project is greenfield in `/Users/adithyar/Desktop/java_project`; the user supplied the empty `https://github.com/adithya-1010010/BookShow.git` repository as its remote.
- The user reconfirmed that the application has no login or authentication.
- Phase 1 pins JavaFX `17.0.20`, SQLite JDBC `3.53.4.0`, JUnit Jupiter `5.14.4`, and JavaFX Maven plugin `0.0.8`.
- Local Phase 1 verification used the available Maven JDK 26 runtime while `maven.compiler.release=17` kept the project bytecode target at Java 17.
- Phase 2 creates `./data/moviebooking.db` before the Home scene loads; manual first-run and re-run checks confirmed all 6 tables exist and remain empty.
- `DatabaseManagerTest` uses an isolated temporary SQLite file and verifies the exact table/column sets, empty initial rows, shared connection reuse, foreign-key enforcement, and repeated initialization.
- Phase 3 added immutable `Movie`, `Theatre`, `Show`, and `Seat` model classes plus abstract `Person` -> `Customer` inheritance; models contain no database code.
- `SeedData` inserts the finalized dataset in one JDBC transaction and returns without changes when `movie` already contains rows; manual first-run and re-run checks confirmed 3/1/6/180 movie/theatre/show/seat counts both times.
- Phase 4 added `model/Booking.java` (immutable, carries show + customer + seats + total + createdAt) because `BookingDao.insertBooking` needs it; it was deferred out of Phase 3 and is listed in the architecture's `model/` package.
- Phase 4 DAO layer: 5 interfaces (`MovieDao`, `TheatreDao`, `ShowDao`, `SeatDao`, `BookingDao`) + 5 `Sqlite*Dao` impls + unchecked `DataAccessException`. `Sqlite*Dao` map `ResultSet` rows to model objects via `PreparedStatement`; no raw `SQLException` escapes `dao/`.
- `SqliteShowDao` JOINs `show`+`movie`+`theatre` in one query so `Show` objects are returned fully populated (title/price for pricing, theatre name for confirmation) without extra round-trips.
- `SqliteBookingDao.insertBooking` runs the whole booking in ONE transaction: insert `booking` row -> `SeatDao.markBooked` (conditional `UPDATE ... WHERE id = ? AND booked = 0`) -> insert `booking_seat` rows -> commit. If the conditional update affects fewer rows than requested, it throws `DataAccessException` and rolls back, so duplicate-seat booking is impossible at the DB layer. `BookingService` (Phase 5) additionally re-checks availability and throws `SeatUnavailableException` for the user-facing path.
- Phase 4 tests (JUnit, isolated temp SQLite via test-only `com.moviebooking.db.TestDatabase`): movie/show/seat reads, show JOIN correctness, and booking insert + two rollback cases (already-booked seat, unknown seat id) proving no partial state. 20 tests total, all passing.
- Phase 5 added `service/` (`PricingStrategy`, `StandardPricing`, `WeekendPricing`, `PricingStrategySelector`, `MovieService`, `BookingService`, `SeatUnavailableException`, `ValidationException`) and `util/BookingIdGenerator`. `service/` depends only on `dao/` + `model/` + `util/`, never on JavaFX.
- `BookingService.createBooking(show, seats, customer)` performs a two-stage guard: (1) service-level re-check via `SeatDao.findByShowId` immediately before writing -> `SeatUnavailableException`; (2) DB-level conditional `UPDATE ... WHERE booked = 0` inside the transaction -> `SeatConflictException`, rolled back. Only after both checks does it price the booking and call `BookingDao.insertBooking`.
- `BookingService` also rejects empty seat lists, null show/customer, non-positive show ids, and seats belonging to a different show, all via `ValidationException`.
- Two extra classes beyond the Phase 5 file list were added deliberately: `service/PricingStrategySelector` (encapsulates the day-of-week rule so `BookingService` stays free of `if/else` on strategy type) and `dao/SeatConflictException extends DataAccessException` (lets the service convert a DB-level seat conflict into `SeatUnavailableException` without fragile message string-matching).
- Phase 5 tests cover TC-003, TC-004, TC-005, TC-006, TC-012 plus validation edge cases. 49 tests total, all passing.
- Booking totals are stored as raw doubles; **display formatting to 2 decimal places is a UI-layer concern** (Phase 6-8), so no rounding is applied in the service or DAO.

## Problems and Resolutions
- The first Phase 1 launch exposed a missing `fx` namespace declaration in `Home.fxml`. Adding the JavaFX FXML namespace resolved it; `mvn clean install` and `mvn clean javafx:run` then succeeded, and the Home window was manually verified and closed cleanly.
- The first Phase 3 seed test expected `HH:mm` while SQLite JDBC returned equivalent zero-second ISO-8601 values as `HH:mm:ss`; the exact-value assertion now uses the normalized representation.
- Phase 4 `SqliteBookingDao` initially failed to compile because `connection.getAutoCommit()` throws `SQLException` and the read sat outside the try block. The method was restructured into `insertBooking` (reads/restores auto-commit, wrapping SQL failures in `DataAccessException`) plus a private `runInTransaction` (commit/rollback logic), which keeps the transaction boundaries explicit and compiles cleanly.
- Phase 5 tests initially failed for two reasons worth remembering: (1) the Booking ID test wrongly expected a dash inside the timestamp (`BK-20260926-103045-001`) when the documented format has none (`BK-20260926103045-001`); (2) booking-total assertions hard-coded `180.0` while the DAO orders movies by title, so `findAll().get(0)` is "Beyond the Blue" at `140.0`, and because `firstShowDate` lands on a weekend the surcharge applied. Fixed by asserting persisted totals against `bookingService.calculateTotal(...)` (the persistence check is the point of those tests) and by keeping the date-dependent pricing rules in dedicated tests that construct `Show` objects with fixed dates.

## Changes to Previous Decisions
```
Previous decision: Repository: https://github.com/adithya-1010010/java_project.git
New decision: Repository: https://github.com/adithya-1010010/BookShow.git
Reason: The user confirmed this is a new greenfield project and explicitly supplied the empty BookShow repository for its history and push target.
Date/Phase: 2026-09-25 / Phase 1
Affected components: Git remote and repository links in project documentation

Previous decision: SeedData depends on DAO implementations.
New decision: SeedData uses the shared DatabaseManager JDBC connection directly.
Reason: The DAO layer is intentionally delivered in Phase 4, while seed bootstrap must run during Phase 3 startup; keeping the SQL inside the db bootstrap class preserves the one-way UI -> Service -> DAO -> Database layering for application features.
Date/Phase: 2026-09-25 / Phase 3
Affected components: `db/SeedData.java` and `04-architecture/component-design.md`

Previous decision: Pricing-strategy selection would be decided by an if/else at the point BookingService is invoked.
New decision: The rule is encapsulated in `service/PricingStrategySelector.forDayOfWeek(DayOfWeek)`, which BookingService calls.
Reason: `07-oop/polymorphism.md` explicitly requires that BookingService call `priceFor(...)` polymorphically without an `if/else` on show type. Keeping the rule in a separate selector honours that goal and makes the rule independently unit-testable.
Date/Phase: 2026-09-25 / Phase 5
Affected components: `service/PricingStrategySelector.java`, `service/BookingService.java`, `04-architecture/component-design.md`
```

## Open Questions (must be resolved before the relevant phase begins)
_None. Both previously open questions (Booking ID format, weekend-pricing rule) were resolved and recorded in Phase 5._

## Rules for Future Development (Anti-Hallucination)
1. Do not invent requirements not present in `context.md` or this file.
2. Do not contradict confirmed architecture in `docs/04-architecture/architecture.md`.
3. Do not change technology (framework, DB, build tool) without explicit user confirmation recorded here first.
4. Always check this file before making any architectural or design decision.
5. Always check the previous phase's `docs/12-phases/phase-NN.md` and its COMPLETE status before starting a new phase.
6. If a decision is ambiguous and not resolvable from `context.md` / this file / `requirements.md` / `architecture.md`, STOP and add it to "Open Questions" above rather than guessing.
7. Update this file after every meaningful decision — this file must never fall out of sync with reality.
