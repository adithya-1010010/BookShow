# Screen Flow & Navigation

```mermaid
flowchart LR
    Home --> MovieList --> ShowSelection --> SeatSelection --> CustomerDetails --> Confirmation
    Confirmation -->|Book Another| Home
    MovieList -->|Back| Home
    ShowSelection -->|Back| MovieList
    SeatSelection -->|Back| ShowSelection
    CustomerDetails -->|Back| SeatSelection
```

Every non-Home screen has a Back action; every screen after Confirmation offers a clear "Book Another Ticket" action returning to Home. No dead ends, per NFR-002.
