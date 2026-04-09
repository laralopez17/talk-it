# TALKIT App Growth Plan (Android)

## 1) Current state

The app currently works as a single-screen MVP implemented in `MainActivity.kt`.

What is already working:

- A fixed in-app question bank.
- Swipe right to mark question as used.
- Swipe left to skip without marking as used.
- Persistence of used questions via `SharedPreferences`.
- Reset button to clear used questions.

Main technical risk right now:

- Presentation, game rules, storage, and state transitions are coupled in one file, which makes testing, scaling, and multiplayer synchronization difficult.

## 2) Product direction (short and medium term)

Primary objective:

- Build a stable, scalable social game app with synchronized question rounds for different play modes.

Modes to support:

- Couple mode.
- Friends mode.
- Family mode.

Core experience goals:

- Avoid repeated questions.
- Keep sessions fast and synchronized across devices.
- Preserve session history (question + participant answers).
- Allow favorite questions.

Out of scope for now:

- AI-generated questions as a core dependency.
- Complex recommendation systems.

## 3) Scalable architecture proposal (Hexagonal-oriented)

Recommended approach:

- Keep Android-specific UI and framework code at the edges.
- Keep game logic and business rules in pure Kotlin (domain core).
- Isolate data providers (local DB, remote backend) behind ports/interfaces.

Suggested top-level structure:

```text
app/src/main/java/com/example/myapplication/
  core/
    domain/
      model/
      rules/
      ports/
    application/
      usecase/
  data/
    local/
      datastore/
      db/
      mapper/
    remote/
      api/
      dto/
      mapper/
    repository/
  features/
    game/
      presentation/
        screen/
        components/
        state/
        vm/
      domain/
      data/
    history/
      presentation/
      domain/
      data/
    favorites/
      presentation/
      domain/
      data/
    settings/
      presentation/
  platform/
    di/
    analytics/
    notifications/
```

Why this structure:

- Domain remains independent from UI and storage.
- New features can be added by feature module/folder without touching unrelated flows.
- Unit tests can focus on use cases and game rules.
- Device sync concerns can evolve from local-first to online-first without rewriting UI.

## 4) Main domains and responsibilities

Game Session Domain:

- Session lifecycle (created, waiting answers, locked, reveal, waiting next).
- Participant readiness.
- Timers (undo window, reveal countdown, response limit by mode).
- Next-question gating (advance only when all required participants confirm).

Question Domain:

- Catalog by mode/category.
- Selection strategy (no repetition in current session; configurable reuse rules later).
- Mark-as-used and favorite tracking.

History Domain:

- Persist question, participant, answer, timestamps, and round outcome.
- Query by session and by participant.

Synchronization Domain:

- Session state consistency across devices.
- Conflict-safe transitions (server-authoritative session state).

## 5) Multiplayer flow proposal (Couple mode)

Round sequence:

1. Server selects one question and sends it to both participants.
2. Each participant writes an answer privately.
3. Participant taps "Ready".
4. A 3-second undo window starts (participant can cancel readiness).
5. When both remain ready after undo window:
   - start a reveal countdown (3 or 5 seconds).
6. Reveal both answers and question on both screens.
7. Show "Next question" button.
8. Advance only when both participants tap "Next question".

Important rules:

- If one user disconnects, session should recover without corrupting round state.
- Each transition must be idempotent and validated server-side.

## 6) Friends/Family mode time constraints

Additional behavior for groups:

- Enforce answer time limit per round (configurable by mode).
- If time expires:
  - unanswered participants are marked as "no answer" for that round.
  - round proceeds to reveal with available answers.

Suggested defaults:

- Couple mode: no strict answer timeout.
- Friends mode: 45-60s.
- Family mode: 60-90s (can be longer depending on audience).

## 7) Lobby creation and join strategy

Proposed host flow:

1. One user creates a session (host).
2. Host configures:
   - game mode (`COUPLE`, `FRIENDS`, `FAMILY`)
   - max response time (optional for couple mode, required for group modes)
   - optional category filter
3. App creates a lobby and waits for participants.
4. Session starts only when minimum participant count is met.

Join options roadmap:

- V1 (recommended first): join with a short 6-character code.
- V2 (later): join through friend relationships and direct invitations.

Why code-first:

- Lowest implementation complexity.
- Faster validation of core multiplayer behavior.
- No need for a complete social graph in early releases.

Recommended code constraints:

- Alphanumeric uppercase.
- Expiration window (for example 10-20 minutes if session not started).
- Collision-safe generation and server-side uniqueness checks.

## 8) Data model baseline (future backend-ready)

Essential entities:

- User
- Session
- SessionParticipant
- Question
- Round
- RoundAnswer
- FavoriteQuestion

Minimum metadata to include early:

- `createdAt`, `updatedAt`
- `status` enums
- `mode` enum (`COUPLE`, `FRIENDS`, `FAMILY`)
- `category`

## 9) Incremental delivery plan

### Phase 0 — Foundation refactor (now)

Goal:

- Split `MainActivity.kt` into clear layers and move game logic out of UI.

Tasks:

- Introduce `GameViewModel` and UI state model.
- Move question source into repository interface + local implementation.
- Move used/favorite persistence into data layer.
- Keep reset button, but guard it behind a temporary debug flag.

Done when:

- UI is stateless-ish (render from view state).
- Business actions are called as use cases, not directly inside composables.

### Phase 1 — Single-device robust gameplay

Goal:

- Prepare for multiplayer by stabilizing local game engine.

Tasks:

- Implement deterministic round state machine.
- Add history storage and history screen.
- Add favorites flow and favorites screen.
- Add categories and mode selection screen.
- Define lobby configuration model locally (host settings and validation rules).

Done when:

- App supports complete local rounds with clean state transitions.

### Phase 2 — Realtime synchronization (online sessions)

Goal:

- Synchronize rounds across multiple devices.

Tasks:

- Add backend session service (rooms, participants, round state).
- Add lobby creation flow and 6-character join code flow.
- Make server authoritative for round transitions.
- Implement reconnect/resume behavior.
- Add basic anti-duplication and consistency checks.

Done when:

- Two devices in Couple mode see same question, synchronized readiness, reveal, and next transitions.
- Host can create a lobby, share code, and participants can join reliably.

### Phase 3 — Group optimization and moderation

Goal:

- Make Friends/Family modes reliable at larger participant counts.

Tasks:

- Add mode-based response timers.
- Improve lobby and participant status indicators.
- Add abuse/failure safeguards (timeouts, stale participants, round skips).
- Start friend-based joining and invitation model (without replacing code join).

Done when:

- Sessions remain smooth with larger groups and intermittent connectivity.

### Phase 4 — Content quality scale

Goal:

- Expand and manage question bank quality.

Tasks:

- Curated question catalog operations (versioned datasets).
- Better category management and localization support.
- Optional experimentation with AI-assisted generation pipeline (offline validation first).

Done when:

- Content can grow safely without degrading relevance or causing repeated prompts.

## 10) Non-functional requirements for Play Store readiness

Performance:

- Keep UI updates lightweight and state-driven.
- Avoid storing large payloads in memory; paginate history when needed.

Reliability:

- Ensure offline-safe local persistence and crash recovery.
- Use robust retry/reconnect strategy for sync events.

Security and privacy:

- Minimize personal data.
- Protect session/answer access by membership rules.

Quality:

- Unit tests for domain use cases and state machine.
- Integration tests for repository and synchronization adapters.
- UI tests for key gameplay flow.

## 11) Immediate next issues to create

1. Refactor monolithic `MainActivity.kt` into feature + domain + data layers.
2. Implement `GameViewModel` with explicit round state machine.
3. Add question categories and game mode selector.
4. Add favorites persistence and favorites screen.
5. Add history persistence and history screen.
6. Add temporary debug settings screen (including reset toggle).
7. Define backend session contract for realtime Couple mode.
8. Define lobby contract (host settings, participant limits, join code lifecycle).

## 12) Notes about reset button

Current decision:

- Keep reset functionality for development/testing speed.

Transition plan:

- Move reset to a debug-only surface.
- Remove from production UI before public release.
