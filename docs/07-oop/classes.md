# Classes and Objects

Each domain entity is a class; each row loaded from SQLite becomes an object of that class at runtime (e.g., `MovieDao.findAll()` returns `List<Movie>` — a list of Movie *objects*, not raw data).

Core classes: `Movie`, `Theatre`, `Show`, `Seat`, `Person`, `Customer`, `Booking`, `Ticket`.

Object creation points:
- DAOs construct model objects from `ResultSet` rows.
- `BookingService` constructs a `Booking` object after a successful transaction.
- `Ticket.fromBooking(Booking)` constructs a display-ready `Ticket` object.
