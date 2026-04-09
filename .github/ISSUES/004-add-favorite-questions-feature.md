---
name: Add favorite questions feature
about: Allow users to save and review favorite questions.
---

## 🎯 Objectives

- Enable users to mark/unmark questions as favorites.
- Provide a favorites list screen for quick access.

## 🧱 Tasks

- [ ] Add favorite toggle action in question UI.
- [ ] Add favorites repository interface and local implementation.
- [ ] Persist favorite question IDs locally for MVP.
- [ ] Add favorites screen with list and empty state.
- [ ] Support removing favorites from list.

## Possible Implementation

- Add `FavoriteQuestion` domain model.
- Add use cases:
  - toggle favorite
  - get favorites
  - remove favorite
- Use local persistence adapter initially.

## 🧪 Acceptance Criteria

- [ ] Question can be favorited and unfavorited from gameplay flow.
- [ ] Favorites persist across app restarts.
- [ ] Favorites screen reflects updates correctly.

## 🗺️ Future (out of scope for this issue)

- Cross-device favorite sync.
- Favorite folders or tags.
