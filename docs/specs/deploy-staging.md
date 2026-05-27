# Staging Deployment Pipeline

## Purpose

When a PR merges to main and CI passes, the system SHALL automatically deploy to staging with no manual steps. The team MUST be notified on failure. The staging deploy SHALL use the same Docker image and build artifacts validated by CI.

## Requirements

### Requirement: Server MUST auto-deploy to staging VM on merge to main

On push to main with changes under `server/**`, after CI passes, the workflow SHALL build a Docker image, tag it with the git SHA and `staging-latest`, push to the container registry, SSH to the staging VM, pull the image, and restart the container via docker compose.

#### Scenario: Merge to main triggers staging deploy
- GIVEN a PR is merged to main that changes server code
- WHEN the push event fires and CI passes
- THEN a Docker image SHALL be built and pushed
- AND the staging VM SHALL pull and restart that image
- AND a smoke test SHALL verify the health endpoint returns 200

### Requirement: App MUST receive OTA update on merge to main

On push to main with changes under `app/**`, after CI passes, the workflow SHALL run `eas update` to push the JS bundle OTA to preview builds on the staging channel.

#### Scenario: App JS changes reach preview builds automatically
- GIVEN a PR merges to main that changes app code
- WHEN CI passes
- THEN `eas update` SHALL run
- AND preview builds SHALL receive the update OTA

### Requirement: Smoke tests MUST pass after deploy

After deploying, the workflow SHALL run smoke tests: `GET /actuator/health` (expect 200) and a basic GraphQL query. If smoke tests fail, the team SHALL be notified.

#### Scenario: Smoke test failure triggers notification
- GIVEN the server deployed but the health endpoint returns 500
- WHEN the smoke test runs
- THEN the workflow SHALL fail
- AND a notification SHALL be sent (Slack webhook or GitHub issue)

### Requirement: Environment config MUST come from GitHub Secrets

`STAGING_HOST`, `STAGING_SSH_KEY`, and `STAGING_DB_URL` SHALL be sourced from GitHub Secrets. No secrets SHALL be committed to the repository.

#### Scenario: Deploy uses GitHub Secrets for credentials
- GIVEN the deploy workflow runs
- WHEN SSH credentials are needed
- THEN they SHALL be read from `secrets.STAGING_SSH_KEY`
- AND no key material SHALL appear in workflow logs
