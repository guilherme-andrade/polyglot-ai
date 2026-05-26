# Spec: Server Scaffold

**Status**: implemented (PR #10)
**Owner**: backend
**Related ADRs**: [0001](../architecture/adr/0001-monorepo-with-ddd.md),
[0002](../architecture/adr/0002-tech-stack.md),
[0003](../architecture/adr/0003-ci-cd-strategy.md)

## Purpose

Describe what the Spring Boot server scaffold under [`server/`](../../server/) provides,
what it deliberately leaves out, and the conditions under which each deferred piece
should be added. New contributors (human or AI) should be able to read this document
and know exactly what they can rely on and what they need to build.

This is **not** the place to document feature behaviour. Feature specs live alongside
this file under [`docs/specs/`](.) and own their own contracts.

## What the scaffold provides

### Build & tooling

| Concern | Choice | Notes |
|---------|--------|-------|
| Build system | Gradle (Kotlin DSL) with wrapper | Wrapper pinned to 8.11.1 |
| JVM | Java 21 LTS | Virtual threads enabled in `application.yml` |
| Framework | Spring Boot 3.5.0 | Dependency-management plugin 1.1.6 |
| Formatter | Spotless + Palantir Java format 2.50.0 | `./gradlew spotlessApply` |
| Linter | Checkstyle 10.18.1 | Minimal config in `server/config/checkstyle/checkstyle.xml`; expand as conventions stabilise |
| Migrations | Flyway | `server/src/main/resources/db/migration/` |

### Spring starters wired in

- `spring-boot-starter-web` — REST controllers and Tomcat
- `spring-boot-starter-graphql` — GraphQL endpoint (primary API per ADR 0002)
- `spring-boot-starter-data-jpa` — relational persistence (PostgreSQL)
- `spring-boot-starter-data-mongodb` — document persistence (MongoDB)
- `spring-boot-starter-security` — security filter chain
- `spring-boot-starter-oauth2-resource-server` — JWT validation
- `spring-boot-starter-validation` — Bean Validation (`@Valid`)
- `spring-boot-starter-actuator` — health, info, probes
- `flyway-core` + `flyway-database-postgresql` — migrations

### Bounded contexts and layers

Six bounded contexts pre-created with the standard four-layer DDD split
(see [`docs/architecture.md`](../architecture.md)):

```
com.polyglotai.<context>.<layer>
```

| Context | Responsibility |
|---------|---------------|
| `user` | Accounts, profiles, preferences |
| `curriculum` | Skill assessment, curriculum generation, progress |
| `content` | Content catalog, metadata, search |
| `lesson` | Lesson generation, exercise templates, scheduling |
| `gamification` | XP, streaks, leaderboards, achievements |
| `analytics` | Usage metrics, learning analytics |

Each context contains four empty layers: `domain`, `application`, `infrastructure`,
`interfaces`. A `package-info.java` exists in every layer so the package is committed
to source control and IDEs recognise it.

### Tests

- **Smoke test**: `PolyglotAiApplicationTests` boots the Spring context (with JPA,
  Flyway, and MongoDB auto-config excluded so the test runs without a database)
- **ArchUnit boundary tests** under `src/test/java/com/polyglotai/archunit`:
  - Domain layer has no Spring / JPA / Hibernate dependencies
  - Layered architecture: `domain ← application ← infrastructure / interfaces`
  - Slices defined by context are free of cycles
- Testcontainers (`postgresql`, `mongodb`) on the test classpath, ready for
  per-feature integration tests

### Local infrastructure

[`server/docker-compose.yml`](../../server/docker-compose.yml) brings up:

| Service | Image | Host port | Notes |
|---------|-------|-----------|-------|
| Postgres | `pgvector/pgvector:pg17` | **5433** | Port 5433, not 5432, to avoid conflict with locally-installed Postgres. Override via `POLYGLOT_DB_URL`. `V1__init.sql` enables the `vector` extension on first migrate. |
| MongoDB | `mongo:8` | 27017 | |

### Container image

Multi-stage [`Dockerfile`](../../server/Dockerfile):

1. `eclipse-temurin:21-jdk` builds the fat JAR
2. `eclipse-temurin:21-jre` runs it as a non-root `polyglot` user with ZGC enabled

## What the scaffold deliberately does **not** provide

Each item below has a documented trigger for when to add it.

| Capability | Status | Trigger to un-defer |
|------------|--------|---------------------|
| RabbitMQ (`spring-boot-starter-amqp`) | Deferred | First async workload that needs durable queuing. Track in a feature spec before adding. |
| Redis cache | Deferred | When MongoDB-based session cache or hot-path reads outgrow what the existing stores handle. |
| Elasticsearch | Deferred | When PostgreSQL full-text search proves insufficient for the content catalog. |
| Production OAuth2 issuer | Deferred | The first auth feature spec (account creation / login). Until then, `POLYGLOT_JWT_ISSUER_URI` is empty and JWT validation is inert (see *Temporary scaffolding* below). |
| GraphQL feature schemas | Deferred to per-feature PRs | Each context contributes its own `*.graphqls` files under `src/main/resources/graphql/` (Spring for GraphQL merges all files on the classpath). |
| `.env.example` | [#58](https://github.com/guilherme-andrade/polyglot-ai/issues/58) | Tracked separately. |
| CI workflow | [#11](https://github.com/guilherme-andrade/polyglot-ai/issues/11) | Tracked separately. |

## Temporary scaffolding

Three pieces of code exist only because the scaffold needs to start cleanly with no
feature code on top. **Remove them in the same PR that introduces the first real
implementation in the relevant area.**

### `com.polyglotai.PingGraphqlController` + `graphql/schema.graphqls`

GraphQL requires `type Query` to declare at least one field, and Spring for GraphQL
fails at startup otherwise. A single `ping: String!` query is declared and resolved
by `PingGraphqlController` so the boot sequence succeeds before any context defines
real queries.

**Remove when**: any bounded context contributes its first `*.graphqls` file with a
real `Query` field.

### `com.polyglotai.SecurityConfig`

Lives at the application root (outside any bounded context) and:

- Permits `/actuator/health/**`, `/actuator/info`, `/graphql`, `/graphiql/**`
- Requires authentication on everything else
- Wires `oauth2ResourceServer().jwt()` only when
  `spring.security.oauth2.resourceserver.jwt.issuer-uri` is configured

**Move when**: the auth feature spec for the `user` context lands. At that point the
config should move into `com.polyglotai.user.infrastructure` (or wherever the auth
feature specifies), and the request-matcher list should be re-evaluated.

### ArchUnit relaxations

[`server/src/test/resources/archunit.properties`](../../server/src/test/resources/archunit.properties)
sets `archRule.failOnEmptyShould=false`, and the layered-architecture rule uses
`.withOptionalLayers(true)`. Both are concessions to the fact that the
context-and-layer packages are empty at scaffold time — without these, ArchUnit
refuses to evaluate rules that match no classes and the test suite fails.

**Flip back when**: every bounded context has at least one production class in
every layer, **or** the team explicitly decides which contexts may remain empty.
At that point delete `archunit.properties` (or set the property to `true`) and
remove `.withOptionalLayers(true)` from the layered-architecture rule.

## How to extend the scaffold

When adding a new bounded context (rare — should be specced first):

1. Create `com.polyglotai.<new-context>.{domain,application,infrastructure,interfaces}`
2. Add a `package-info.java` in each layer matching the existing pattern
3. Update the table in [`docs/architecture.md`](../architecture.md) and this spec

When adding a deferred capability (RabbitMQ, Redis, etc.):

1. Write or update a feature spec that justifies the addition
2. Add the dependency in `server/build.gradle.kts`
3. Add the service to `server/docker-compose.yml` if it runs locally
4. Update the *What the scaffold deliberately does not provide* table above —
   either remove the row (now provided) or update its status

## Verification (what "scaffold works" means)

The conditions verified at the time of #10's merge — keep these green on every
change to the scaffold itself:

- [ ] `./gradlew check` passes (Spotless, Checkstyle, smoke + ArchUnit tests)
- [ ] `./gradlew bootRun` starts; `/actuator/health` returns `{"status":"UP"}`
- [ ] `POST /graphql {"query":"{ ping }"}` returns `{"data":{"ping":"pong"}}`
- [ ] `docker compose up -d` brings up Postgres (port 5433) and MongoDB (port 27017)
- [ ] Flyway applies `V1__init.sql` on first run (enables `pgvector`)

See [`docs/testing.md`](../testing.md) for the standard testing workflow.

## Form: spec vs ADR

This document is a **spec**, not an ADR. The architectural choices it depends on
(monorepo + DDD, Java + Spring Boot, GraphQL primary, PG + Mongo, Gradle, GitHub
Actions) are already recorded in ADRs 0001–0005. This document only describes
what was materialised and what was deferred when those decisions were
implemented — descriptive, not deliberative. If the team later changes one of the
underlying decisions, the change goes through a new ADR and this spec is updated
to match.
