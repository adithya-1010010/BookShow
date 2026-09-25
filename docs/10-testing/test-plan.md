# Test Plan (By Phase)

| Phase | Automated Tests | Manual Verification |
|---|---|---|
| 1 | none (scaffolding) | App window launches |
| 2 | Schema creation succeeds, idempotent | Inspect DB file created |
| 3 | Seed data idempotency, Customer.getRole() | Re-run app twice, confirm no duplicate rows |
| 4 | DAO CRUD/read tests (temp DB) | n/a |
| 5 | Pricing correctness, duplicate-seat rejection, booking ID uniqueness | n/a |
| 6 | n/a | Movie list + show selection render correct seeded data |
| 7 | n/a | Seat grid renders correctly, booked seats disabled, multi-select works |
| 8 | Ticket.fromBooking() mapping | Full booking flow completes; restart app, confirm seat still shows booked |
| 9 | Validation edge cases (empty fields, zero seats, double-submit) | Error messages appear inline, no crashes |
| 10 | Full regression suite | Full manual walkthrough, all screens polished per ui-design.md |
