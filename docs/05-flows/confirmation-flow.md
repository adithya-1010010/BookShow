# Confirmation Flow

```mermaid
flowchart TD
    A[BookingService returns Booking object] --> B[Ticket.fromBooking(booking)]
    B --> C[ConfirmationController displays Ticket]
    C --> D[Shows: Booking ID, Movie, Show time/theatre, Seats, Customer name, Total cost]
```

No printing/export step — on-screen display is the final state of the flow, per confirmed decisions.
