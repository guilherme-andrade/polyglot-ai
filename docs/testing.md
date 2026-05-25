# Testing Guide

How to test every part of the stack. Work is not ready until it's been tested.
Always include test examples in your PR description.

## Running the stack locally

```bash
# Start databases
cd server && docker compose up -d

# Start backend (http://localhost:8080)
cd server && ./gradlew bootRun

# Start mobile app
cd app && pnpm start
# Then press 'i' for iOS simulator, 'a' for Android emulator, or 'w' for web
```

## Server testing

### Unit + integration tests

```bash
cd server
./gradlew test                          # All tests
./gradlew test --tests "*UserArchUnit*" # Specific test class
```

### Manual API testing (curl)

```bash
# Health check
curl http://localhost:8080/actuator/health

# Login (replace with real credentials once auth is implemented)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@polyglot.ai", "password": "test"}'

# Authenticated request (replace TOKEN from login response)
curl http://localhost:8080/api/lessons/today \
  -H "Authorization: Bearer TOKEN"

# Create a resource
curl -X POST http://localhost:8080/api/lessons \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"language": "es", "level": "beginner"}'
```

### Database inspection

```bash
# PostgreSQL
docker exec -it polyglot-postgres psql -U polyglot -d polyglot
# \dt          — list tables
# \d table_name — describe table
# SELECT * FROM users LIMIT 10;

# MongoDB
docker exec -it polyglot-mongo mongosh polyglot
# db.users.find().limit(10)
```

### Linting and formatting

```bash
cd server
./gradlew spotlessApply    # Auto-fix formatting
./gradlew spotlessCheck    # Check only (runs in CI)
./gradlew checkstyleMain   # Lint check
```

## App testing

### Unit + component tests

```bash
cd app
pnpm test                  # Jest (all tests)
pnpm test -- --watch       # Watch mode
pnpm test -- --coverage    # With coverage report
```

### Browser testing (Expo web)

```bash
cd app
pnpm start                 # Then press 'w' for web
```

Use Playwright MCP to automate browser testing against Expo web:
```bash
# In a separate terminal while Expo web is running
npx playwright test --config=e2e/playwright.config.ts
```

### Type checking and linting

```bash
cd app
pnpm tsc --noEmit          # TypeScript check (runs in CI)
pnpm lint                  # ESLint
pnpm format                # Prettier
```

### E2E testing (Maestro — requires simulator)

```bash
cd app
maestro test e2e/flows/    # Run all E2E flows
maestro test e2e/flows/lesson-flow.yml  # Single flow
```

## CI pipeline (local verification)

Before opening a PR, run the same checks CI will run:

```bash
# Server
cd server
./gradlew spotlessCheck && ./gradlew checkstyleMain && ./gradlew test

# App
cd app
pnpm format --check && pnpm lint && pnpm tsc --noEmit && pnpm test
```

## Pre-commit hooks

Lefthook runs automatically on commit. To run manually:

```bash
lefthook run pre-commit
```

## Testing checklist

Before marking a PR as ready:

- [ ] Unit tests pass (`./gradlew test` / `pnpm test`)
- [ ] Integration tests pass (Testcontainers)
- [ ] ArchUnit tests pass (server — DDD boundaries intact)
- [ ] Formatting clean (`spotlessCheck` / `prettier --check`)
- [ ] Linting clean (`checkstyleMain` / `pnpm lint`)
- [ ] Type checking clean (`tsc --noEmit` for app)
- [ ] Manual API test with curl (server changes)
- [ ] Manual UI test on simulator/emulator/web (app changes)
- [ ] Spec acceptance criteria all met
- [ ] CI is green on the PR
