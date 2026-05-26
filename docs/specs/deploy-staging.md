# Spec: Staging Deployment Pipeline

**Status**: draft
**Bounded contexts**: devops
**Issue**: [#25](https://github.com/guilherme-andrade/polyglot-ai/issues/25)
**Depends on**: ADR-0005 (Environments), ADR-0006 (Cloud Provider), `ci-pipelines.md`

## Overview

When a PR merges to main and CI passes, automatically deploy to staging for fast
feedback. No manual steps. Team gets notified on failure.

## Trigger

```yaml
on:
  push:
    branches: [main]
    paths: ['server/**', 'app/**', '.github/workflows/deploy-staging.yml']
```

After `server-ci.yml` and `app-ci.yml` both pass on main.

## Server deploy

1. Build Docker image from `server/Dockerfile`
2. Tag with git SHA and `staging-latest`
3. Push to container registry (Hetzner-hosted or GitHub Container Registry)
4. SSH to staging VM, pull image, restart container via docker compose
5. Smoke test: `GET /actuator/health`, basic GraphQL query (`{ __typename }`)
6. Notify on failure (Slack or GitHub issue)

## App deploy (OTA + build)

1. `eas update` — push JS bundle OTA to preview builds (staging channel)
2. `eas build --profile preview` — new installable for TestFlight / internal testing track (only when native deps change — option, not every deploy)

## Environment

- `STAGING_HOST`, `STAGING_SSH_KEY`, `STAGING_DB_URL` from GitHub Secrets
- App points to staging API URL via EAS Build env vars

## Acceptance criteria

- [ ] `deploy-staging.yml` committed and triggers on push to main
- [ ] Server: Docker image built, pushed, deployed to staging VM
- [ ] Smoke tests pass after deploy (return 200)
- [ ] App: `eas update` runs (stubbed if EAS not configured)
- [ ] Failure notification wired (Slack webhook or GitHub issue creation)

## Out of scope

- Database migrations (Flyway runs on server startup — if it fails, deploy fails)
- Rollback (manual for now)
- Production deploy (separate spec: `deploy-prod.md`)
