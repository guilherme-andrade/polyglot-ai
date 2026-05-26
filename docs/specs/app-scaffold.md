# Spec: React Native App Scaffold

**Status**: draft
**Bounded contexts**: app (cross-cutting)
**Issue**: [#23](https://github.com/guilherme-andrade/polyglot-ai/issues/23)
**Depends on**: ADR-0002 (Tech Stack), ADR-0008 (Cross-Context Contracts)

## Overview

Scaffold the React Native (Expo) mobile app with the agreed stack: Expo SDK 52+,
TypeScript strict, Expo Router, NativeWind, Apollo Client (GraphQL), TanStack
Query (REST), Zustand (UI state).

## Stack

| Concern | Choice |
|---------|--------|
| Runtime | Expo SDK 52+ managed workflow, TypeScript strict |
| Navigation | Expo Router (file-based) |
| Styling | NativeWind (Tailwind for RN) |
| GraphQL | Apollo Client (queries, mutations, cache, optimistic updates) |
| REST | TanStack Query (auth endpoints, file uploads) |
| UI state | Zustand (tabs, onboarding step, theme — nothing from server) |
| Codegen | GraphQL Code Generator → typed hooks |
| Testing | Jest + React Native Testing Library |

## Folder structure

```
app/
├── src/
│   ├── app/                  # Expo Router file-based routes
│   │   ├── _layout.tsx        # Root layout (providers, auth gate)
│   │   ├── index.tsx          # Entry redirect
│   │   ├── (auth)/            # Login, register screens
│   │   └── (tabs)/            # Main tab navigator
│   │       ├── _layout.tsx    # Tab bar layout
│   │       ├── learn/         # Daily lesson
│   │       ├── progress/      # Curriculum, stats
│   │       └── profile/       # Settings, profile
│   ├── features/              # Feature modules (self-contained)
│   ├── components/            # Shared UI library
│   ├── services/
│   │   ├── apollo/            # Apollo Client setup, links, cache
│   │   └── api/               # REST client + TanStack Query hooks
│   ├── hooks/                 # Shared hooks
│   ├── stores/                # Zustand stores
│   ├── lib/                   # Utilities, constants, types
│   └── graphql/               # Auto-generated types + hooks (do not edit)
├── codegen.ts                 # GraphQL Code Generator config
├── app.json                   # Expo config
├── tsconfig.json
└── package.json
```

## Apollo Client setup

- HttpLink → GraphQL endpoint (staging/prod from env)
- AuthLink middleware: attaches access token; on 401, triggers token refresh via REST, retries
- InMemoryCache with type policies per bounded context entity
- Persisted cache (AsyncStorage wrapper) — survives app restart
- Error link: logs, surfaces network errors to global toast

## REST / TanStack Query setup

- QueryClient with default stale time and retry config
- Auth hooks: `useLogin`, `useRegister`, `useRefreshToken`, `useLogout`
- File upload hook: `useUploadFile`
- expo-secure-store for access + refresh tokens
- Auth state in Zustand store: `currentUser`, `isAuthenticated`, `isLoading`

## Environment config

- `.env.example` with `API_URL`, `GRAPHQL_URL`
- EAS Build picks env vars per profile

## Acceptance criteria

- [ ] `pnpm install && pnpm start` launches Expo dev server
- [ ] `pnpm test` runs and passes (at least one smoke test)
- [ ] `pnpm lint` and `pnpm format` run clean
- [ ] `tsc --noEmit` passes with strict mode
- [ ] Apollo Client wired with auth link and persisted cache
- [ ] TanStack Query wired with auth hooks
- [ ] Zustand store for auth state
- [ ] GraphQL Codegen configured and generates typed hooks
- [ ] Folder structure follows `docs/architecture.md`

## Out of scope

- Actual screen implementations (those are feature specs)
- EAS Build configuration (separate spec: `eas-build.md`)
