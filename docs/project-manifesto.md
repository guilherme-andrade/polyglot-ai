# Project Manifesto

## Vision

Polyglot AI is a mobile language-learning app that combines the engagement of gamified
micro-lessons (in the style of Duolingo) with content the learner already loves — videos,
music, films, documentaries, books, podcasts. Every day the user receives a personalised
lesson built from real-world content, exactly at their skill level.

Polyglot learns about the user over time: their vocabulary range, grammar gaps, interests,
and optimal session length. It then builds and continuously refines a curriculum that keeps
the user in their zone of proximal development.

## Team & Workflow

- **Team size**: 3 developers.
- **Collaboration**: GitHub private monorepo, GitHub Projects for task management.
- **AI posture**: heavy use of Claude Code / AI coding agents. Every convention in this
  document exists so that agents (and humans) can work on different parts of the system
  simultaneously without stepping on each other.

## Tech Stack

### Mobile App (`/app`)
| Concern | Choice | Notes |
|---------|--------|-------|
| Framework | React Native (Expo SDK 52+) | Managed workflow; iOS + Android from one codebase |
| Language | TypeScript (strict) | |
| Navigation | Expo Router (file-based) | |
| State Management | Zustand + React Query (TanStack Query) | Zustand for UI state; React Query for server/async state |
| Styling | NativeWind (Tailwind for RN) | |
| Testing | Jest + React Native Testing Library + Maestro (E2E) | |

### Backend (`/server`)
| Concern | Choice | Notes |
|---------|--------|-------|
| Language | Java 21 LTS | Virtual threads enabled |
| Framework | Spring Boot 3.5.x | |
| Build | Gradle (Kotlin DSL) | |
| API style | GraphQL (Spring for GraphQL) + REST for auth / file upload | |
| Auth | Spring Security + JWT (Access + Refresh token rotation) | |
| Messaging | RabbitMQ (if async work needed) | |
| Testing | JUnit 5, Testcontainers, AssertJ | |

### Data
| Concern | Choice | Notes |
|---------|--------|-------|
| Primary relational | PostgreSQL 17 | User accounts, curriculum, progress |
| Vectors | pgvector | Semantic search over content, exercise embeddings |
| Document / cache | MongoDB 8 | User-content interaction logs, session cache |
| Search | (future) Elasticsearch | Full-text search over content catalog |

### DevOps & Infrastructure
| Concern | Choice | Notes |
|---------|--------|-------|
| Infrastructure as Code | Terraform (HCL) | |
| Configuration management | Ansible | Server provisioning |
| CI/CD | GitHub Actions | Build, test, deploy pipeline |
| Cloud | TBD (AWS / GCP) | Decision pending evaluation |

### Specifications
| Concern | Choice | Notes |
|---------|--------|-------|
| Spec format | OpenSpec | All feature specs live in `/docs/specs/` |

## Environment & Tooling

- **Package manager (app)**: pnpm
- **Java formatter**: Spotless (Palantir Java format)
- **TypeScript formatter**: Prettier
- **Linting**: ESLint (app), Checkstyle (server)
- **Pre-commit**: Lefthook (managed in `.lefthook.yml`)
- **Secrets**: Never commit secrets. Use `.env.example` templates. Real values go through
  GitHub Secrets or 1Password CLI.

## Development Workflow

1. **Spec first**: Write or update the OpenSpec in `/docs/specs/<feature>.md`.
2. **Branch**: `feature/<slug>`, `fix/<slug>`, or `chore/<slug>`.
3. **Implement**: Follow DDD boundaries. Write tests alongside code.
4. **PR**: Keep PRs scoped to one bounded context where possible. Link the spec.
5. **Review**: At least one human review before merge. AI can (and should) review first.
