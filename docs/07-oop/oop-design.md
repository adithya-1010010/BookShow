# OOP Design — Summary

Every OOP concept below is used because it solves a real design problem in this application — none are added purely to satisfy the academic requirement.

| Concept | Class(es) | Real Problem It Solves |
|---|---|---|
| Class / Object | Movie, Theatre, Show, Seat, Booking, Ticket, Customer | Represent real-world entities with state (a movie IS a title+genre+price, not just data floating around) |
| Inheritance | `Person` (abstract) -> `Customer` | Models the fact that a "Customer" is fundamentally a person with a name/phone — allows future extension (e.g. Staff extends Person) without touching Customer |
| Polymorphism | `PricingStrategy` interface -> `StandardPricing`, `WeekendPricing` | `BookingService` calculates cost via `pricingStrategy.priceFor(show, seatCount)` without knowing which concrete pricing rule is active — enables adding new pricing rules without modifying `BookingService` |
| Encapsulation | All model classes (private fields), all DAOs (SQL hidden behind interfaces) | Protects internal state/SQL details from misuse by other layers |

See `classes.md`, `inheritance.md`, `polymorphism.md` for full detail.
