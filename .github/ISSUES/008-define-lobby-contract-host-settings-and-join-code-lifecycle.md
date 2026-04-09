---
name: Define lobby contract host settings and join code lifecycle
about: Specify lobby creation, participant join rules, and code-based entry behavior.
---

## 🎯 Objectives

- Define the contract for host-created lobbies and participant joining by short code.
- Ensure reliable code lifecycle (generation, uniqueness, expiration, invalidation).

## 🧱 Tasks

- [ ] Define lobby creation input:
  - mode
  - max response time
  - optional category
  - participant limits
- [ ] Define lobby status model (waiting, ready-to-start, in-progress, closed).
- [ ] Define join-by-code flow and validation rules.
- [ ] Define code format and constraints (6-char alphanumeric uppercase).
- [ ] Define expiration and invalidation behavior for unused/finished lobbies.
- [ ] Define host permissions (start session, close lobby, remove participant if needed).

## Possible Implementation

- Create contract document with:
  - API/RPC actions for create/join/start/close
  - realtime event payloads for participant presence and lobby updates
  - server-side uniqueness and anti-collision strategy for join codes

## 🧪 Acceptance Criteria

- [ ] Contract fully specifies create and join flows for code-based lobbies.
- [ ] Lifecycle of a join code is explicit and testable.
- [ ] Host and participant capabilities are clearly separated.
- [ ] Contract is ready for client implementation in Android app.

## 🗺️ Future (out of scope for this issue)

- Friend-based invites and social graph joins.
- Deep-link invite links and cross-platform invite flows.
