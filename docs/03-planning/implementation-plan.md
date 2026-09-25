# Implementation Plan — OpenCode Execution Blueprint

**This is the primary document OpenCode must follow. It is executable, not descriptive.**

---

## 0. Mandatory Pre-Flight (every session, every phase)

Before touching any code, OpenCode MUST:
1. Read `context.md`
2. Read `docs/13-reference/memory.md` (current state — source of truth)
3. Read `docs/04-architecture/architecture.md`
4. Read the relevant `docs/06-modules/*.md` for the phase
5. Read `docs/12-phases/phase-NN.md` for the phase about to be implemented
6. Read the previous phase's spec and confirm it is marked COMPLETE in `memory.md`

If `memory.md` says the current phase is anything other than "NOT STARTED", **stop and report the discrepancy instead of proceeding.**

---

## 1. Anti-Hallucination Rules (non-negotiable)

1. **Never invent a requirement.** Only implement what `requirements.md` lists.
2. **Never silently change an architecture decision.** If `architecture.md` says JDBC/DAO pattern, do not introduce JPA/Hibernate.
3. **Always consult** `context.md`, `memory.md`, `requirements.md`, `architecture.md` before any non-trivial decision.
4. **If genuinely ambiguous and undocumented, STOP and ask** — do not guess and proceed.
5. **Do not add dependencies** not already listed in `04-architecture/technology-stack.md`.
6. **Do not implement future-phase functionality**, even if it seems convenient (e.g., don't wire up seat selection UI while doing Phase 5 business logic).
7. **Do not rewrite working components** unless the current phase explicitly requires modifying them.
8. **Document every deviation** in `memory.md` under "Changes to previous decisions" with reason and date/phase.

---

## 2. Per-Phase Execution Lifecycle (mandatory, identical for every phase)

```text
1. Read memory.md + relevant docs (Section 0)
2. Implement ONLY the current phase's listed files/classes
3. Run the phase's required tests (unit/integration as specified)
4. Manually verify the phase's "Expected Result"
5. Review the diff (git status / git diff) — confirm no out-of-scope files changed
6. Update relevant docs (module docs, architecture.md if something genuinely changed)
7. Update memory.md:
     - Mark phase COMPLETE
     - Record any discoveries, deviations, or decisions made during the phase
8. git add .
9. git commit -m "phase-NN: <accurate description>"
10. git push
11. STOP. Do not begin the next phase automatically.
```

---

## 3. Phase Summaries (full detail in `docs/12-phases/phase-NN.md`)

| Phase | Name | Depends On |
|---|---|---|
| 1 | Project Foundation | — |
| 2 | Database Schema & Connection | 1 |
| 3 | Domain Model & Seed Data | 2 |
| 4 | DAO Layer | 3 |
| 5 | Business Logic (Pricing + Booking) | 4 |
| 6 | UI: Movies & Shows | 5 |
| 7 | UI: Seat Selection | 6 |
| 8 | UI: Customer Details & Confirmation | 7 |
| 9 | Validation & Error Handling | 8 |
| 10 | Final Testing, Polish, Documentation | 9 |

---

## 4. Commit Message Convention

```text
phase-01: initialize Maven+JavaFX project skeleton
phase-02: add SQLite schema and DatabaseManager
phase-03: add domain models and seed data
phase-04: implement DAO layer for all entities
phase-05: implement pricing and booking business logic
phase-06: implement movie browsing and show selection UI
phase-07: implement seat selection UI
phase-08: implement customer details and booking confirmation UI
phase-09: add validation and error handling
phase-10: final polish, documentation, and test pass
```
Messages must describe what was **actually done**, not the generic template text, if the actual work differs.

---

## 5. Ambiguity Resolution Protocol

If OpenCode hits a decision not covered by `context.md`, `memory.md`, `requirements.md`, or `architecture.md`:

1. Do NOT guess or silently pick a default.
2. Record the exact ambiguity in `memory.md` under a new "Open Question" entry.
3. Stop implementation of the affected part of the phase.
4. Surface the question to the user/developer for a decision.
5. Resume only after the decision is recorded in `memory.md` as a Decision.

---

## 6. Definition of "Phase Complete"

A phase is complete only when **all** are true:
- All files listed in the phase spec exist and match their documented responsibility.
- All phase-specific tests pass.
- Expected Result (from the phase spec) is manually verified.
- `memory.md` is updated.
- Changes are committed with the correct message and pushed to `https://github.com/adithya-1010010/BookShow.git`.

If any of these is not true, the phase is **NOT COMPLETE**, regardless of how much code exists.
