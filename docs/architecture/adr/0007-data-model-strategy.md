# ADR 0007: Data Model Strategy

**Status**: draft
**Date**: 2026-05-26

## Context

We need to define the core data model — entities, aggregates, database schema
strategy, and cross-context data access rules — before implementing repositories
and APIs across the six bounded contexts.

## Decision

### PostgreSQL is the primary relational store

All bounded contexts store their domain entities in PostgreSQL, with Flyway
for schema migrations. Each context owns its own migration scripts.

### MongoDB for user-content interaction logs

User-content interaction logs (views, clicks, time-spent, exercise attempts) are
semi-structured and high-volume. MongoDB's document model fits this better than
relational tables. These documents are owned by the `lesson` and `analytics` contexts.

### pgvector for embeddings

Vocabulary embeddings, exercise embeddings, and content embeddings are stored as
`vector` columns in PostgreSQL via the pgvector extension. The `content` context
owns the embedding schema.

### Aggregate design per bounded context

| Context | Primary aggregate | Key entities | Key value objects |
|---------|-------------------|--------------|-------------------|
| `user` | User | Account, Profile, Preferences | Email, PasswordHash, LanguagePair |
| `curriculum` | Curriculum | SkillProfile, Unit, LearningPath | CefrLevel, SkillGap, VocabularyRange |
| `content` | ContentItem | Source, Topic, Language | ContentType, Difficulty, Embedding |
| `lesson` | Lesson | Exercise, ExerciseAttempt, Session | ExerciseType, Answer, Score |
| `gamification` | PlayerProfile | Streak, Achievement, XpTransaction | XpAmount, StreakCount, Milestone |
| `analytics` | UsageEvent | SessionLog, InteractionLog | EventType, Duration |

### Flyway migration conventions

- One migration per logical change: `V{NN}__{context}_{description}.sql`
- Migrations live in `server/src/main/resources/db/migration/`
- No cross-context migrations — each migration targets one context's tables

### Cross-context data access

- No direct repository imports across context boundaries
- Read-only access via application service interfaces or domain events
- Contexts that need another context's data subscribe to its domain events
  and maintain a local projection (CQRS-lite)

## Consequences

- Each bounded context owns its tables and migrations
- Flyway ensures deterministic schema state across environments
- MongoDB documents are schema-flexible but versioned in application code
- pgvector extension is a hard dependency for PostgreSQL
- Cross-context queries that join data are prohibited — use events + projections
