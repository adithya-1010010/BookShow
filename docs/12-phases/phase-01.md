# Phase 01 — Project Foundation

**Goal:** A runnable Maven + JavaFX project that launches a blank Home window.
**Depends on:** none.

## Files to Create
- `pom.xml` (Java 17, JavaFX plugin, sqlite-jdbc, JUnit 5 dependencies)
- `src/main/java/com/moviebooking/MainApp.java`
- `src/main/resources/com/moviebooking/views/Home.fxml`
- `src/main/java/com/moviebooking/controller/HomeController.java`
- `.gitignore` (target/, *.db, IDE files)
- `README.md` (initial skeleton — expanded fully in Phase 10)

## Database / UI Changes
None beyond a blank window.

## OOP Concepts Introduced
None yet (scaffolding only).

## Implementation Tasks
1. Set up `pom.xml` with confirmed dependencies only (Section: `04-architecture/technology-stack.md`).
2. `MainApp` loads `Home.fxml` into a `Stage`.
3. `Home.fxml` shows a title label and a disabled/placeholder "Browse Movies" button (wired in Phase 6).

## Testing
Manual only: `mvn clean javafx:run` opens a window without error.

## Expected Result
App launches, shows Home screen, closes cleanly.

## Completion Checklist
```
[ ] mvn clean install succeeds
[ ] JavaFX window opens and displays Home screen
[ ] No compiler warnings related to missing dependencies
```

## Documentation Updates
- `memory.md`: Phase 1 -> COMPLETE.

## Git Checkpoint
`git add . && git commit -m "phase-01: initialize Maven+JavaFX project skeleton" && git push`
