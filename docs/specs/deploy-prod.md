# Spec: Production Deployment Pipeline

**Status**: draft
**Bounded contexts**: devops
**Issue**: [#26](https://github.com/guilherme-andrade/polyglot-ai/issues/26)
**Depends on**: ADR-0005 (Environments), `deploy-staging.md`

## Overview

Manual, intentional, gated production deployment. Triggered from the GitHub
Actions tab with confirmation. Deploys the same Docker image validated on staging.

## Trigger

```yaml
on:
  workflow_dispatch:
    inputs:
      confirm:
        description: 'Deploy to production? Type "prod" to confirm'
        required: true
```

Restricted to users with write access. Uses `environment: production` (GitHub
Environments with required reviewers).

## Deploy steps

1. Verify the git ref being deployed matches the image tagged on staging
2. Deploy server: SSH to prod VM, pull image tagged with git SHA, restart container
3. Deploy app: `eas build --profile production` → submit to App Store / Google Play
4. Post-deploy smoke test (health, basic query)
5. Create GitHub Release with deploy notes (commit range, contributors)

## Safety

- Same image that was smoke-tested on staging (no rebuild)
- Confirmation gate prevents accidental deploys
- `environment: production` enforces required reviewers
- Deploy history is visible in GitHub Actions and Releases

## Acceptance criteria

- [ ] `deploy-prod.yml` committed with `workflow_dispatch` + confirmation input
- [ ] `environment: production` configured with required reviewers
- [ ] Server: deploys Docker image to prod VM
- [ ] App: `eas build --profile production` submits to stores
- [ ] Smoke tests pass after deploy
- [ ] GitHub Release created with deploy notes

## Out of scope

- Canary deploys / blue-green (post-v1)
- Automated rollback (manual for now)
- Database rollback (manual Flyway undo)
