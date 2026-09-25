# Project Plan (Summary)

This file summarizes the plan; the authoritative, executable version for OpenCode is `implementation-plan.md` in this same folder.

## Sequencing Logic
Dependency-driven, not arbitrary:
1. You cannot write business logic before the domain model exists.
2. You cannot persist data before the schema exists.
3. You cannot build UI before there's a service layer to call.
4. You cannot validate/harden before the core flow works end-to-end.

## Phase List (see `roadmap.md` for the dependency diagram, `phases.md` for short descriptions, `implementation-plan.md` for full executable detail, `12-phases/phase-NN.md` for individual phase specs)

1. Project Foundation
2. Database Schema & Connection Management
3. Domain Model & Seed Data
4. DAO Layer
5. Business Logic (Pricing + Booking Service)
6. UI — Movie Browsing & Show Selection
7. UI — Seat Selection
8. UI — Customer Details & Confirmation
9. Validation & Error Handling
10. Final Testing, Polish & Documentation

## Governing Rule
**One phase at a time. Implement → test → document → update memory.md → commit → push → STOP.**
No phase may implement functionality belonging to a later phase.
