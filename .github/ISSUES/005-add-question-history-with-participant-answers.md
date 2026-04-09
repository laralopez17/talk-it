---
name: Add question history with participant answers
about: Store and display historical rounds with answers.
---

## 🎯 Objectives

- Persist completed rounds locally, including question and participant answers.
- Provide a history screen to review previous rounds.

## 🧱 Tasks

- [ ] Define history entities (`Round`, `RoundAnswer`) in domain.
- [ ] Add history repository interface and local persistence implementation.
- [ ] Store round result on reveal completion.
- [ ] Build history screen with list of rounds and answer details.
- [ ] Add loading and empty states.

## Possible Implementation

- Start with local DB or simple persisted storage for MVP.
- Use a mapper layer between persistence models and domain models.

## 🧪 Acceptance Criteria

- [ ] Completed rounds are saved with question and participant answers.
- [ ] History survives app restarts.
- [ ] User can navigate and inspect past rounds in a readable format.

## 🗺️ Future (out of scope for this issue)

- Cloud history sync across devices.
- Search and filters by mode, participant, or category.
