# Phase 02 — Database Schema & Connection Management

**Goal:** SQLite database file + full schema created automatically on startup.
**Depends on:** Phase 1.

## Files to Create
- `src/main/java/com/moviebooking/db/DatabaseManager.java`
- Schema SQL embedded in `DatabaseManager` or as a resource file `schema.sql`

## Database Changes
Create all tables per `08-database/schema.md`: `movie`, `theatre`, `show`, `seat`, `booking`, `booking_seat`.

## Implementation Tasks
1. `DatabaseManager.init()`: opens/creates `data/moviebooking.db`, runs `CREATE TABLE IF NOT EXISTS` for all 6 tables.
2. `DatabaseManager.getConnection()`: exposes the single shared connection for DAOs (Phase 4+).
3. Wire `DatabaseManager.init()` into `MainApp.start()` before the UI loads.

## OOP Concepts Introduced
Encapsulation (DatabaseManager hides connection/schema details from the rest of the app).

## Testing
JUnit: `DatabaseManagerTest` — calling `init()` twice does not throw and tables exist both times (idempotency).

## Expected Result
Running the app creates `data/moviebooking.db` with all 6 empty tables; re-running does not error or duplicate schema.

## Completion Checklist
```
[ ] DB file created on first run
[ ] All 6 tables exist with correct columns (spot-check via sqlite3 CLI or a quick test query)
[ ] Re-running app does not throw or duplicate schema
[ ] JUnit tests pass
```

## Documentation Updates
`memory.md`: Phase 2 -> COMPLETE; record actual DB file path if it differs from the plan.

## Git Checkpoint
`git add . && git commit -m "phase-02: add SQLite schema and DatabaseManager" && git push`
