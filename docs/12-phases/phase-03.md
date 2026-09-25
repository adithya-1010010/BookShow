# Phase 03 — Domain Model & Seed Data

**Goal:** Core model classes implemented; seed data inserted idempotently.
**Depends on:** Phase 2.

## Files to Create
- `model/Movie.java`, `model/Theatre.java`, `model/Show.java`, `model/Seat.java`
- `model/Person.java` (abstract), `model/Customer.java` (extends Person)
- `db/SeedData.java`

## Database Changes
Insert seed rows (movies, theatres, shows, seats) — content per `08-database/schema.md` "Seed Data" section; finalize exact seed values here and record them in `memory.md`.

## Implementation Tasks
1. Implement model classes per `04-architecture/component-design.md` (fields + getters only at this stage — no DB code in models).
2. Implement `Person`/`Customer` inheritance exactly as specified in `07-oop/inheritance.md`.
3. `SeedData.seedIfEmpty()`: checks if `movie` table is empty; if so, inserts the fixed seed set (movies, theatre(s), shows, and generated seat rows per show).
4. Wire `SeedData.seedIfEmpty()` into `MainApp.start()` after `DatabaseManager.init()`.

## OOP Concepts Introduced
Class/Object (all model classes), Inheritance (Person -> Customer).

## Testing
JUnit: `Customer.getRole()` returns "CUSTOMER"; `SeedData` inserts data on first run and does NOT duplicate on a second run (run seed twice in test, assert row count unchanged after the second call).

## Expected Result
Fresh DB gets seeded automatically; re-running the app never duplicates seed rows.

## Completion Checklist
```
[ ] Seed data present after first run
[ ] Re-run does not duplicate seed data
[ ] Customer extends Person correctly and overrides getRole()
[ ] JUnit tests pass
```

## Documentation Updates
`memory.md`: Phase 3 -> COMPLETE; record exact seed dataset used (movie titles, theatre name, show times, seat grid size per show).

## Git Checkpoint
`git add . && git commit -m "phase-03: add domain models and seed data" && git push`
