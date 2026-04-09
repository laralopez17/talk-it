---
name: Implement GameViewModel and round state machine
about: Define explicit game states and transitions for deterministic gameplay.
---

## 🎯 Objectives

- Introduce `GameViewModel` as the single entry point for screen actions.
- Implement a deterministic round state machine as foundation for multiplayer sync.

## 🧱 Tasks

- [x] Define round states (idle, answering, ready-pending-undo, countdown, reveal, waiting-next).
- [x] Define user intents/events (answer changed, ready tapped, undo ready, next tapped).
- [x] Add transition rules with guards for invalid transitions.
- [x] Expose immutable UI state for Compose rendering.
- [x] Persist only required local state for app resume.

## Possible Implementation

- Create a `GameRoundState` sealed interface/class in domain.
- Create transition use cases in application layer.
- In `GameViewModel`, route user events to use cases and emit UI state.

## 🧪 Acceptance Criteria

- [x] Round flow is driven by explicit states, not ad hoc booleans.
- [x] Invalid transitions are blocked and do not corrupt state.
- [x] UI updates only by observing state from `GameViewModel`.
- [ ] State machine logic is unit-testable independently from Android UI.

## ✅ Implementation Notes

- Added `GameRoundState` and `GameIntent` as explicit state and event models.
- Added transition reducer use case and guarded state transitions through `GameViewModel`.
- Added timer-based flow for undo window and reveal countdown.
- Added dedicated reveal UI flow and waiting-next confirmation flow.
- Saved minimal resume state (`currentQuestion`, `answerDraft`, `roundState`) with `SavedStateHandle`.

## 🔜 Remaining for full closure

- Add unit tests for reducer and key `GameViewModel` transitions to fully satisfy testability acceptance criteria.

## 🗺️ Future (out of scope for this issue)

- Multi-device synchronization with server-authoritative transitions.
- Host/participant role handling in online lobbies.
