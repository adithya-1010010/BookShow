# Test Cases

| ID | Case | Expected Result | Phase |
|---|---|---|---|
| TC-001 | List all seeded movies | Returns non-empty list matching seed data | 4 |
| TC-002 | List shows for a valid movie | Returns correct shows for that movie only | 4 |
| TC-003 | Valid booking, 1 seat | Booking created, seat marked booked, correct total | 5 |
| TC-004 | Valid booking, multiple seats | Total = price x seatCount (per active PricingStrategy) | 5 |
| TC-005 | Attempt to book an already-booked seat | SeatUnavailableException thrown, no partial booking, seat states unchanged | 5 |
| TC-006 | Booking ID uniqueness across N bookings | All generated IDs are unique | 5 |
| TC-007 | No seats selected, attempt continue | Continue button disabled / validation error | 9 |
| TC-008 | Empty customer name/phone | Inline validation error, booking not attempted | 9 |
| TC-009 | Double-submit confirm (rapid double click) | Only one booking created, second attempt rejected/no-ops | 9 |
| TC-010 | Restart app after a booking | Previously booked seat still shows booked; seed data not duplicated | 8, 3 |
| TC-011 | Invalid/no movie selected on Show Selection | Cannot proceed without a selection | 9 |
| TC-012 | Weekend vs standard pricing (if implemented per memory.md decision) | Correct strategy applied based on show date | 5 |

Traceability to requirements is in `../02-requirements/requirements.md` and the master `13-reference` (see traceability matrix in the final plan).
