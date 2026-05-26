# Spec: Lefthook Pre-Commit Hooks

**Status**: draft
**Bounded contexts**: devops
**Issue**: [#27](https://github.com/guilherme-andrade/polyglot-ai/issues/27)

## Overview

Pre-commit hooks via Lefthook that run locally before every commit to catch
formatting, linting, and DDD boundary violations before they hit CI.

## Hooks

| Hook | Scope | Command |
|------|-------|---------|
| Spotless format | `server/**/*.java` | `cd server && ./gradlew spotlessApply` |
| Checkstyle | `server/**/*.java` | `cd server && ./gradlew checkstyleMain checkstyleTest` |
| ArchUnit | `server/**/*.java` | `cd server && ./gradlew test --tests '*ArchUnit*'` |
| Prettier format | `app/**/*.{ts,tsx}` | `cd app && pnpm format` |
| ESLint | `app/**/*.{ts,tsx}` | `cd app && pnpm lint` |

## Configuration

`.lefthook.yml` at repo root. Install with `lefthook install`.

Key config rules:
- `parallel: true` for independent hooks (format + lint run concurrently)
- `fail_text: "Pre-commit checks failed. Run 'lefthook run pre-commit' to see details."`
- Staged files only where Gradle/ESLint support it; fall back to all files
- Skip with `LEFTHOOK=0 git commit ...` in emergencies (documented, not promoted)

## Acceptance criteria

- [ ] `.lefthook.yml` committed at repo root
- [ ] `lefthook install` registers git hooks
- [ ] `git commit` triggers format + lint + ArchUnit on changed files
- [ ] Failed hooks block the commit with clear output
- [ ] `lefthook run pre-commit` runs all hooks manually
- [ ] Hooks complete in under 30 seconds (incremental where possible)

## Out of scope

- Commit message linting (conventional commits — add if team agrees)
- Secret scanning (pre-commit hook for .env files — add if needed)
