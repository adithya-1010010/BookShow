# Testing Strategy

## Levels
| Level | Tool | Scope |
|---|---|---|
| Unit | JUnit 5 | Pricing strategies, BookingIdGenerator, model validation |
| Integration | JUnit 5 + temp SQLite file/in-memory | DAO read/write correctness, transaction rollback on duplicate seat |
| Manual/E2E | Human tester | Full JavaFX flow: Home -> ... -> Confirmation |
| Regression | JUnit 5 (re-run full suite) | Run before every phase's Git checkpoint from Phase 5 onward |

## What Is NOT Tested (and why)
- JavaFX rendering pixel-output is not unit-tested (disproportionate effort for an academic project) — verified manually per phase per `screens.md`.
- No load/performance testing — single-user local app, not a requirement.

See `test-plan.md` for the phase-by-phase test plan and `test-cases.md` for concrete cases (TC-IDs).
