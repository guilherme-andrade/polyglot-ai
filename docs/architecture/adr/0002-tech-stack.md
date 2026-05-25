# ADR 0002: Technology Stack

**Status**: proposed
**Date**: 2026-05-25

## Context

We need to choose a full technology stack for a language-learning mobile app with a
gamified lesson engine. The stack must be productive for a 3-person team, safe for AI
agent collaboration, and capable of handling non-trivial domains (curriculum generation,
content matching, semantic search over embeddings).

## Decisions

### Mobile: React Native (Expo SDK 52+) + TypeScript (strict)

**Alternatives considered:**

| Option | Verdict | Reason |
|--------|---------|--------|
| Flutter + Dart | Rejected | Smaller AI training corpus than React/TypeScript — AI agents are less effective with Dart. |
| Native (SwiftUI + Jetpack Compose) | Rejected | Two codebases is too expensive for 3 devs. No shared logic. |
| React Native (bare) | Open | More native module flexibility at the cost of per-platform build config. EAS Build works with both managed and bare — it is not a differentiator. Worth discussing: do we need native modules that managed Expo doesn't support? |

TypeScript in strict mode catches a class of bugs that are especially common in
AI-generated code (null access, implicit any, missing properties).

Key libraries:
- **Expo Router**: File-based routing, standard in Expo SDK 52+
- **TanStack Query**: Server state, caching, and cache invalidation. Prevents the
  most common class of mobile bugs (stale UI state)
- **Zustand**: UI-only state (theme, form state, ephemeral UI). No boilerplate
- **NativeWind**: Tailwind for React Native. One styling language across the team

### Backend: Java 21 LTS + Spring Boot 3.5

**Alternatives considered:**

| Option | Verdict | Reason |
|--------|---------|--------|
| Kotlin / Ktor | Rejected | Smaller ecosystem, smaller AI training corpus. Spring Boot's maturity matters for a small team. |
| Go | Rejected | Weaker type system. DDD with rich domain models is less natural in Go. |
| Node.js / Express or NestJS | Rejected | Dynamic typing (or TypeScript with structural typing) makes DDD boundaries harder to enforce at compile time. Java's package-level access control and strong nominal typing help. |

Why Java is the right call *for this project*:
- **Virtual threads (Java 21)** give lightweight concurrency without reactive programming complexity
- **Compile-time safety** catches cross-context violations that AI agents might introduce
- **ArchUnit** has no equivalent in other ecosystems — it's the enforcement mechanism for DDD
- **Spring Boot 3.5** has mature GraphQL, security, and data integrations

Key libraries:
- **Spring for GraphQL**: Primary API surface
- **Spring Security + JWT**: Token rotation (access + refresh)
- **Flyway**: Database migrations
- **Testcontainers**: Integration tests against real PostgreSQL and MongoDB
- **Spotless**: Palantir Java format, automated in CI

### API: GraphQL (primary) + REST (auth, file upload)

**Alternatives considered:**

| Option | Verdict | Reason |
|--------|---------|--------|
| REST only | Rejected | Mobile clients benefit from precise field selection. The lesson screen needs deeply nested data (lesson → exercises → content references → media URLs). REST would require multiple round trips or bespoke endpoints. |
| GraphQL only | Rejected | Auth flows (login, refresh) are simpler with REST. File uploads are not GraphQL's strength. |

### Data: PostgreSQL 17 + pgvector (primary), MongoDB 8 (secondary)

**PostgreSQL 17 + pgvector** is the primary store for:
- User accounts, preferences, profiles
- Curriculum structure, skill assessments, progress
- Content catalog metadata
- Vector embeddings for semantic search (pgvector)

**MongoDB 8** is a secondary store for:
- User-content interaction logs (high volume, schema-flexible event data)
- Session cache and ephemeral exercise state

**Alternatives considered:**

| Option | Verdict | Reason |
|--------|---------|--------|
| PostgreSQL only (JSONB for logs) | Deferred | Viable fallback. If MongoDB becomes an operational burden, interaction logs fit in JSONB columns. Start with both, simplify later if needed. |
| Pinecone / Weaviate for vectors | Rejected | Operational cost of a third data service. pgvector is mature enough for this scale. |
| Redis for caching | Deferred | MongoDB covers the caching use cases at start. Add Redis when cache performance demands it. |

### Infrastructure: Terraform + Ansible + GitHub Actions

- **Terraform**: Cloud resources (compute, databases, networking). Declarative, auditable.
- **Ansible**: Server configuration (JDK install, app deployment, firewalls). Complements Terraform (TF provisions the VM, Ansible configures it).
- **GitHub Actions**: CI/CD. Already in the repo's ecosystem, no separate CI service to manage.

### Build: Gradle (Kotlin DSL)

Maven alternative rejected. Kotlin DSL gives type-safe build scripts with better IDE
support, and Gradle's incremental builds matter as the codebase grows.

## Deferred Decisions

- **Cloud provider (AWS vs GCP)**: Needs evaluation of managed PostgreSQL offerings,
  GPU availability for ML inference, and team experience. ADR to follow.
- **Observability stack**: Start with Spring Boot Actuator + cloud-provider logs.
  Evaluate OpenTelemetry when we have real traffic patterns.
- **Elasticsearch**: Deferred until full-text search over the content catalog becomes
  necessary. PostgreSQL full-text search may suffice.

## Consequences

- Team needs Java 21 + Spring Boot expertise (or willingness to learn)
- Two databases to manage operationally (mitigated by Terraform)
- GraphQL brings N+1 query risk — must use `@BatchMapping` and DataLoader from day one
- Expo managed workflow means some native modules are unavailable (acceptable trade-off)
