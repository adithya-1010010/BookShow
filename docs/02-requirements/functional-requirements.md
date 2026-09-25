# Functional Requirements (Detail)

This file expands on each FR from `requirements.md` with acceptance detail. See that file for the ID table.

### FR-001 / FR-002 — Movie Listing
The Home/Movie screen must query all seeded movies from SQLite and display: title, genre, base price. No pagination needed (seed data is small).

### FR-003 / FR-004 — Theatre & Show Selection
For a selected movie, the app queries all shows (theatre + date/time) associated with that movie and lets the user pick exactly one.

### FR-005 / FR-006 / FR-007 — Seat Selection & Duplicate Prevention
- Seat grid is rendered per show (each show has its own seat availability state).
- Already-booked seats are visually disabled and cannot be selected.
- Enforcement is **not** UI-only: `BookingService` re-validates seat availability against the database immediately before writing the booking, to prevent a stale-UI race (e.g., grid not yet refreshed).

### FR-008 — Customer Details
Minimum fields: name (non-empty), phone (non-empty, basic format check only — no external validation service).

### FR-009 — Cost Calculation
`total = pricePerSeat × numberOfSeats`, where `pricePerSeat` comes from the applicable `PricingStrategy` for that show (see OOP design). No VIP tiers, no payment processing.

### FR-010 / FR-011 / FR-012 — Confirmation
On confirming: seats are atomically marked booked, a booking row is inserted, a unique Booking ID is generated (`BookingIdGenerator`), and the confirmation screen displays: Booking ID, movie, show (theatre + time), selected seats, customer name, total cost.

### FR-013 / FR-014 — Persistence & Seeding
On first launch, `DatabaseManager` creates the schema if absent, then `SeedData` inserts a fixed seed set only if the tables are empty (idempotent — re-running the app must not duplicate seed rows).
