# System Flow

```mermaid
flowchart TD
    Start[Application Start] --> Home[Home Screen]
    Home --> MovieSel[Movie Selection]
    MovieSel --> ShowSel[Show Selection]
    ShowSel --> SeatSel[Seat Selection]
    SeatSel --> CustDetails[Customer Details]
    CustDetails --> Review[Booking Review + Cost Calculation]
    Review --> Confirm[Booking Confirmation]
    Confirm --> BookingId[Booking ID + Ticket Details Displayed]
```
