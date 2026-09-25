# UI Design — Direction & Principles

## Design Goal
The interface should feel like a **premium, modern booking experience** — closer to a polished consumer app than a typical academic Swing/JavaFX form. Concretely, that means: a real visual identity (not default JavaFX gray), clear hierarchy, generous spacing, smooth screen transitions, and a seat map that feels tactile and satisfying to use — the seat grid and movie cards are the two places worth the most visual investment, since they're what a user actually looks at longest.

This is achieved entirely within confirmed constraints: **pure JavaFX + CSS**, no extra UI libraries, no web views, no images required (vector/CSS-drawn where needed). "Nicer than a commercial booking app" is a bar for *polish and clarity*, not a scope increase — no new screens or features are implied.

## Visual Language
| Element | Direction |
|---|---|
| Theme | Dark-first cinematic theme: deep charcoal/near-black background, one confident accent color (e.g. warm amber/red — "now showing" marquee feel) for primary actions and selected states |
| Typography | One clean sans-serif family, strong size contrast between screen titles and body text; avoid default system font look via `-fx-font-family` in a shared stylesheet |
| Cards | Movie list uses card-style layout (poster placeholder/color block + title + genre chip + price), not a plain `ListView` of text rows |
| Seat grid | Seats rendered as rounded rectangles/toggle buttons in a `GridPane`; three visual states — available (outline), selected (filled accent), booked (dimmed/disabled) — with a small legend always visible |
| Motion | Subtle only: hover/press feedback on buttons and seats, a fade/slide on screen transitions (JavaFX `FadeTransition`/`TranslateTransition`) — never gratuitous, never blocking interaction |
| Feedback | Inline validation messages (colored text under the field), not blocking modal alerts, except for irrecoverable errors (DB failure) |
| Consistency | One shared `styles.css`; one shared layout shell (header/back-button pattern) reused across all screens so navigation feels like one continuous app, not five disconnected windows |

## Non-Goals
- No skeuomorphism, no stock photography, no external image/font downloads (keeps the app fully offline/local, consistent with the single-user local constraint).
- No UI complexity beyond what's needed to browse, select, and confirm — polish is about *execution quality*, not feature count.

## Implementation Notes for OpenCode
- All styling lives in `src/main/resources/com/moviebooking/views/styles.css`, loaded once via `scene.getStylesheets().add(...)` in `MainApp`.
- Color/spacing values should be defined once (CSS custom-property-style via `-fx-*` root variables at the `.root` selector) and reused — never hardcoded per-screen.
- This is a Phase 6-8 concern (styling applied as each screen is built), plus a dedicated pass in Phase 10 to unify anything inconsistent. Do not introduce styling work into earlier phases (violates "no future-phase functionality").

See `screen-flow.md` for navigation and `screens.md` for per-screen specs.
