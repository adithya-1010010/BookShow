# Phase 06 — UI: Movie Browsing & Show Selection

**Goal:** User can browse real seeded movies and pick a show, styled per `09-ui/ui-design.md`.
**Depends on:** Phase 5.

## Files to Create/Modify
- `views/MovieList.fxml`, `controller/MovieListController.java`
- `views/ShowSelection.fxml`, `controller/ShowSelectionController.java`
- `views/styles.css` (introduced here — shared stylesheet, root color/spacing variables)
- Modify `Home.fxml`/`HomeController` to wire the "Browse Movies" button

## UI Changes
Implement Movie List as a card grid and Show Selection as a styled list, per `09-ui/screens.md`. Apply the dark/accent theme in `styles.css` and load it once in `MainApp`.

## Implementation Tasks
1. `MovieListController` calls `MovieService.listMovies()` and renders cards.
2. Selecting a movie navigates to `ShowSelection`, calling `MovieService.listShowsForMovie(movieId)`.
3. Selecting a show and clicking Continue navigates to Seat Selection (screen itself built in Phase 7 — for now, a placeholder/no-op or stub navigation target is acceptable, but must not implement seat selection logic here).
4. Add Back navigation on both screens per `09-ui/screen-flow.md`.

## OOP Concepts Introduced
None new — UI wiring only.

## Testing
Manual: movie list displays seeded movies with correct genre/price; selecting a movie shows only its shows; Back navigation works both directions.

## Expected Result
A user can visually browse movies and pick a show; the app looks like a real, styled application (not default JavaFX gray).

## Completion Checklist
```
[ ] Movie list displays seeded movies as styled cards
[ ] Selecting a movie shows its correct shows
[ ] Back navigation works on both screens
[ ] styles.css applied consistently (no default-gray unstyled controls)
```

## Documentation Updates
`memory.md`: Phase 6 -> COMPLETE.

## Git Checkpoint
`git add . && git commit -m "phase-06: implement movie browsing and show selection UI" && git push`
