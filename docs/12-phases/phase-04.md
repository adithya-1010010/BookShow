# Phase 04 — DAO Layer

**Goal:** Full read (and write where needed) access to all entities via DAOs.
**Depends on:** Phase 3.

## Files to Create
- `dao/MovieDao.java` (interface) + `dao/SqliteMovieDao.java`
- `dao/TheatreDao.java` + `dao/SqliteTheatreDao.java`
- `dao/ShowDao.java` + `dao/SqliteShowDao.java`
- `dao/SeatDao.java` + `dao/SqliteSeatDao.java`
- `dao/BookingDao.java` + `dao/SqliteBookingDao.java`
- `dao/DataAccessException.java` (custom unchecked exception)

## Implementation Tasks
1. Define each DAO interface with only the methods actually needed by the service layer (see below) — no speculative CRUD.
   - `MovieDao.findAll()`
   - `TheatreDao.findById(id)`
   - `ShowDao.findByMovieId(movieId)`, `ShowDao.findById(id)`
   - `SeatDao.findByShowId(showId)`, `SeatDao.markBooked(seatIds, connection)` (transactional, used by BookingDao)
   - `BookingDao.insertBooking(booking, seatIds)` (transactional: insert booking row + booking_seat rows + mark seats booked, all-or-nothing)
2. Implement each `Sqlite*Dao` using `PreparedStatement`, mapping `ResultSet` rows to model objects.
3. Wrap all `SQLException` in `DataAccessException` — never let raw `SQLException` propagate out of `dao/`.

## OOP Concepts Introduced
Interface-based polymorphism (DAO interfaces vs. SQLite implementations); Encapsulation (SQL hidden behind DAO methods).

## Testing
JUnit integration tests against a temporary/test SQLite file (or in-memory DB, seeded manually per test): each DAO's read methods return expected data; `BookingDao.insertBooking` correctly marks seats booked in the same transaction; a forced failure mid-transaction leaves no partial state (rollback verified).

## Expected Result
Every entity can be read from and (where applicable) written to the database through its DAO, with no SQL outside `dao/`.

## Completion Checklist
```
[ ] MovieDao.findAll() returns seeded movies
[ ] ShowDao.findByMovieId() returns correct shows
[ ] SeatDao.findByShowId() returns correct seat states
[ ] BookingDao.insertBooking() correctly persists booking+seats atomically
[ ] Rollback verified on simulated failure
[ ] JUnit tests pass
```

## Documentation Updates
`memory.md`: Phase 4 -> COMPLETE.

## Git Checkpoint
`git add . && git commit -m "phase-04: implement DAO layer for all entities" && git push`
