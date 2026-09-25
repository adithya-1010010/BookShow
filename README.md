# 🎬 Movie Ticket Booking System

A Java 17 / JavaFX desktop application for browsing movies, selecting a show and seats, and confirming a booking — built as an academic project demonstrating core OOP concepts (class, object, inheritance, polymorphism) through a real, working system.

## Features
- Browse movies with genre and price
- View theatres and show timings
- Select a show and pick seats from a live seat grid
- Duplicate-seat booking is impossible (enforced at the database layer)
- Automatic ticket cost calculation
- Booking confirmation with a generated Booking ID
- All data persists locally across restarts (SQLite)
- Polished, premium-feeling UI (dark theme, styled seat grid, card-based movie list)

## Tech Stack
Java 17 · JavaFX · Maven · SQLite (plain JDBC) · JUnit 5

## Architecture
```
JavaFX UI -> Controllers -> Services -> DAOs (JDBC) -> SQLite
```
Full detail: [`docs/04-architecture/architecture.md`](docs/04-architecture/architecture.md)

## How to Run
```bash
mvn clean javafx:run
```

## How to Test
```bash
mvn test
```

## Documentation
| Start Here | |
|---|---|
| 📖 Project Overview | [`docs/01-overview/overview.md`](docs/01-overview/overview.md) |
| 🧠 Project Memory (source of truth) | [`docs/13-reference/memory.md`](docs/13-reference/memory.md) |
| 🗺️ Full Implementation Plan | [`docs/03-planning/implementation-plan.md`](docs/03-planning/implementation-plan.md) |
| 🏗️ Architecture | [`docs/04-architecture/architecture.md`](docs/04-architecture/architecture.md) |
| 🎨 UI Design | [`docs/09-ui/ui-design.md`](docs/09-ui/ui-design.md) |
| 🗄️ Database Design | [`docs/08-database/database-design.md`](docs/08-database/database-design.md) |
| ✅ Testing Strategy | [`docs/10-testing/testing-strategy.md`](docs/10-testing/testing-strategy.md) |
| 📦 Per-Phase Specs | [`docs/12-phases/`](docs/12-phases/) |

## Current Status
**Phase 0 (Planning & Documentation): COMPLETE**
**Phase 1 (Project Foundation): COMPLETE**
**Phase 2 (Database Schema): COMPLETE**
**Phase 3 (Domain Model + Seed Data): COMPLETE**

Live, authoritative status: [`docs/13-reference/memory.md`](docs/13-reference/memory.md)

## Project Status
| | |
|---|---|
| Repository | https://github.com/adithya-1010010/BookShow.git |
| Branch | `main` |
| Development model | One phase at a time — implement, test, document, commit, push, stop |
