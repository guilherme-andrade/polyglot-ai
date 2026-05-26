---
name: review
description: Comprehensive PR review — runs linters, tests, checks DDD conventions, verifies specs, manually tests APIs with curl, tests mobile with Maestro, and posts the review to GitHub using the reviewer bot account. Use when the user asks to review a PR, audit code, or check someone else's work.
disable-model-invocation: true
argument-hint: "[pr-number|all]"
allowed-tools: Bash(git *), Bash(gh *), Bash(./gradlew *), Bash(pnpm *), Bash(docker *), Bash(curl *), Bash(maestro *), Bash(npx *), Bash(source *), Read, Grep, Glob, Write
---

# PR Review

Thorough, test-driven PR review. Reviews are **not** "skim the diff" — they include
manual testing, spec verification, linting, DDD boundary checks, and a posted
GitHub review with inline comments.

## Quick start

```
/review 59         # Review PR #59
/review 10 12      # Review multiple PRs
/review all        # Review all open PRs by other devs
```

## Phase 0: Credentials & repository

Source the reviewer token and set up the GitHub CLI:

```bash
source .env.ops 2>/dev/null || { echo "ERROR: Missing .env.ops — create it from .env.ops.example and set REVIEWER_GITHUB_TOKEN"; exit 1; }
export GH_TOKEN="$REVIEWER_GITHUB_TOKEN"
```

Confirm we are authenticated as the reviewer bot (not the user's personal account):

```bash
echo "Authenticated as: $(gh api user --jq '.login')"
gh auth status
```

Determine the repo owner/name:

```bash
REPO=$(gh repo view --json nameWithOwner --jq '.nameWithOwner')
echo "Repository: $REPO"
```

## Phase 1: Understand the PR

For each PR under review, gather the full picture:

```bash
PR=$ARGUMENTS  # or iterate if "all" / multiple numbers
gh pr view $PR --json title,body,author,files,baseRefName,headRefName,commits
gh pr diff $PR
gh pr view $PR --comments --json comments
```

Map the change:
- **What bounded context(s)** does it touch? (user, curriculum, content, lesson, gamification, analytics)
- **What spec(s)** does it reference in the body? Extract every `docs/specs/*.md` link.
- **What type** is it? (feature, fix, chore, docs, infra)
- **Who authored** it? (only review PRs by other devs — never review your own)

Check out the branch for local inspection:

```bash
gh pr checkout $PR
```

## Phase 2: Spec verification

For every spec referenced in the PR body, verify every acceptance criterion:

```bash
# Extract spec paths from PR body
gh pr view $PR --json body | jq -r '.body' | grep -oP 'docs/specs/[^)\s]+' | sort -u
```

For each spec:
1. **Read the full spec** with the Read tool
2. **List every acceptance criterion** (checkboxes, numbered requirements, "must"/"shall" statements)
3. **Map each criterion to evidence** in the PR diff — a code change, a test, or a doc line
4. **Flag any criterion without coverage** — these block approval

Output a table:

| Criterion | Covered? | Evidence |
|-----------|----------|----------|
| Server starts and /actuator/health returns UP | ✅ | `PolyglotAiApplicationTests.contextLoads()` passes |
| DDD packages follow convention | ✅ | ArchUnit test in `DddBoundariesArchUnitTest` |
| ... | | |

If the PR changes behavior but references **no spec**, flag it — per global rule #1,
code changes need a spec.

## Phase 3: Static analysis

Run the full CI pipeline locally. Do NOT proceed past this phase if anything fails.

### Server changes

```bash
cd server
echo "=== Spotless (formatting) ===" && ./gradlew spotlessCheck
echo "=== Checkstyle (lint) ===" && ./gradlew checkstyleMain checkstyleTest
echo "=== ArchUnit (DDD boundaries) ===" && ./gradlew test --tests "*ArchUnit*"
echo "=== All tests ===" && ./gradlew test
```

### App changes

```bash
cd app
echo "=== Prettier ===" && pnpm format --check
echo "=== ESLint ===" && pnpm lint
echo "=== TypeScript ===" && pnpm tsc --noEmit
echo "=== Jest ===" && pnpm test
```

Capture all failures. Any failure in this phase = automatic **REQUEST_CHANGES**.

## Phase 4: DDD architecture check

Read `docs/architecture.md` and verify these rules against the diff:

### 4.1 Cross-context isolation

```bash
# For each context touched, check for imports from another context's domain/infrastructure
git diff HEAD~1 --name-only | while read f; do
  if echo "$f" | grep -q "src/main/java"; then
    imports=$(grep "^import com.polyglotai" "$f" 2>/dev/null || true)
    echo "$f: $imports"
  fi
done
```

Rules:
- No `import com.polyglotai.<contextA>.domain` inside `<contextB>` packages
- No `import com.polyglotai.<contextA>.infrastructure` inside `<contextB>` packages
- Cross-context communication only via application services or domain events

### 4.2 DTOs at boundaries

- Controllers/resolvers must return DTOs, never domain objects
- Request/response objects live in the `interfaces` layer

### 4.3 No shared kernel

- No `common`, `shared`, or `util` packages at the context level without a spec
- Shared types need an explicit spec and ADR

### 4.4 Package convention

- `com.polyglotai.<context>.<layer>` strictly followed
- No classes in the root `com.polyglotai` package except `PolyglotAiApplication`

### 4.5 Test placement

- Tests mirror the package structure they test
- ArchUnit tests in `src/test/java/com/polyglotai/archunit/`

## Phase 5: Documentation check

- **Wiki**: Architecture/workflow changes must mention wiki updates. Check the PR
  description for wiki links or update notes.
- **CLAUDE.md**: New directories, new commands, or convention changes → CLAUDE.md
  or subdirectory README must be updated.
- **ADR**: Architectural decisions must reference or create an ADR in
  `docs/architecture/adr/`.
- **PR template**: Verify the PR description fills out the summary and test plan.

## Phase 6: Manual testing

### 6.1 Server PRs

```bash
# Start databases
cd server && docker compose up -d

# Wait for healthy Postgres
until docker exec polyglot-postgres pg_isready -U polyglot 2>/dev/null; do
  echo "Waiting for Postgres..." && sleep 1
done

# Start server in background
cd server && ./gradlew bootRun &
SERVER_PID=$!

# Wait for server to be ready
until curl -s http://localhost:8080/actuator/health | grep -q UP; do
  echo "Waiting for server..." && sleep 2
done
```

Test every endpoint the PR adds or modifies. Derive curl commands from:
- Controller `@PostMapping`/`@GetMapping` paths
- DTO fields (use them as request bodies)
- Spec acceptance criteria that mention specific API behavior

```bash
# Example: health check
curl -s http://localhost:8080/actuator/health | jq .

# Example: test new endpoint (adapt path and body from the actual PR)
curl -s -X POST http://localhost:8080/api/... \
  -H "Content-Type: application/json" \
  -d '{...}' | jq .
```

Document each curl test with the command, expected result, and actual result.

### 6.2 App PRs

```bash
cd app && pnpm start &
```

For UI changes, test on Expo web:
1. Open the app in a browser (press `w` in Expo CLI)
2. Navigate through the changed flows
3. Test edge cases (empty states, errors, loading states)

For E2E flows with Maestro:
```bash
maestro test e2e/flows/
```

### 6.3 Infrastructure/CI PRs

For CI workflow changes, verify the workflow file syntax:
```bash
# Check workflow YAML is valid
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/server-ci.yml'))" && echo "YAML valid"
```

Push the branch and confirm CI triggers on the PR.

## Phase 7: Determine verdict

| Verdict | When to use |
|---------|-------------|
| **APPROVE** | All checks pass, all ACs verified with evidence, manual testing confirms behavior, docs complete |
| **COMMENT** | No blocking issues, but suggestions or questions worth raising (e.g., naming nits, optional refactors) |
| **REQUEST_CHANGES** | Test failures, missing AC coverage, DDD violations, broken manual testing, missing spec, incomplete docs, cross-context leaks |

The bar for APPROVE is high. When in doubt, COMMENT with specific asks.

## Phase 8: Post the review

### 8.1 Draft the review body

Read `.claude/skills/review/review-template.md` for the standard format. Fill it in
with findings from all phases. Be specific: reference file paths, line numbers,
commands run, and output received.

### 8.2 Post inline comments for specific issues

For each specific code issue (not general feedback), post an inline comment on the
relevant line:

```bash
gh api "repos/$REPO/pulls/$PR/comments" \
  -f body="**issue**: DTO `UserResponse` is missing the `email` field that the spec requires in section 3.2.

**suggestion**: Add `private String email;` to match the spec contract." \
  -f commit_id="$(gh pr view $PR --json commits --jq '.commits[-1].oid')" \
  -f path="server/src/main/java/com/polyglotai/user/interfaces/UserResponse.java" \
  -f line=15
```

### 8.3 Post the review with verdict

```bash
gh pr review $PR --approve   # or --comment, or --request-changes
```

If using `--request-changes` or `--comment`, include the full review body:

```bash
gh pr review $PR --request-changes --body "$(cat <<'EOF'
## Review Summary
...
EOF
)"
```

### 8.4 Verify the review is posted

```bash
gh pr view $PR --comments --json reviews,comments | jq .
```

## Phase 9: Cleanup

```bash
# Stop server
pkill -f "gradlew bootRun" 2>/dev/null || true

# Stop Expo
pkill -f "expo" 2>/dev/null || true

# Tear down databases (keep data if you'll review more PRs)
docker compose -f server/docker-compose.yml down 2>/dev/null || true

# Return to main branch
git checkout main 2>/dev/null || true
```

## Reference files

- **Review template**: `.claude/skills/review/review-template.md` — standard output format
- **Checklists**: `.claude/skills/review/checklists.md` — detailed per-dimension checklists
- **Testing guide**: `docs/testing.md` — how to run servers, tests, and automation
- **Architecture**: `docs/architecture.md` — DDD conventions and cross-context rules
- **Server conventions**: `server/README.md`
- **App conventions**: `app/README.md`
