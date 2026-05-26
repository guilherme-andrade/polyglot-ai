# Review checklists

Detailed per-dimension checklists. Work through each that applies to the PR.

---

## Spec-driven development

- [ ] PR description links at least one spec in `docs/specs/`
- [ ] Every acceptance criterion in the spec has evidence in the PR
- [ ] No behaviour is implemented that isn't in the spec
- [ ] Spec is under `docs/specs/<feature-name>.md` (not ADR, not a comment)
- [ ] If no spec exists and code changes behaviour, flag as blocking

## DDD architecture

### Package structure

- [ ] All code is under `com.polyglotai.<context>.<layer>`
- [ ] Six bounded contexts: user, curriculum, content, lesson, gamification, analytics
- [ ] Four layers per context: domain, application, infrastructure, interfaces
- [ ] No classes in root `com.polyglotai` except `PolyglotAiApplication`
- [ ] `bootstrap` package only for app-wide config (security, GraphQL wiring)

### Domain layer (`domain/`)

- [ ] Zero framework dependencies (no `@Entity`, `@Repository`, `@Service`)
- [ ] Entities, value objects, domain services, repository interfaces only
- [ ] Repository interfaces are plain Java interfaces (no `@Repository` in domain)
- [ ] Domain services are stateless and named for domain concepts
- [ ] Value objects are immutable (`record` or final class with no setters)

### Application layer (`application/`)

- [ ] Application services depend on domain interfaces, not infrastructure
- [ ] Use-cases are focused: one public method per service
- [ ] DTOs for input/output (never domain objects cross this boundary)
- [ ] Transaction boundaries are here (`@Transactional` on application services)

### Infrastructure layer (`infrastructure/`)

- [ ] JPA entities with `@Entity`, `@Table`, `@Id` (NOT in domain)
- [ ] Repository implementations that implement domain interfaces
- [ ] No business logic in repository implementations — delegation only
- [ ] Flyway migrations follow naming convention (`V{n}__description.sql`)

### Interfaces layer (`interfaces/`)

- [ ] Controllers/resolvers are thin — delegate to application services
- [ ] Request/Response DTOs are in this layer (not domain objects)
- [ ] Validation annotations on DTOs (`@Valid`, `@NotBlank`, etc.)
- [ ] Mappers between DTOs and domain objects are in this layer
- [ ] Error responses follow a consistent structure

### Cross-context rules

- [ ] No `import com.polyglotai.<contextA>.domain` inside `<contextB>` packages
- [ ] No `import com.polyglotai.<contextA>.infrastructure` inside `<contextB>` packages
- [ ] Cross-context calls go through application services only
- [ ] Domain events used for async cross-context communication
- [ ] No `common`, `shared`, or `util` packages without a spec + ADR

## Code quality

### Java (server)

- [ ] No public constructors where static factories make more sense
- [ ] No raw types or unchecked casts
- [ ] No `System.out.println` — use SLF4J
- [ ] No `e.printStackTrace()` — proper logging
- [ ] No `catch (Exception e)` without a good reason
- [ ] Builder pattern for objects with >3 constructor params
- [ ] `record` used for DTOs and value objects
- [ ] `Optional` for nullable returns (not for fields or params)
- [ ] No Lombok (use Java records and manual builders)
- [ ] Virtual threads enabled (check `application.yml`)

### TypeScript (app)

- [ ] Strict mode enabled (no implicit any)
- [ ] Features are self-contained — no imports from other feature folders
- [ ] API calls go through TanStack Query hooks
- [ ] No raw fetch/axios in components
- [ ] Zustand stores for UI state, TanStack Query for server state
- [ ] Components use NativeWind (Tailwind) for styling
- [ ] Expo Router file-based routing conventions followed

### General

- [ ] No commented-out code
- [ ] No TODO comments without an issue number (`TODO(#123)`)
- [ ] No hardcoded credentials, tokens, or secrets
- [ ] No hardcoded IP addresses or hostnames
- [ ] Error messages are user-facing and actionable
- [ ] Logging at appropriate levels (debug for dev, info for key events, warn/error for problems)

## Testing

- [ ] Unit tests exist for domain logic
- [ ] Integration tests exist for repository implementations
- [ ] ArchUnit tests verify DDD boundaries
- [ ] API tests (curl or WebTestClient) for new endpoints
- [ ] Test class names follow convention (`*Test` for unit, `*ArchUnitTest` for ArchUnit)
- [ ] Tests use AssertJ (not Hamcrest or plain JUnit assertions)
- [ ] Testcontainers for DB-dependent tests (not H2)
- [ ] Maestro flows for new app screens

## Documentation

- [ ] Architecture/workflow changes → wiki update referenced in PR
- [ ] New CLI commands or scripts → CLAUDE.md updated
- [ ] New bounded context → `docs/architecture.md` updated
- [ ] New convention → subdirectory README updated
- [ ] ADR created for architectural decisions
- [ ] PR template fields all filled out (summary, test plan)
- [ ] Links in the PR description are resolvable

## Infrastructure

- [ ] Docker Compose changes tested with `docker compose up -d`
- [ ] CI workflow changes: YAML valid, all jobs defined, triggers correct
- [ ] New environment variables documented in `.env.example` or equivalent
- [ ] No secrets in Dockerfiles or compose files
- [ ] Multi-stage Docker builds used where applicable
- [ ] Non-root user in Dockerfiles
