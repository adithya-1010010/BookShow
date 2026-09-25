# Data Flow

```text
JavaFX Controller
    -> Service (business rule check)
    -> DAO (SQL statement, parameterized)
    -> SQLite (single local file, one JDBC Connection for app lifetime)
    -> ResultSet
    -> DAO maps rows to model objects
    -> Service returns model objects / booking result
    -> Controller renders on FXML view
```

All writes that touch more than one table (booking creation) use a single JDBC transaction (`connection.setAutoCommit(false)` ... `commit()`/`rollback()`), managed inside `BookingDao`/`BookingService` — never split across multiple implicit auto-commits.
