# Spec: PR Template

**Status**: draft
**Bounded contexts**: docs (process)
**Issue**: [#33](https://github.com/guilherme-andrade/polyglot-ai/issues/33)

## Overview

GitHub PR template that enforces the spec-driven, DDD workflow conventions.
PRs are the primary review surface — the template guides both human and AI
contributors.

## Template path

`.github/pull_request_template.md`

## Template contents

```markdown
## Spec
Link to the spec this PR implements:

- `docs/specs/<name>.md`

## Bounded contexts
Which bounded contexts does this PR touch?

- [ ] `user`
- [ ] `curriculum`
- [ ] `content`
- [ ] `lesson`
- [ ] `gamification`
- [ ] `analytics`
- [ ] `devops` / infra
- [ ] `app` (cross-cutting)

## Checklist
- [ ] Spec linked above
- [ ] Tests pass (`./gradlew test` / `pnpm test`)
- [ ] ArchUnit clean (server)
- [ ] Formatting applied (`spotlessApply` / `prettier`)
- [ ] TypeScript typecheck clean (`tsc --noEmit`)
- [ ] Manual test evidence included (curl, screenshot, screen recording)

## Evidence
<!-- Screenshots, curl output, CI results, Playwright traces -->

## Breaking changes
<!-- List any cross-context contract changes. If none, write "None." -->
```

## Acceptance criteria

- [ ] `.github/pull_request_template.md` committed
- [ ] Template includes spec link section
- [ ] Template includes bounded context checklist
- [ ] Template includes pre-flight checklist matching CI gates
- [ ] Template includes evidence and breaking changes sections
- [ ] PRs created after this automatically get the template

## Out of scope

- Issue templates (separate task if needed)
