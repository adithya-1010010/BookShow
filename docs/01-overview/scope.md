# Scope

## In Scope
- Browsing movies, genres, prices
- Theatre/show timing display
- Seat selection (simple row/column grid, no VIP tiers)
- Customer detail entry (name, phone)
- Ticket cost calculation
- Duplicate seat booking prevention
- Booking confirmation with generated Booking ID
- On-screen ticket details display
- Local SQLite persistence across restarts
- Seeded initial data (movies/theatres/shows/seats)

## Out of Scope (explicitly, per confirmed decisions)
- User authentication / login
- Admin management UI (editing movies/theatres/shows)
- Payment processing (real or simulated)
- VIP/seat-category pricing tiers
- Ticket printing/export (PDF, file export)
- Multi-user or networked/concurrent access
- Any GUI framework other than JavaFX
- Any database other than SQLite

## Boundary Notes
- "Single-user local desktop application" means the app assumes one person operating one instance at a time. Duplicate-seat prevention still must be enforced at the database/service layer (not just in the UI), because it is a core functional requirement, but true concurrent-access race conditions across multiple app instances are out of scope.
