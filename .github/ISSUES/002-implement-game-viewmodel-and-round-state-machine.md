---
name: Implement GameViewModel and round state machine
about: Define explicit game states and transitions for deterministic gameplay.
---

## 🎯 Objectives

- Introduce `GameViewModel` as the single entry point for screen actions.
- Implement a deterministic round state machine as foundation for multiplayer sync.

## 🧱 Tasks

- [ ] Define round states (idle, answering, ready-pending-undo, countdown, reveal, waiting-next).
- [ ] Define user intents/events (answer changed, ready tapped, undo ready, next tapped).
- [ ] Add transition rules with guards for invalid transitions.
- [ ] Expose immutable UI state for Compose rendering.
- [ ] Persist only required local state for app resume.

## Possible Implementation

- Create a `GameRoundState` sealed interface/class in domain.
- Create transition use cases in application layer.
- In `GameViewModel`, route user events to use cases and emit UI state.

## 🧪 Acceptance Criteria

- [ ] Round flow is driven by explicit states, not ad hoc booleans.
- [ ] Invalid transitions are blocked and do not corrupt state.
- [ ] UI updates only by observing state from `GameViewModel`.
- [ ] State machine logic is unit-testable independently from Android UI.

## 🗺️ Future (out of scope for this issue)

- Multi-device synchronization with server-authoritative transitions.
- Host/participant role handling in online lobbies.
