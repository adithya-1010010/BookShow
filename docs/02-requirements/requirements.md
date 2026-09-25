# Master Requirements Document

All requirements are sourced from `context.md` and the confirmed technical decisions. No requirement below was invented.

## Functional Requirements

| ID | Description | Source | Priority | Module | Phase |
|---|---|---|---|---|---|
| FR-001 | User can view a list of available movies | context.md | High | Movie | 6 |
| FR-002 | User can view genre and ticket price per movie | context.md | High | Movie | 6 |
| FR-003 | User can view theatres and show timings | context.md | High | Movie/Booking | 6 |
| FR-004 | User can select a movie and a show | context.md | High | Booking | 6 |
| FR-005 | User can view a seat grid for the selected show | context.md (derived: "simple row/column grid") | High | Booking | 7 |
| FR-006 | User can select one or more available seats | context.md | High | Booking | 7 |
| FR-007 | System prevents selection/booking of an already-booked seat | context.md | Critical | Booking | 5, 7 |
| FR-008 | User can enter customer details (name, phone) | context.md | High | Booking | 8 |
| FR-009 | System calculates total ticket cost based on number of seats | context.md | High | Ticket | 5 |
| FR-010 | User can confirm a booking | context.md | High | Confirmation | 8 |
| FR-011 | System generates a unique Booking ID per booking | context.md | High | Confirmation | 5 |
| FR-012 | System displays complete ticket details on confirmation | context.md | High | Confirmation | 8 |
| FR-013 | All movie/theatre/show/seat/booking data persists across application restarts | Confirmed decision | High | Data | 2–4 |
| FR-014 | Initial movie/theatre/show/seat data is seeded automatically on first run | Confirmed decision | Medium | Data | 3 |

## Non-Functional Requirements

| ID | Description | Source | Priority |
|---|---|---|---|
| NFR-001 | Application must run as a single-user local desktop app (no login, no network) | Confirmed decision | High |
| NFR-002 | UI must be simple, usable, and consistent (no unnecessary complexity) | context.md ("user-friendly") | Medium |
| NFR-003 | Codebase must be maintainable and demonstrate clean OOP design | context.md + confirmed decision | High |
| NFR-004 | Application must reliably prevent duplicate seat bookings even under repeated attempts | context.md | Critical |

## OOP Requirements

| ID | Concept | Justification Required In Design |
|---|---|---|
| OOP-001 | Class / Object | Every domain entity |
| OOP-002 | Inheritance | Must have a genuine, non-contrived use case |
| OOP-003 | Polymorphism | Must have a genuine, non-contrived use case |

See `07-oop/oop-design.md` for how OOP-001–003 are satisfied.
