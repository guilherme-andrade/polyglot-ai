# Architecture

Domain-Driven Design with strict bounded contexts.

## Repository Structure

```
polyglot-ai/
├── .claude/                  # Claude Code agent config
│   └── settings.json
├── .github/                  # GitHub Actions workflows + issue templates
│   └── workflows/
├── app/                      # React Native (Expo) mobile app
│   ├── README.md
│   └── ...
├── server/                   # Spring Boot backend
│   ├── README.md
│   └── ...
├── docs/                     # OpenSpec specifications + architecture docs
│   ├── specs/                # Feature specs (one .md per feature)
│   └── architecture/         # ADRs, diagrams
├── terraform/                # Infra-as-code (Terraform)
├── ansible/                  # Configuration management (Ansible)
├── CLAUDE.md                 # Project router for humans and AI agents
├── README.md                 # Public-facing project overview
└── .gitignore
```

## Server: DDD Bounded Contexts

The server codebase is organised around **bounded contexts** — each context owns its
models, repository interfaces, application services, and API surface.

### Package Convention

```
com.polyglotai.<context>.<layer>
```

### Contexts (illustrative — refined as specs are written)

| Context | Responsibility |
|---------|---------------|
| `user` | Accounts, profiles, preferences |
| `curriculum` | Skill assessment, curriculum generation, progress tracking |
| `content` | Content catalog, metadata, search |
| `lesson` | Lesson generation, exercise templates, scheduling |
| `gamification` | XP, streaks, leaderboards, achievements |
| `analytics` | Usage metrics, learning analytics |

### Layers (within each context)

| Layer | Purpose | Framework deps |
|-------|---------|---------------|
| `domain` | Entities, value objects, domain services, repository interfaces | Zero |
| `application` | Use-cases / application services | Interfaces only |
| `infrastructure` | Repository impls, JPA entities, API clients, message listeners | Spring, JPA, etc. |
| `interfaces` | REST controllers, GraphQL resolvers, DTOs, mappers | Spring Web/GraphQL |

### Cross-Context Rules

1. **One context per agent at a time.** If you're touching files in `com.polyglotai.curriculum`,
   don't also edit files in `com.polyglotai.user` in the same PR unless there's an
   explicit cross-context contract.
2. **Contexts communicate only through application services or domain events.** Never
   import a repository from another context directly. Never reach into another context's
   domain package.
3. **DTOs at the boundary.** Every interface layer exposes DTOs. Domain objects never
   leak into controllers or resolvers.
4. **Follow OpenSpec.** Before implementing a feature, there must be a spec in
   `/docs/specs/<feature-name>.md`. The spec defines the contracts agents must follow.
5. **Tests live in the context they test.** Follow the same package structure.
6. **No shared kernel unless explicitly spec'd.** Don't create a `common` or `shared`
   package without first adding it to a spec and getting agreement.

## App: Feature-Based Structure

```
app/
├── src/
│   ├── app/                  # Expo Router file-based routes
│   ├── features/             # Feature modules (screen-level components + logic)
│   │   ├── lesson/
│   │   ├── profile/
│   │   ├── curriculum/
│   │   └── ...
│   ├── components/           # Shared UI components (design system)
│   ├── hooks/                # Shared hooks
│   ├── services/             # API client, auth, analytics
│   ├── stores/               # Zustand stores
│   └── lib/                  # Utilities, constants, types
```

### App Rules

1. **Features are self-contained.** A feature folder contains its screens, components,
   hooks, and types. It may import from `components/` and `services/`, but never from
   another feature.
2. **API calls go through React Query hooks.** No raw fetch/axios in components.
3. **File-based routing via Expo Router.** Each route file exports a default component.
