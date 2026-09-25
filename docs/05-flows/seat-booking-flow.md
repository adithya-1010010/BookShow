# Seat Selection & Duplicate-Seat Validation Flow

```mermaid
flowchart TD
    A[User opens Seat Selection for a Show] --> B[Load seat grid from SeatDao]
    B --> C{Seat already booked?}
    C -->|Yes| D[Render disabled / non-selectable]
    C -->|No| E[Render selectable]
    E --> F[User selects 1+ seats]
    F --> G[User proceeds to Customer Details]
    G --> H[On Confirm: BookingService re-validates seats against DB]
    H --> I{Still available?}
    I -->|No| J[Reject — show error, refresh grid]
    I -->|Yes| K[Proceed with booking]
```

This two-stage check (UI-level disabling + service-level re-validation at commit time) is what actually satisfies FR-007/NFR-004 — the UI disabling alone is a convenience, not the enforcement mechanism.
