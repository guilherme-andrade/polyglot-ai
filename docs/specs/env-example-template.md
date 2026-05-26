# Spec: server/.env.example Template

**Status**: draft
**Bounded contexts**: server (infrastructure)
**Issue**: [#58](https://github.com/guilherme-andrade/polyglot-ai/issues/58)

## Overview

Create `server/.env.example` listing every env var referenced from `application.yml`
with placeholder values and comments. A new contributor should not have to read
`application.yml` to figure out what env vars exist.

## Env vars

```bash
# PostgreSQL (matches docker-compose.yml; host port 5433 avoids local conflict)
POLYGLOT_DB_URL=jdbc:postgresql://localhost:5433/polyglot
POLYGLOT_DB_USER=polyglot
POLYGLOT_DB_PASSWORD=polyglot

# MongoDB
POLYGLOT_MONGO_URI=mongodb://localhost:27017/polyglot

# JWT — leave issuer blank in local dev to skip OAuth2 resource server
POLYGLOT_JWT_ISSUER_URI=

# Server port
POLYGLOT_PORT=8080
```

## Acceptance criteria

- [ ] `server/.env.example` committed with every env var from `application.yml`
- [ ] `server/README.md` mentions copying `.env.example` to `.env` for local overrides
- [ ] `server/.gitignore` ignores `.env` (verify)
- [ ] No real secrets in the example file (placeholders only)
