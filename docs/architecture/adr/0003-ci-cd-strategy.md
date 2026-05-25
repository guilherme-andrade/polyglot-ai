# ADR 0003: CI/CD Strategy

**Status**: accepted
**Date**: 2026-05-25

## Context

A 3-person team with heavy AI agent contribution needs a CI/CD pipeline that:

1. Catches AI-introduced mistakes before human review
2. Enforces DDD boundaries automatically
3. Gives fast feedback (a slow pipeline will be bypassed)
4. Works for both the Spring Boot server and the React Native app
5. Is cheap to run and simple to maintain

## Decision

### Platform: GitHub Actions

No separate CI service. The repo is on GitHub, the team uses GitHub Projects for task
management, and Actions has first-class integration with branch protection rules.

**Alternatives considered:**

| Option | Verdict | Reason |
|--------|---------|--------|
| Jenkins (self-hosted) | Rejected | Operational overhead. A 3-person team should not be managing CI infrastructure. |
| CircleCI / GitLab CI | Rejected | Adds a second platform to manage. GitHub Actions is sufficient. |

### Pipeline Architecture

Two workflow files, one per project, triggered on PR and merge to `main`:

#### Server Pipeline (`server-ci.yml`)

```
on: [pull_request, push:main]
jobs:
  format:
    - Spotless check (Palantir Java format)
    - Fails fast — no point running tests if formatting is wrong

  lint:
    - Checkstyle analysis
    - Runs in parallel with format check

  archunit:
    - Runs ArchUnit tests (DDD boundary enforcement)
    - FAILURE HERE BLOCKS MERGE — cross-context violation
    - Runs in parallel with format/lint

  test:
    - needs: [format, lint, archunit]
    - ./gradlew test
    - Testcontainers spins up real PostgreSQL and MongoDB
    - Uploads test report as artifact

  build:
    - needs: [test]
    - ./gradlew bootJar (produces fat JAR)
    - Uploads JAR as artifact (for deployment)

  deploy-staging:
    - if: push to main
    - needs: [build]
    - Deploys the JAR to staging environment
    - Runs smoke tests against staging
```

#### App Pipeline (`app-ci.yml`)

```
on: [pull_request, push:main]
jobs:
  format:
    - Prettier check
    - Fails fast

  lint:
    - ESLint (strict TypeScript rules)
    - Runs in parallel with format

  typecheck:
    - tsc --noEmit
    - Catches type errors that AI agents commonly introduce

  test:
    - needs: [format, lint, typecheck]
    - pnpm test (Jest + React Native Testing Library)
    - Uploads coverage report

  build-preview:
    - needs: [test]
    - EAS Build (Expo) — creates a preview build
    - Uploads to Expo for manual testing

  build-production:
    - if: push to main
    - needs: [test]
    - EAS Build — production build
    - Submits to App Store Connect / Google Play Console (manual trigger on release tags)
```

### Quality Gates (all must pass before merge)

| Gate | Server | App | Blocker? |
|------|--------|-----|----------|
| Code formatting | Spotless | Prettier | Yes |
| Linting | Checkstyle | ESLint | Yes |
| Type checking | Java compilation | `tsc --noEmit` | Yes |
| DDD boundaries | ArchUnit | N/A | Yes |
| Unit tests | JUnit 5 | Jest | Yes |
| Integration tests | Testcontainers | N/A | Yes |

### Branch Strategy

```
main          ← protected, requires PR with 1 approval
├── feature/*  ← feature branches (e.g. feature/lesson-generation)
├── fix/*      ← bug fixes
└── chore/*    ← dependency bumps, tooling changes
```

No long-lived branches besides `main`. All work merges via PR. Squash merge to keep
history clean (each PR = one commit on main).

### AI Agent Workflow

1. Agent reads `CLAUDE.md`, relevant spec, and subdirectory README
2. Agent creates a branch: `feature/<slug>` or `fix/<slug>`
3. Agent implements, writing tests alongside code
4. Agent runs the full pipeline locally before pushing (pre-commit hook via Lefthook)
5. Agent opens a PR with the spec linked
6. CI runs automatically — ArchUnit failures block the PR
7. Human reviews and merges

### Pre-commit (Lefthook)

```yaml
# .lefthook.yml (to be created)
pre-commit:
  parallel: true
  commands:
    format-server:
      glob: "server/**/*.java"
      run: cd server && ./gradlew spotlessApply
    format-app:
      glob: "app/**/*.{ts,tsx}"
      run: cd app && pnpm format
    lint-server:
      glob: "server/**/*.java"
      run: cd server && ./gradlew checkstyleMain
    lint-app:
      glob: "app/**/*.{ts,tsx}"
      run: cd app && pnpm lint
    archunit:
      glob: "server/**/*.java"
      run: cd server && ./gradlew test --tests "*ArchUnitTest"
```

## Deferred Decisions

- **E2E tests in CI**: Maestro for mobile E2E requires a simulator. Run these on a
  schedule or manually until we have a device farm budget.
- **Performance testing**: Add when we have baseline traffic numbers.
- **Secrets management**: Start with GitHub Secrets. Evaluate 1Password CLI if the
  secret count grows beyond what's manageable in the UI.

## Consequences

- CI run takes ~5-8 minutes for server (Testcontainers startup is the bottleneck)
- ArchUnit enforcement means certain "quick fixes" that cross contexts will fail CI
  (this is intentional)
- Pre-commit hooks add ~30 seconds to each commit (acceptable trade-off for catching
  issues before CI)
