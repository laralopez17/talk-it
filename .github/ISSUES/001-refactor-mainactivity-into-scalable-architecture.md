---
name: Refactor MainActivity into scalable architecture
about: Split the monolithic screen into modular layers and feature boundaries.
---

## 🎯 Objectives

- Replace the single-file implementation in `MainActivity.kt` with a scalable structure aligned with hexagonal architecture.
- Separate presentation, domain, and data concerns to enable future multiplayer and backend sync features.

## 🧱 Tasks

- [x] Create `features/game/presentation` package for UI screens and state rendering.
- [x] Create `core/domain` package for game entities and business rules.
- [x] Create `data` package for local persistence and question sources.
- [x] Move question loading and used-question persistence out of composables.
- [x] Keep current gameplay behavior unchanged during refactor.
- [x] Keep reset behavior available for development.

## Possible Implementation

- Introduce a `GameScreen` composable in presentation.
- Introduce use cases in `core/application/usecase` for:
  - get current question
  - mark question as used
  - skip question
  - reset question usage
- Implement a local repository adapter backed by `SharedPreferences` for MVP.

## 🧪 Acceptance Criteria

- [x] `MainActivity.kt` no longer contains game business logic or persistence logic.
- [x] App behavior remains equivalent to current swipe + reset MVP.
- [x] Project follows a modular folder structure that supports adding new features without central file growth.

## ✅ Implementation Notes

- `MainActivity.kt` is now a thin entry point that wires `GameViewModel` and `GameScreen`.
- Question model, repository port, use cases, and local adapter were split into domain/application/data layers.
- Game UI moved to feature presentation package with dedicated state and screen files.

## 🗺️ Future (out of scope for this issue)

- Full realtime synchronization and lobby lifecycle.
- Backend-driven question selection.
