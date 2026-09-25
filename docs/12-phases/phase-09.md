# Phase 09 — Validation & Error Handling

**Goal:** Harden every flow against invalid input, edge cases, and DB errors.
**Depends on:** Phase 8.

## Files to Modify
Controllers and services across the app — defensive checks added, no new screens.

## Implementation Tasks
1. Ensure every screen with user input has inline, non-blocking validation messages (no raw exception dialogs).
2. Handle: no seats selected, empty/invalid customer fields, double-submission of Confirm (disable the button immediately on click, or guard against duplicate calls in `BookingService`).
3. Handle DB-layer failures gracefully (`DataAccessException` caught at controller level, shown as a clear but non-technical message; app must not crash).
4. Re-run and confirm all TC- test cases pass, including edge cases TC-007, TC-008, TC-009, TC-011.

## OOP Concepts Introduced
None new — hardening pass.

## Testing
JUnit + manual, per `10-testing/test-plan.md` Phase 9 row and TC-007/008/009/011 in `test-cases.md`.

## Expected Result
The app cannot be crashed or put into an inconsistent state through normal or careless use.

## Completion Checklist
```
[ ] Empty customer fields rejected with a clear inline message
[ ] Zero-seat continue is blocked
[ ] Double-submission does not create duplicate bookings
[ ] DB errors handled gracefully, no crash
[ ] All JUnit tests pass
```

## Documentation Updates
`memory.md`: Phase 9 -> COMPLETE.

## Git Checkpoint
`git add . && git commit -m "phase-09: add validation and error handling" && git push`
