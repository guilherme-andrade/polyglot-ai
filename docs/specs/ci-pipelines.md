# CI Pipelines

## Purpose

GitHub Actions workflows that run quality gates on every PR and push to main for both server (Java/Gradle) and app (TypeScript/Expo). Failed checks MUST block merge. Both pipelines SHALL mirror the local verification commands documented in `docs/testing.md`.

## Requirements

### Requirement: Server CI MUST gate on format, lint, ArchUnit, and test

The server CI workflow SHALL run `spotlessCheck` (format), `checkstyleMain checkstyleTest` (lint), ArchUnit tests, and the full test suite on every PR and push to main with changes under `server/**`. Format and lint jobs MUST run in parallel. Test SHALL depend on format, lint, and ArchUnit all passing.

#### Scenario: PR with formatting errors is blocked
- GIVEN a PR changes a Java file with incorrect formatting
- WHEN the server CI workflow runs
- THEN the `format` job SHALL fail
- AND the `test` job SHALL be skipped
- AND the PR SHALL show a failing check

#### Scenario: ArchUnit violation blocks merge
- GIVEN a PR introduces a cross-context import violation
- WHEN the server CI workflow runs
- THEN the `archunit` job SHALL fail
- AND the PR SHALL be blocked from merging

### Requirement: App CI MUST gate on format, lint, typecheck, and test

The app CI workflow SHALL run `pnpm format --check`, `pnpm lint`, `pnpm tsc --noEmit`, and `pnpm test` on every PR and push to main with changes under `app/**`. Type checking MUST use strict mode. Test SHALL depend on format, lint, and typecheck all passing.

#### Scenario: TypeScript error blocks merge
- GIVEN a PR introduces a type error
- WHEN the app CI workflow runs
- THEN the `typecheck` job SHALL fail
- AND the PR SHALL be blocked from merging

### Requirement: Test reports MUST be uploaded on failure

When the test job fails, the workflow SHALL upload test reports as artifacts so contributors can diagnose failures without re-running locally.

#### Scenario: Failed tests produce downloadable artifacts
- GIVEN a test assertion fails
- WHEN the test job completes
- THEN the test report XML SHALL be uploaded as a workflow artifact
- AND the artifact SHALL be accessible from the GitHub Actions run page

### Requirement: Caching MUST be configured for fast repeat runs

The server workflow SHALL use `gradle/actions/setup-gradle@v4` for Gradle caching. The app workflow SHALL use pnpm caching via `actions/setup-node@v4`. Cache keys SHALL include the lockfile hash.

#### Scenario: Second CI run is faster than the first
- GIVEN a prior CI run completed and cached dependencies
- WHEN a new commit is pushed with the same lockfile
- THEN dependency installation SHALL hit the cache
- AND total workflow time SHALL be at least 50% faster than a cold run
