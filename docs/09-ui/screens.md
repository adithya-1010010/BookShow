# Screens — Per-Screen Spec

## Home
- **Purpose:** entry point, brand/title, "Browse Movies" call to action.
- **Components:** app title/logo text, primary button.
- **Navigation:** -> Movie List.
- **Controller:** `HomeController`. **Service:** none required.

## Movie List
- **Purpose:** browse all seeded movies.
- **Components:** card grid (title, genre chip, price, "View Shows" button per card).
- **Data:** `MovieService.listMovies()`.
- **Navigation:** card click -> Show Selection (for that movie). Back -> Home.
- **Controller:** `MovieListController`. **Service:** `MovieService`.

## Show Selection
- **Purpose:** pick theatre + time for the chosen movie.
- **Components:** list of shows (theatre name, date/time, screen number), select + "Continue" button.
- **Data:** `MovieService.listShowsForMovie(movieId)`.
- **Validation:** must select exactly one show to continue.
- **Navigation:** -> Seat Selection. Back -> Movie List.
- **Controller:** `ShowSelectionController`.

## Seat Selection
- **Purpose:** pick 1+ seats for the chosen show.
- **Components:** `GridPane` seat map with legend (Available / Selected / Booked), running "X seats selected" counter, "Continue" button (disabled until >=1 seat selected).
- **Data:** `BookingService.getSeatsForShow(showId)`.
- **Validation:** at least one seat required; already-booked seats non-interactive.
- **Navigation:** -> Customer Details. Back -> Show Selection.
- **Controller:** `SeatSelectionController`. **Service:** `BookingService`.

## Customer Details
- **Purpose:** collect name + phone, show running total.
- **Components:** name field, phone field, live total cost display, "Confirm Booking" button.
- **Validation:** non-empty name, non-empty/basic-format phone; inline error text.
- **Navigation:** -> Confirmation (on success, re-validates seats server-side). Back -> Seat Selection.
- **Controller:** `CustomerDetailsController`. **Service:** `BookingService`.

## Confirmation
- **Purpose:** show the completed booking.
- **Components:** Booking ID (prominent), movie title, show time/theatre, seat list, customer name, total cost, "Book Another Ticket" button.
- **Data:** `Ticket` built from the returned `Booking`.
- **Navigation:** -> Home.
- **Controller:** `ConfirmationController`.
