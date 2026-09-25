# Phase 08 — UI: Customer Details & Booking Confirmation

**Goal:** Full booking flow completes end-to-end with a polished on-screen confirmation.
**Depends on:** Phase 7.

## Files to Create/Modify
- `views/CustomerDetails.fxml`, `controller/CustomerDetailsController.java`
- `views/Confirmation.fxml`, `controller/ConfirmationController.java`
- `model/Ticket.java`

## Implementation Tasks
1. `CustomerDetailsController`: collect name/phone, display running total (via active `PricingStrategy`), validate non-empty fields inline.
2. On Confirm: call `BookingService.createBooking(show, seats, customer)`.
   - On `SeatUnavailableException`: show inline error, return user to Seat Selection with a refreshed grid (do NOT proceed).
   - On success: build `Ticket.fromBooking(booking)` and navigate to Confirmation.
3. `ConfirmationController` displays Booking ID, movie, show info, seats, customer name, total cost, styled per `09-ui/ui-design.md`.
4. "Book Another Ticket" button returns to Home.

## OOP Concepts Introduced
`Ticket` as a display-ready object built from `Booking` (Class/Object usage).

## Testing
- JUnit: `Ticket.fromBooking()` correctly maps all fields.
- Manual full-flow: Home -> Movie -> Show -> Seats -> Details -> Confirmation, end to end.
- Manual persistence check (TC-010): restart the app, verify the booked seat still shows booked in Seat Selection, and seed data was not duplicated.

## Expected Result
A user can complete an entire booking and see a correct, well-styled confirmation; the booking persists across restarts.

## Completion Checklist
```
[ ] Customer detail validation works (non-empty fields)
[ ] Booking persists correctly to DB
[ ] Confirmation screen shows correct Booking ID, movie, show, seats, customer, total
[ ] Restarting the app shows the seat as still booked
[ ] JUnit tests pass
```

## Documentation Updates
`memory.md`: Phase 8 -> COMPLETE.

## Git Checkpoint
`git add . && git commit -m "phase-08: implement customer details and booking confirmation UI" && git push`
