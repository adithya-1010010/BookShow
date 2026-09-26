# Phase 07 — UI: Seat Selection

**Goal:** Styled, interactive seat grid for a chosen show.
**Depends on:** Phase 6.

## Files to Create/Modify
- `views/SeatSelection.fxml`, `controller/SeatSelectionController.java`
- Extend `styles.css` with seat-state classes (available/selected/booked)

## Implementation Tasks
1. Load seats via `BookingService`/`SeatDao` (through the service layer only) for the chosen show.
2. Render a `GridPane` of seat toggle controls per `09-ui/screens.md`, with a visible legend.
3. Already-booked seats rendered disabled/dimmed and non-interactive.
4. Track selected seats in controller state; show a running "X seats selected" count; enable Continue only when count >= 1.
5. Continue navigates to Customer Details (built in Phase 8) carrying the selected seats + show.

## OOP Concepts Introduced
None new — UI wiring only.

## Testing
Manual: seat grid renders correct layout for the show; booked seats correctly disabled; multiple seat selection/deselection works; Continue disabled at zero seats.

## Expected Result
User can visually select seats for a show with correct availability states.

## Completion Checklist
```
[?] Seat grid renders correctly for a given show          -- UNVERIFIED (needs a human eye)
[?] Booked seats are visibly disabled and non-selectable  -- UNVERIFIED (needs a human eye)
[?] Multi-seat selection/deselection works correctly      -- UNVERIFIED (needs a human eye)
[?] Continue button disabled until >=1 seat selected      -- UNVERIFIED (needs a human eye)
```

All four items are implemented in `SeatSelectionController` but were **never visually confirmed**:
the session that wrote this phase could not launch the JavaFX GUI headlessly and the user accepted
committing on that basis. Headlessly-verifiable checks did pass: the project compiles, `mvn test` is
49/49 green, and every `styleClass` used by `SeatSelection.fxml` exists in `styles.css`.
See "Outstanding Verification" in `docs/13-reference/memory.md`.

## Documentation Updates
`memory.md`: Phase 7 -> COMPLETE (with the visual-verification caveat above).

## Git Checkpoint
`git add . && git commit -m "phase-07: implement seat selection UI" && git push`
