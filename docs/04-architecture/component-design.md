# Component / Class Design

| Class | Responsibility | Key Fields | Key Methods | Depends On | OOP Concept |
|---|---|---|---|---|---|
| `Movie` | Movie data | id, title, genre, basePrice, durationMins | getters | — | Class/Object |
| `Theatre` | Theatre data | id, name, location, screenCount | getters | — | Class/Object |
| `Show` | A screening | id, movie, theatre, dateTime, screenNumber | getters | Movie, Theatre | Association |
| `Seat` | A bookable seat for a show | id, showId, row, column, booked | isAvailable() | — | Class/Object |
| `Person` (abstract) | Shared identity | name, phone | abstract getRole() | — | Inheritance base |
| `Customer extends Person` | Booking customer | (inherits) | getRole() override | Person | **Inheritance** |
| `Booking` | A completed booking | id, bookingId, show, customer, seats, totalCost, createdAt | — | Show, Customer, Seat | Aggregation |
| `Ticket` | Display-ready booking summary | bookingId, movieTitle, showInfo, seatList, totalCost | display() | Booking | Class/Object |
| `PricingStrategy` (interface) | Cost calculation contract | — | `double priceFor(Show show, int seatCount)` | — | **Polymorphism base** |
| `StandardPricing` | Default pricing | — | priceFor(...) | PricingStrategy | Polymorphism |
| `WeekendPricing` | Weekend surcharge pricing | — | priceFor(...) | PricingStrategy | Polymorphism |
| `MovieService` | Movie/show read operations | movieDao, showDao | listMovies(), listShowsForMovie(id) | MovieDao, ShowDao | Encapsulation |
| `BookingService` | Orchestrates booking creation | seatDao, bookingDao, pricingStrategy | createBooking(...), validateSeats(...) | SeatDao, BookingDao, PricingStrategy | Polymorphism (via strategy), Encapsulation |
| `*Dao` (interfaces) | Persistence contracts per entity | — | CRUD/read methods | Model | Interface-based polymorphism |
| `Sqlite*Dao` | JDBC implementations | Connection | implements interface methods | DatabaseManager | Polymorphism, Encapsulation |
| `DatabaseManager` | Connection + schema lifecycle | connection | init(), getConnection() | — | Encapsulation |
| `SeedData` | Idempotent initial data insert | — | seedIfEmpty() | DAOs | — |
| `BookingIdGenerator` | Unique ID generation | — | generate() | — | Utility |

Full narrative justification for each OOP concept is in `07-oop/oop-design.md`.
