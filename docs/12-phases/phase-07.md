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
[ ] Seat grid renders correctly for a given show
[ ] Booked seats are visibly disabled and non-selectable
[ ] Multi-seat selection/deselection works correctly
[ ] Continue button disabled until >=1 seat selected
```

## Documentation Updates
`memory.md`: Phase 7 -> COMPLETE.

## Git Checkpoint
`git add . && git commit -m "phase-07: implement seat selection UI" && git push`
