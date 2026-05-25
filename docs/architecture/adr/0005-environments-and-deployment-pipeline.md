# ADR 0005: Environments & Deployment Pipeline

**Status**: proposed
**Date**: 2026-05-25

Supersedes the environments and deployment trigger sections of ADR 0004.

## Context

We need four environments (local, staging, prod backends + a mobile testing channel)
and clear deployment triggers for each. The pipeline must:

1. Give fast feedback — staging deploys automatically, no manual step
2. Keep prod safe — prod deploys are intentional, gated, and auditable
3. Give mobile testers working builds without App Store review delays
4. Be enforced by GitHub permissions, not hoping people follow a convention

## Decisions

### Environments

| Environment | Purpose | Backend | Database | Mobile |
|-------------|---------|---------|----------|--------|
| `local` | Individual developer workflow | Docker Compose | Docker Compose (PG + Mongo) | Expo dev server |
| `staging` | Integration testing, pre-release validation | Deployed VM | Managed, seeded with test data | EAS preview build pointing to staging API |
| `prod` | Live users | Deployed VM | Managed, production | App Store + Google Play builds |

There is no shared `dev` environment. Every developer runs the full stack locally via
Docker Compose for the backend and Expo dev server for the app. A shared dev
environment creates contention (who broke the DB?) without benefit at a 3-person scale.

### Mobile Testing Environment

The "testing env" for mobile is a combination of:

1. **EAS Preview Builds** — installable `.app` / `.apk` files distributed to internal
   testers. Built automatically on push to `main`. These point to the staging backend.
   No App Store review required.

2. **EAS Update (OTA)** — JavaScript and asset changes are pushed over-the-air to
   preview builds. The app checks for updates on launch. This means a tester installs
   the preview build once, then receives JS updates automatically — no rebuild needed.

3. **TestFlight / Google Play Internal Testing** — for release candidates that need
   native code changes. Triggered manually as part of the prod release workflow.

```
push to main
  ├── Backend: deploy to staging VM
  └── Mobile: EAS Update (OTA) → existing preview builds get new JS
              EAS Build (preview) → new installable for first-time testers
```

### Deployment Triggers

#### Staging — automatic on push to main

```
on:
  push:
    branches: [main]
```

When a PR is merged to `main` and CI passes (format, lint, archunit, tests, build):

1. **Server**: Build fat JAR → build Docker image → push to container registry →
   pull on staging VM → restart container. Run smoke tests (GET /actuator/health,
   basic GraphQL query).
2. **Mobile**: Run `eas update` to push JS bundle OTA. Run `eas build --profile preview`
   for a new preview build (if native dependencies changed).

All automated. No manual approval. If staging deploy fails, the team gets notified in
GitHub and can revert via a new PR.

#### Production — on-demand, contributors only

```
on:
  workflow_dispatch:
    inputs:
      environment:
        description: 'Confirm production deploy'
        required: true
        type: choice
        options:
          - 'I confirm deployment to production'
```

A GitHub Actions workflow named `deploy-prod.yml` that:

1. Is triggered manually from the Actions tab (`workflow_dispatch`)
2. Requires a confirmation input (selecting "I confirm deployment to production")
3. Can only be triggered by users with **write access** to the repo (GitHub's built-in
   permission model — outside collaborators cannot trigger manual workflows)
4. Deploys the same Docker image that was validated on staging
5. Runs a post-deploy smoke test and reports success/failure

**Why not `repository_dispatch` or a tag-based trigger?**
`workflow_dispatch` is the simplest GitHub-native mechanism. It appears in the Actions
UI with a "Run workflow" button. The confirmation dropdown prevents accidental
triggers. Write-access gating is free and requires no additional configuration.

### Pipeline Overview

```
PR opened
  └── CI runs (format, lint, archunit, test, build)
       └── PR merged to main
            └── CI passes on main
                 ├── auto-deploy to staging
                 │    ├── server: Docker image → staging VM
                 │    ├── mobile: EAS Update (OTA)
                 │    └── mobile: EAS preview build
                 │
                 └── [manual trigger: deploy-prod.yml]
                       └── server: Docker image → prod VM
                       └── mobile: EAS production build
                            └── manual: submit to App Store / Play Store
```

### GitHub Environments (Optional Enhancement)

GitHub's built-in Environments feature can add an extra layer of protection for prod:

```yaml
# .github/workflows/deploy-prod.yml
jobs:
  deploy:
    environment: production
    steps:
      - ...
```

This enables:
- Required reviewers (e.g., 2 people must approve before the job runs)
- Wait timer (e.g., 5-minute delay before deploy to allow aborting)
- Deployment history tracking in the GitHub UI
- Protection rules that prevent deployment if CI is failing

This is optional at project start but costs nothing and is quick to configure.

### Staging Data

The staging database is seeded with anonymized test data, not a copy of production.
At pre-revenue stage, there is no production data to copy. When real users exist, a
periodic sync from prod → staging (with PII scrubbing) can be added.

## Consequences

- No shared dev environment — every dev must run Docker Compose locally. This requires
  Docker Desktop or equivalent on every dev machine
- OTA updates mean mobile testers don't need to reinstall the app for JS changes.
  This is a significant velocity win for a small team
- `workflow_dispatch` is not API-friendly for external tooling. If we later want a
  Slack bot or chatbot to trigger deploys, we'd switch to `repository_dispatch`
- The confirmation dropdown in `workflow_dispatch` is UI-only protection — it doesn't
  prevent someone from selecting "confirm" by accident. GitHub Environments with
  required reviewers is the stronger mechanism if we want it later
