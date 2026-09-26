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
Phase  6 (UI: Movies/Shows)          — COMPLETE
Phase  7 (UI: Seat Selection)        — COMPLETE (committed; visual checks unverified, see below)
Phase  8 (UI: Customer/Confirmation) — NOT STARTED
Phase  9 (Validation/Error Handling) — NOT STARTED
Phase 10 (Final Testing/Polish)      — NOT STARTED
```

**Uncommitted work in the working tree:** `views/CustomerDetails.fxml` + `controller/CustomerDetailsController.java`
exist as a deliberate Phase 6-style placeholder stub (a `placeholderLabel` reading "Form is being
built." and a Back button), so the seat screen's Continue has a valid navigation target. They are
Phase 8 work-in-progress and must be replaced, not extended, when Phase 8 starts. `Screens.CONFIRMATION`
already declares a path for a `Confirmation.fxml` that does not exist yet, so
`SceneNavigator.goTo(Screens.CONFIRMATION)` currently throws `IllegalStateException`.

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
- Phase 6 added a `ui/` support package that is shared by all screens: `Screens` (FXML path constants + window size), `SceneNavigator` (static navigator that owns the `Stage`, builds scenes, attaches `styles.css` once per scene, and plays a 220 ms fade), `BookingSession` (singleton in-memory state carrying the selected movie/show/seats between screens and the resulting booking code), `AppServices` (singleton service registry so FXML-instantiated controllers can receive services without a DI framework), and `Currency` (single `NumberFormat` for USD display).
- `styles.css` implements the dark cinematic theme from `09-ui/ui-design.md`: base `#0b0f16`, surfaces `#151b26`/`#1c2431`, amber accent `#f5a524`, defined **once** as looked-up colors on the `.root` selector and referenced everywhere else (JavaFX has no CSS custom properties; looked-up colors are its equivalent).
- `MovieListController` builds true card nodes in Java inside a `FlowPane` (poster block with initials glyph, title, genre chip, price, duration, "View Shows" button) because `fx:repeat` only works with `ListView`/`TableView` and the design calls for a card grid, not a list.
- `ShowSelectionController` builds single-select show rows as `ToggleButton`s sharing a `ToggleGroup`, each holding its `Show` as `userData`; the Continue button stays disabled until a row is selected (TC-011).
- Phase 6 included a **sanctioned stub** `SeatSelection.fxml`/`SeatSelectionController` so Continue has a valid navigation target; Phase 7 replaces it with the real seat map.
- Phase 6 Problems and Resolutions: FXML cannot place a `ToggleGroup` as a child node ("Unable to coerce ToggleGroup to class javafx.scene.Node"), so the group is created in the controller instead; and `ToggleGroup` pre-selects its first toggle, so `showGroup.selectToggle(null)` is called after the rows are built to guarantee nothing is selected on arrival. Also corrected a wrong `setFullWidth`/`setSelectedToggle` assumption (those are not `ToggleButton`/`ToggleGroup` APIs — use `setMaxWidth`/`selectToggle`).
- UI verification approach: since this session cannot view images, screens were verified with a **throwaway JavaFX snapshot harness kept outside the repo** (`/tmp/bsharness/`) that loads each FXML, applies `styles.css`, and writes a PNG. A pixel analysis confirmed the theme renders (Home: 98% base background + accent text/button; Movie List: cards, poster blocks and accent buttons; Show Selection: header/footer surfaces and show rows, and correctly *no* accent because nothing is selected). The harness is never committed.
- Phase 7 replaced the Phase 6 `SeatSelection` stub with a real seat map. `SeatSelectionController.initialize()` reads the chosen `Show` from `BookingSession` and **bounces to `Screens.SHOW_SELECTION` if it is null**, so the screen is safe to reach without a prior show selection. Seats come from `BookingService.getSeatsForShow(showId)` (never from `SeatDao` directly), grouped by `row_label` with `LinkedHashMap` to keep A-E order, sorted by `column_number`, and added to a `GridPane` whose column 0 holds the row label.
- Each seat is a `ToggleButton` styled `seat`, holding its `Seat` as `userData` and `setDisable(seat.isBooked())` for booked seats. Selection/deselection updates the `BookingSession` seat list and the footer; `Continue` is disabled at 0 seats. Running total is `BookingService.calculateTotal(show, count)` rendered through `ui/Currency`, so the seat screen shows the same weekend-surcharge total the booking will actually be charged.
- **`booking.setShow(...)` clears the selected-seat list** (`BookingSession.setShow` calls `selectedSeats.clear()`), and `handleBack` on the seat screen also clears it. Re-picking a show therefore never carries stale seats forward — do not add caching here without re-checking this.
- Phase 7 needed **no `styles.css` change**: Phase 6 had already committed the full theme, including the seat-state classes (`.seat`, `.seat:selected`, `.seat:disabled`, `.seat-legend`, `.legend-swatch` + `.legend-available/-selected/-booked`, `.screen-line`, `.scroll-pane`) and, ahead of time, the Phase 8 classes (`.text-field`, `.summary-panel`, `.ticket`, `.ticket-code`, `.banner`). `phase-07.md` predicted styles would need extending; they did not. Expect the same for Phase 8.
- Phase 7 process note: the work existed in the working tree uncommitted while this file still said `NOT STARTED`. Per `implementation-plan.md` Section 0 that was reported rather than acted on silently, then reconciled (dead field removed, visual checks recorded as unverified, this file updated) before committing. A dead `Map<Long, ToggleButton> seatButtons` field that was written but never read was removed during the reconciliation.

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

Previous decision: `controller/` would be instantiated with no dependencies and reach services ad hoc.
New decision: FXML controllers read services from the `ui/AppServices` singleton and navigation state from the `ui/BookingSession` singleton; both are wired once in `MainApp.start()`.
Reason: `FXMLLoader` instantiates controllers through their no-arg constructors, so constructor injection is not available without a DI framework (none is confirmed for this project). A single composition root in `MainApp` keeps construction centralised and keeps `controller/` free of SQL and of `dao/` access, preserving UI -> Service -> DAO.
Date/Phase: 2026-09-25 / Phase 6
Affected components: `ui/`, `MainApp.java`, all `controller/` classes
```

## Open Questions (must be resolved before the relevant phase begins)
_None. Both previously open questions (Booking ID format, weekend-pricing rule) were resolved and recorded in Phase 5._

## Outstanding Verification (must be closed before Phase 8 is called COMPLETE)
- **Phase 7 seat-screen visuals are UNVERIFIED.** The implementation was committed without a
  human eye on it, because the session that wrote it could not launch the GUI headlessly. The user
  accepted this. Unchecked items from the `phase-07.md` completion checklist:
  1. seat grid lays out correctly (row label column, seats A1-E6, `SCREEN` bar) for a real show,
  2. booked seats are visibly dimmed **and** non-selectable,
  3. multi-seat select/deselect updates count + total and stays consistent,
  4. Continue is disabled at 0 seats and enabled at >= 1.
  Everything that *can* be checked headlessly was checked: sources compile and `mvn test` is
  49/49 green, every `styleClass` referenced by `SeatSelection.fxml` exists in `styles.css`, and
  `ShowSelectionController` populates `BookingSession.setShow(...)` that the seat screen consumes.

## Rules for Future Development (Anti-Hallucination)
1. Do not invent requirements not present in `context.md` or this file.
2. Do not contradict confirmed architecture in `docs/04-architecture/architecture.md`.
3. Do not change technology (framework, DB, build tool) without explicit user confirmation recorded here first.
4. Always check this file before making any architectural or design decision.
5. Always check the previous phase's `docs/12-phases/phase-NN.md` and its COMPLETE status before starting a new phase.
6. If a decision is ambiguous and not resolvable from `context.md` / this file / `requirements.md` / `architecture.md`, STOP and add it to "Open Questions" above rather than guessing.
7. Update this file after every meaningful decision — this file must never fall out of sync with reality.
