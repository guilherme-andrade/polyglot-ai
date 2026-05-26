# server/.env.example Template

## Purpose

A new contributor MUST be able to set up their development environment without reading `application.yml`. The `.env.example` file SHALL list every environment variable the server references, with placeholder values and one-line comments. Real secrets SHALL NOT appear in the example file.

## Requirements

### Requirement: .env.example MUST list every env var from application.yml

`server/.env.example` SHALL contain entries for: `POLYGLOT_DB_URL`, `POLYGLOT_DB_USER`, `POLYGLOT_DB_PASSWORD` (PostgreSQL), `POLYGLOT_MONGO_URI` (MongoDB), `POLYGLOT_JWT_ISSUER_URI` (JWT — blank for local dev), and `POLYGLOT_PORT` (server port). Each entry SHALL have a one-line comment.

#### Scenario: New contributor copies .env.example and starts the server
- GIVEN a new contributor clones the repo
- WHEN they run `cp server/.env.example server/.env` and start docker compose
- THEN the server SHALL start with those values
- AND the contributor SHALL NOT need to read application.yml

### Requirement: .env MUST be in server/.gitignore

`server/.gitignore` SHALL contain a rule ignoring `.env` files so real values are never committed. This SHALL be verified — the scaffold already includes this rule.

#### Scenario: .env is ignored by git
- GIVEN `.env` exists in `server/`
- WHEN `git status` is run
- THEN `.env` SHALL NOT appear as an untracked file

### Requirement: README MUST reference .env.example

`server/README.md` SHALL include a section telling developers to copy `.env.example` to `.env` for local overrides. `.env.example` SHALL be documented as the source of truth for available environment variables.

#### Scenario: README points to .env.example
- GIVEN a contributor reads the server README
- WHEN they look for environment setup instructions
- THEN they SHALL see instructions to copy `.env.example` to `.env`
