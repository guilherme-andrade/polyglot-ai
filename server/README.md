# Polyglot AI — Backend Server

Java 21 + Spring Boot 3.5 backend, organised with Domain-Driven Design.

## Stack

- **Language**: Java 21 LTS (virtual threads enabled)
- **Framework**: Spring Boot 3.5.x
- **Build**: Gradle (Kotlin DSL)
- **API**: GraphQL (primary) + REST (auth, file upload)
- **Auth**: Spring Security + JWT (access + refresh token rotation)
- **Messaging**: RabbitMQ (for async workloads)
- **Testing**: JUnit 5, Testcontainers, AssertJ

## Getting Started

Requires JDK 21 (Temurin or OpenJDK) on `JAVA_HOME` and Docker.

```bash
# Start PostgreSQL 17 + pgvector (host port 5433) and MongoDB 8 (host port 27017)
docker compose up -d

# Run the server (http://localhost:8080)
./gradlew bootRun

# Run tests (smoke + ArchUnit boundary tests)
./gradlew test

# Format code (Palantir Java format)
./gradlew spotlessApply

# Full CI pipeline locally: format, lint, archunit, test
./gradlew check
```

Postgres is published on host port **5433** to avoid conflict with locally-installed
Postgres on the default 5432. Override the JDBC URL via `POLYGLOT_DB_URL` if needed.

## Package Structure (DDD)

```
com.polyglotai.<context>.<layer>
```

### Bounded Contexts

| Context | Responsibility |
|---------|---------------|
| `user` | Accounts, profiles, preferences |
| `curriculum` | Skill assessment, curriculum generation, progress |
| `content` | Content catalog, metadata, search |
| `lesson` | Lesson generation, exercise templates, scheduling |
| `gamification` | XP, streaks, leaderboards, achievements |
| `analytics` | Usage metrics, learning analytics |

### Layers (within each context)

| Layer | Purpose | Framework deps |
|-------|---------|---------------|
| `domain` | Entities, value objects, domain services, repository interfaces | Zero |
| `application` | Use-cases / application services | Interfaces only |
| `infrastructure` | Repository impls, JPA entities, API clients | Spring, JPA, etc. |
| `interfaces` | REST controllers, GraphQL resolvers, DTOs | Spring Web/GraphQL |

### Rules

1. Contexts do not import from each other's `domain` or `infrastructure` packages.
2. Cross-context communication goes through application services or domain events.
3. DTOs at every interface boundary — domain objects never leak outward.
4. No shared kernel without an explicit spec.

## Database

- **PostgreSQL 17**: User data, curriculum, progress (primary relational store)
- **pgvector**: Content and exercise embeddings for semantic search
- **MongoDB 8**: User-content interaction logs, session cache
- **Migrations**: Flyway (managed in `src/main/resources/db/migration`)
