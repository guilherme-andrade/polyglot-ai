# ADR 0008: Cross-Context Contracts

**Status**: draft
**Date**: 2026-05-26

## Context

DDD rule: bounded contexts communicate only through application services or domain
events. These contracts must be explicit so humans and AI agents can work in
different contexts simultaneously without breaking each other.

## Decision

### Context Map

```
user ──────── curriculum ──────── lesson ──────── gamification
 │                 │                   │                │
 └─────────────────┴───────────────────┴────────────────┘
                              │
                          content
                              │
                          analytics
```

### Communication patterns

| From | To | Pattern | Rationale |
|------|----|---------|-----------|
| `user` | `curriculum` | Domain event | User registered → create SkillProfile |
| `user` | `lesson` | Domain event | Preferences changed → adjust lesson difficulty |
| `curriculum` | `lesson` | Application service | Generate next lesson from curriculum unit |
| `lesson` | `gamification` | Domain event | Lesson completed → award XP |
| `lesson` | `curriculum` | Domain event | Exercise completed → update SkillProfile |
| `lesson` | `analytics` | Domain event | Every exercise attempt → usage event |
| `content` | `lesson` | Application service | Get content items matching curriculum unit |
| `gamification` | `user` | Domain event | Achievement unlocked → notify profile |

### Domain event schema

All events follow this envelope:

```java
public record DomainEvent(
    UUID eventId,
    String eventType,       // e.g. "lesson.completed", "user.registered"
    UUID aggregateId,
    UUID contextId,         // user ID, lesson ID, etc.
    Instant occurredAt,
    Map<String, Object> payload
) {}
```

### Event naming convention

`{context}.{aggregate}.{action}` — e.g. `lesson.exercise.answered`, `user.account.created`.

### DTO contracts at application service boundaries

When context A calls context B's application service:

1. The caller passes a DTO defined in context B's `interfaces` package
2. Context B returns a DTO defined in its own `interfaces` package
3. Context A maps the returned DTO to its own domain objects if needed

### Event ownership

Each context owns the schema of the events it publishes. Consumers couple to the
event schema, not the publisher's domain model.

## Consequences

- Every cross-context interaction has an explicit contract
- ArchUnit rules enforce that no context imports another context's `domain` or
  `infrastructure` packages directly
- New cross-context interactions require adding the event or DTO to this ADR
- Event payloads are versioned (schema evolution TBD when needed)
