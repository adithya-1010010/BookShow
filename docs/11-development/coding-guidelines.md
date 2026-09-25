# Coding Guidelines

- Package root: `com.moviebooking`.
- One public class per file, file name matches class name.
- Private fields, public getters (no unnecessary setters on immutable-ish domain objects like Movie/Theatre).
- No business logic in controllers or DAOs — only in `service/`.
- No raw SQL string concatenation — use `PreparedStatement` parameters everywhere.
- Exceptions: custom unchecked exceptions (`DataAccessException`, `SeatUnavailableException`, `ValidationException`) — do not let `SQLException` escape the DAO layer.
- Javadoc on every public class and public method summarizing responsibility.
- No `System.out.println` for user-facing messages — use JavaFX UI elements; logging (if any) via a single simple logger, not multiple ad-hoc approaches.
