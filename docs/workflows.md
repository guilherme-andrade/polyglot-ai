# Development Workflows

Standard operating procedures for how work gets done. These apply to every feature,
bug fix, and chore in the project.

## Feature Workflow

```
┌──────────┐    ┌──────────┐    ┌───────────┐    ┌──────────┐    ┌─────────┐
│ 1. Spec  │───→│ 2. Issues │───→│ 3. Develop │───→│ 4. Draft │───→│ 5. Ship │
│          │    │          │    │  with AI   │    │    PR    │    │         │
└──────────┘    └──────────┘    └───────────┘    └──────────┘    └─────────┘
```

### 1. Spec (OpenSpec)

A feature starts with a spec in `docs/specs/<feature-name>.md`. Follow the OpenSpec
template (see `docs/README.md`).

**Who writes it:** The person closest to the problem — usually the dev who will
implement it, or the team lead for cross-cutting features.

**What it must cover:**
- Summary (what, why, for whom)
- Bounded contexts touched
- API contracts (endpoints, request/response shapes)
- Data model changes (tables, collections, migrations)
- UI behaviour (screens, states: loading, empty, error, edge cases)
- Acceptance criteria (testable checklist)

### 2. Review & Approve

- Open a PR with just the spec file
- At least one team member (human) must approve. AI can review for completeness
  but cannot approve.
- Discussion happens in the PR — refine the spec until there's alignment
- Once approved, merge the spec into `main`

### 3. Create Issues

From the approved spec, create GitHub issues — one per bounded context when possible.
Each issue must:
- Link to the spec it implements
- Be scoped to a single bounded context
- Have labels: the context (`server`, `app`), the bounded context (`user`, `lesson`, etc.), and the type (`feature`, `bug`, `chore`)
- Be assigned to a specific developer

### 4. Develop with AI

The assigned developer picks up the issue and works with an AI agent.

**AI agent setup (what the agent reads before writing code):**
1. `CLAUDE.md` — global rules
2. The linked spec in `docs/specs/`
3. The relevant subdirectory README (`app/README.md` or `server/README.md`)
4. `docs/architecture.md` — DDD rules if touching server code
5. `docs/testing.md` — how to test

**AI agent implements:**
- Writes code following DDD conventions
- Writes tests alongside code
- Runs tests locally before opening a PR:
  - Server: `./gradlew spotlessCheck && ./gradlew checkstyleMain && ./gradlew test`
  - App: `pnpm format --check && pnpm lint && pnpm tsc --noEmit && pnpm test`
- Tests with curl for API changes, or Expo web + Playwright for UI changes
- Verifies every acceptance criterion from the spec is met

**Testing (from rule #5):**
Work is not ready until tested. The agent must include test evidence in the PR
description: curl output, CI run, screenshots, or Playwright traces.

### 5. Draft PR

- Open a **draft PR** first — do not request review immediately
- CI must pass: format, lint, archunit, typecheck, tests
- If CI fails, fix before marking as ready
- Once CI is green and the implementation is complete, mark the PR as ready
  and request review

### 6. Review & Iterate

- At least one human review required
- AI can (and should) do a first-pass review using `/review`
- Address feedback in new commits
- CI must stay green after each iteration
- Once approved, squash-merge to `main`

### 7. After Merge

- Staging deploys automatically
- Verify the feature on staging (manual smoke test)
- If the feature is urgent, manually trigger `deploy-prod.yml` (contributors only)
- Update the wiki if the feature changes documented architecture, conventions,
  or workflows (rule #4)

## Bug Fix Workflow

Shorter path — no spec required unless the fix changes APIs or data models:

```
1. Issue (detailed body) → 2. Develop with AI → 3. Draft PR → 4. Review → 5. Ship
```

A bug issue body must include:
- Steps to reproduce
- Expected vs actual behavior
- Which bounded contexts are affected
- Acceptance criteria (what "fixed" looks like)

## Chore / Tooling Workflow

Same as bug fix. No spec needed. Issue body describes the change.

## PR Requirements (All Types)

A PR is not ready for review until:

| Gate | Server | App |
|------|--------|-----|
| Formatting | `spotlessCheck` | `prettier --check` |
| Linting | `checkstyleMain` | `eslint` |
| Type checking | Compilation | `tsc --noEmit` |
| DDD boundaries | `archUnit` tests | N/A |
| Tests | `./gradlew test` | `pnpm test` |
| Test evidence | In PR description | In PR description |

## Playbooks

Reusable automation scripts live in `scripts/`. Always check for a playbook before
running commands manually:

```bash
ls scripts/                     # List available playbooks
./scripts/setup.sh              # First-time dev environment
./scripts/verify.sh             # Check environment health
```

If you find yourself repeating a sequence of commands, add it as a playbook.
Playbooks are the single source of truth for how to operate the project.

## Quick Reference for AI Agents

Every AI agent working on this project must follow this sequence:

```
Read CLAUDE.md
  → Read the spec (docs/specs/<feature>.md)
    → Read the subdirectory README (app/ or server/)
      → Read docs/architecture.md (if touching server)
        → Read docs/testing.md
          → Implement + test
            → Open draft PR
              → CI green → mark ready → request review
```

If the spec doesn't exist, stop and flag it. Don't start coding without one.
If a playbook exists for what you're about to do, use it.
If the wiki documents something you're changing, update it.
