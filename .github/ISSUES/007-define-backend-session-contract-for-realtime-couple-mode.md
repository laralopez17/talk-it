---
name: Define backend session contract for realtime couple mode
about: Specify server-authoritative session and round transition APIs/events.
---

## 🎯 Objectives

- Define a clear backend contract for Couple mode realtime gameplay.
- Ensure state transitions are server-authoritative and idempotent.

## 🧱 Tasks

- [ ] Define core entities and status enums for session and round lifecycle.
- [ ] Define participant actions and transition rules:
  - ready
  - undo ready (within allowed window)
  - next question
- [ ] Define event payloads for client updates (state snapshots and transition events).
- [ ] Define error codes for invalid state transitions and authorization failures.
- [ ] Define reconnect/resume handshake behavior.

## Possible Implementation

- Publish a contract document in `.github/` describing:
  - API endpoints or RPC actions
  - realtime channel event names
  - payload schemas
  - idempotency keys strategy

## 🧪 Acceptance Criteria

- [ ] Contract covers full Couple round flow from question assignment to next round.
- [ ] Contract defines server validation rules for every state transition.
- [ ] Contract includes reconnect behavior and consistency guarantees.
- [ ] Mobile client can implement against contract without undefined transition cases.

## 🗺️ Future (out of scope for this issue)

- Friends/Family high-scale session orchestration.
- Matchmaking and friend graph integration.
