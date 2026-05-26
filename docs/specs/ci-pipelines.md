# Spec: CI Pipelines (Server + App)

**Status**: draft
**Bounded contexts**: devops
**Issue**: [#24](https://github.com/guilherme-andrade/polyglot-ai/issues/24), [#11](https://github.com/guilherme-andrade/polyglot-ai/issues/11)
**Depends on**: ADR-0003 (CI/CD Strategy), ADR-0005 (Environments)

## Overview

GitHub Actions workflows that run on every PR and push to main. Quality gates
must pass before merge. Covers both server (Java/Gradle) and app (TypeScript/Expo).

## Server CI (`server-ci.yml`)

Triggered on PRs and pushes to main with paths: `server/**`.

| Job | Command | Notes |
|-----|---------|-------|
| `format` | `./gradlew spotlessCheck` | Fails fast |
| `lint` | `./gradlew checkstyleMain checkstyleTest` | Parallel with `format` |
| `archunit` | `./gradlew test --tests '*ArchUnit*'` | Parallel; blocks merge |
| `test` | `./gradlew test` | Needs `format`, `lint`, `archunit`; Testcontainers; uploads report |
| `build` | `./gradlew bootJar` | Needs `test`; uploads JAR artifact |

Uses: `actions/checkout@v4`, `actions/setup-java@v4` (temurin, java-21, gradle cache),
`gradle/actions/setup-gradle@v4`, `working-directory: server`.

## App CI (`app-ci.yml`)

Triggered on PRs and pushes to main with paths: `app/**`.

| Job | Command | Notes |
|-----|---------|-------|
| `format` | `pnpm format --check` | Prettier |
| `lint` | `pnpm lint` | ESLint strict TypeScript |
| `typecheck` | `pnpm tsc --noEmit` | Full project type check |
| `test` | `pnpm test` | Jest + RNTL; needs `format`, `lint`, `typecheck` |

Uses: `actions/checkout@v4`, `actions/setup-node@v4` (node 22, pnpm cache),
`working-directory: app`.

## CI naming convention

- Workflow files: `server-ci.yml`, `app-ci.yml`
- Job names: lowercase, hyphenated (`format`, `lint`, `typecheck`, `test`, `build`)
- Artifact names: `server-test-report`, `server-jar`, `app-coverage`

## Acceptance criteria

- [ ] `server-ci.yml` committed and runs on PRs to main
- [ ] `app-ci.yml` committed and runs on PRs to main
- [ ] All jobs pass against current scaffold code
- [ ] Failed checks block merge (branch protection)
- [ ] Test reports uploaded as artifacts on failure
- [ ] Gradle and pnpm caches configured for fast subsequent runs

## Out of scope

- `deploy-staging` and `deploy-prod` workflows (separate specs: `deploy-staging.md`, `deploy-prod.md`)
- Branch protection rules (manual GitHub Settings config)
