# Non-Functional Requirements (Detail)

- **NFR-001 Single-user/local:** No network calls, no login screen, no multi-user session handling. The SQLite file lives locally (e.g. `./data/moviebooking.db`).
- **NFR-002 Usability:** Every screen has a clear "Back"/"Next" or equivalent navigation path; no dead ends; validation errors shown inline, not as raw exceptions.
- **NFR-003 Maintainability/OOP:** Layered architecture (UI / Service / DAO / Model) with one clear responsibility per class; no god-classes; inheritance and polymorphism used only where they solve a real design problem (see `07-oop/oop-design.md`).
- **NFR-004 Booking safety:** Seat-booking checks happen at the service layer against the live database state, not only in the UI layer, so no seat can end up double-booked regardless of UI timing.

No performance, scalability, security, or internationalization requirements are specified in `context.md` or the confirmed decisions — none are assumed here.
