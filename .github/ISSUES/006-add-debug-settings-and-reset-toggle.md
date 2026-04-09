---
name: Add debug settings and reset toggle
about: Move reset behavior to a debug-only settings surface.
---

## 🎯 Objectives

- Keep reset functionality for development while removing it from the main gameplay UI.
- Introduce a debug settings surface with controlled access in non-production contexts.

## 🧱 Tasks

- [ ] Remove reset button from primary game screen.
- [ ] Add debug settings screen/entry point.
- [ ] Add reset-used-questions action inside debug settings.
- [ ] Gate debug settings behind build type/feature flag.
- [ ] Ensure reset action also clears related local derived states if needed.

## Possible Implementation

- Add `features/settings/presentation` with a debug-only section.
- Add `ResetQuestionUsageUseCase` in application layer.
- Expose debug entry condition through configuration provider.

## 🧪 Acceptance Criteria

- [ ] Main user flow no longer exposes reset action in production UI.
- [ ] Reset remains available in debug builds for QA/development.
- [ ] Reset action remains reliable and does not leave inconsistent state.

## 🗺️ Future (out of scope for this issue)

- Remote debug/admin controls.
- Runtime developer menu with additional diagnostics.
