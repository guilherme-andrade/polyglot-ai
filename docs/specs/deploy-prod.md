# Production Deployment Pipeline

## Purpose

Production deployments MUST be manual, intentional, gated, and auditable. The workflow SHALL deploy the exact same Docker image that passed staging smoke tests — no rebuild. A confirmation gate SHALL prevent accidental deploys.

## Requirements

### Requirement: Deploy MUST use workflow_dispatch with confirmation input

The production deploy workflow SHALL be triggered via `workflow_dispatch` with a required confirmation input. The user MUST type "prod" to proceed. The workflow SHALL be restricted to users with write access to the repository.

#### Scenario: Deploy without confirmation is rejected
- GIVEN the workflow is triggered manually
- WHEN the confirmation input does not equal "prod"
- THEN the deploy SHALL abort before any infrastructure change

#### Scenario: Authorised user confirms production deploy
- GIVEN a user with write access triggers the workflow
- WHEN the confirmation input equals "prod"
- THEN the deploy SHALL proceed to the production environment

### Requirement: Production MUST use the same image as staging

The production deploy SHALL deploy the Docker image tagged with the git SHA that was validated on staging. No rebuild SHALL occur. The image digest from staging MUST match the image deployed to production.

#### Scenario: Image mismatch is detected
- GIVEN a git SHA is selected for production deploy
- WHEN the image tagged with that SHA is not found in the registry
- THEN the deploy SHALL fail with a clear error

### Requirement: GitHub Environments MUST gate production deploys

The workflow SHALL use `environment: production` with required reviewers configured in GitHub repository settings. Required reviewers MUST approve before the deploy job executes.

#### Scenario: Required reviewer blocks deploy
- GIVEN the production environment has required reviewers
- WHEN the deploy workflow is triggered
- THEN the job SHALL pause until a reviewer approves
- AND if no reviewer approves within the timeout, the job SHALL fail

### Requirement: GitHub Release MUST be created on deploy

After a successful production deploy, the workflow SHALL create a GitHub Release with the deploy notes, commit range, and list of contributors since the last release.

#### Scenario: Successful deploy creates a release
- GIVEN the production deploy completes successfully
- WHEN the deploy job finishes
- THEN a GitHub Release SHALL be created
- AND the release notes SHALL include the commit range and contributors

### Requirement: Post-deploy smoke tests MUST pass

After deploying to production, the workflow SHALL run the same smoke tests as staging: health endpoint and basic GraphQL query. If smoke tests fail, the team SHALL be notified immediately.

#### Scenario: Production smoke test fails
- GIVEN the production deploy completed but the app is unhealthy
- WHEN the smoke test runs
- THEN the workflow SHALL fail
- AND a notification SHALL be sent
