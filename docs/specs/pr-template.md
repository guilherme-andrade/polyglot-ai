# PR Template

## Purpose

Every pull request MUST use a template that enforces the spec-driven, DDD workflow. The template SHALL guide both human and AI contributors to link their spec, declare bounded contexts touched, and verify pre-flight checks before requesting review.

## Requirements

### Requirement: Template MUST include spec link section

The PR template SHALL have a section for linking the spec this PR implements, referencing `docs/specs/<name>.md`. If no spec applies (e.g. pure infra chore), the contributor SHALL explain why.

#### Scenario: PR links to spec
- GIVEN a PR implements the onboarding feature
- WHEN the PR is created
- THEN the description SHALL contain a link to `docs/specs/onboarding.md`

### Requirement: Template MUST include bounded context checklist

The template SHALL list all bounded contexts (`user`, `curriculum`, `content`, `lesson`, `gamification`, `analytics`, `devops`, `app`) as a checklist. The contributor SHALL check all that this PR touches.

#### Scenario: Cross-context PR checks two boxes
- GIVEN a PR touches both `lesson` and `gamification`
- WHEN the PR is created
- THEN both `lesson` and `gamification` SHALL be checked
- AND a reviewer SHALL verify cross-context contracts are followed

### Requirement: Template MUST include pre-flight checklist

The template SHALL include a checklist matching CI gates: spec linked, tests pass, ArchUnit clean, formatting applied, TypeScript typecheck clean, manual test evidence included.

#### Scenario: Pre-flight checklist is visible in PR body
- GIVEN a new PR is created
- WHEN the template renders
- THEN the checklist SHALL include: spec linked, tests pass, ArchUnit clean, formatting applied, typecheck clean, manual test evidence

### Requirement: Template MUST include evidence and breaking changes sections

The template SHALL have an "Evidence" section for screenshots, curl output, CI results, or Playwright traces. A "Breaking changes" section SHALL list cross-context contract changes. If none, the contributor SHALL write "None."

#### Scenario: UI change includes screenshot evidence
- GIVEN a PR changes a screen layout
- WHEN the PR is created
- THEN the Evidence section SHALL contain a screenshot or screen recording

### Requirement: Template SHALL live at .github/pull_request_template.md

The template file SHALL be at `.github/pull_request_template.md`. GitHub SHALL automatically populate new PRs with this template.

#### Scenario: New PR auto-populates the template
- GIVEN `.github/pull_request_template.md` exists on main
- WHEN any contributor opens a PR
- THEN the PR body SHALL be pre-filled with the template
