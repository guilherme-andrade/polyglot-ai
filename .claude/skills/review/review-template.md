# Review output template

Use this exact structure for every review posted to GitHub. Consistency matters.

---

## Review Summary

**Verdict**: [APPROVE | COMMENT | REQUEST_CHANGES]

**PR**: #$ARGUMENTS — [title]
**Author**: [@author]
**Contexts touched**: [list bounded contexts or "none" for docs/infra]
**Specs referenced**: [list or "none"]

### What this PR does

[One paragraph summarising the change. What problem does it solve?]

### What I tested

- [ ] Static analysis (spotless, checkstyle, tsc, lint)
- [ ] All tests pass
- [ ] ArchUnit DDD boundaries intact
- [ ] Spec acceptance criteria verified
- [ ] Manual API testing (curl)
- [ ] Manual UI testing (Expo web / Maestro)
- [ ] Docker compose up + server starts
- [ ] Documentation links resolve

### Spec coverage

| Criterion | Status | Evidence |
|-----------|--------|----------|
| [criterion from spec] | [covered / missing / partial] | [test, curl, code reference] |

### Findings

#### Blocking (must fix before merge)

| # | File | Line | Issue |
|---|------|------|-------|
| 1 | `path/to/file` | 42 | [specific issue] |

#### Non-blocking (suggestions / nits)

| # | File | Line | Suggestion |
|---|------|------|------------|
| 1 | `path/to/file` | 15 | [suggestion] |

### Manual test evidence

```
$ curl -s http://localhost:8080/actuator/health | jq .
{
  "status": "UP"
}

$ curl -s -X POST http://localhost:8080/api/... \
  -H "Content-Type: application/json" \
  -d '{...}' | jq .
{
  ...
}
```

### Commands run

```
./gradlew spotlessCheck    # PASSED
./gradlew checkstyleMain checkstyleTest  # PASSED
./gradlew test --tests "*ArchUnit*"  # PASSED
./gradlew test             # PASSED (42 tests)
```
