---
name: Add game mode and category selection
about: Add pre-game setup to select mode and question categories.
---

## 🎯 Objectives

- Let users choose game mode (`COUPLE`, `FRIENDS`, `FAMILY`) before starting a session.
- Introduce question category filtering compatible with current local question source.

## 🧱 Tasks

- [ ] Create mode selection UI screen.
- [ ] Create category selection UI section.
- [ ] Add mode/category fields to game session configuration model.
- [ ] Filter question retrieval by selected mode/category.
- [ ] Ensure defaults are safe when user skips optional selections.

## Possible Implementation

- Add `SessionConfig` model in domain.
- Add `GetQuestionsByConfigUseCase`.
- Keep categories local and static in first iteration.

## 🧪 Acceptance Criteria

- [ ] User can start a session with selected mode and category.
- [ ] Questions shown respect selected mode/category filters.
- [ ] Existing swipe flow remains stable after configuration is added.

## 🗺️ Future (out of scope for this issue)

- Dynamic categories managed from backend.
- Personalized category suggestions.
