# Lefthook Pre-Commit Hooks

## Purpose

Pre-commit hooks via Lefthook SHALL run formatting, linting, and ArchUnit checks locally before every commit. This MUST catch issues before they reach CI, saving approximately 5 minutes per failed CI run. Hooks SHALL run in parallel where possible and complete in under 30 seconds.

## Requirements

### Requirement: Java files MUST be checked by Spotless and Checkstyle

When a commit includes changes to `server/**/*.java`, Lefthook SHALL run `./gradlew spotlessApply` for formatting and `./gradlew checkstyleMain checkstyleTest` for linting. These two hooks MAY run in parallel.

#### Scenario: Unformatted Java file is blocked
- GIVEN a Java file has formatting violations
- WHEN `git commit` is run
- THEN Spotless SHALL fail
- AND the commit SHALL be blocked
- AND the output SHALL show which files need formatting

### Requirement: ArchUnit boundary tests MUST run on server changes

When a commit includes changes to `server/**/*.java`, Lefthook SHALL run ArchUnit tests. If a DDD boundary violation is detected, the commit SHALL be blocked.

#### Scenario: Cross-context import is caught at commit time
- GIVEN a change imports from another bounded context's domain package
- WHEN `git commit` is run
- THEN the ArchUnit hook SHALL fail
- AND the output SHALL name the violating import

### Requirement: TypeScript files MUST be checked by Prettier and ESLint

When a commit includes changes to `app/**/*.{ts,tsx}`, Lefthook SHALL run `pnpm format` and `pnpm lint`. These two hooks MAY run in parallel.

#### Scenario: Linting error blocks commit
- GIVEN a TSX file has an unused variable (ESLint error)
- WHEN `git commit` is run
- THEN the ESLint hook SHALL fail
- AND the commit SHALL be blocked

### Requirement: Spec files MUST be linted for OpenSpec format

When a commit includes changes to `docs/specs/*.md`, Lefthook SHALL run `node scripts/lint-specs.js` to validate OpenSpec format. When changes include `docs/architecture/adr/*.md`, Lefthook SHALL run `node scripts/lint-specs.js --adr` to validate ADR format. Errors SHALL block the commit.

#### Scenario: Spec with missing Purpose section is blocked
- GIVEN a spec file has no `## Purpose` section
- WHEN `git commit` is run
- THEN the lint-specs hook SHALL fail with an error message
- AND the commit SHALL be blocked

### Requirement: Hooks MUST be installable and skippable

`lefthook install` SHALL register git hooks. Setting `LEFTHOOK=0` SHALL skip all hooks for emergency commits. This escape hatch SHALL be documented in the Lefthook config, not promoted.

#### Scenario: Emergency commit bypasses hooks
- GIVEN hooks are installed
- WHEN `LEFTHOOK=0 git commit` is run
- THEN no hooks SHALL execute
- AND the commit SHALL proceed regardless of formatting state
