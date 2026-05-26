# App Scaffold

## Purpose

Scaffold the React Native (Expo) mobile app with the agreed stack — Expo SDK 52+, TypeScript strict, Expo Router, NativeWind, Apollo Client (GraphQL), TanStack Query (REST), and Zustand (UI state). The scaffold MUST provide a working dev environment that passes all CI gates so every subsequent feature has a solid foundation.

## Requirements

### Requirement: Project MUST use Expo SDK 52+ with TypeScript strict mode

The app SHALL be initialised with Expo SDK 52 or later in managed workflow. TypeScript strict mode MUST be enabled in tsconfig.json and enforced in CI via `tsc --noEmit`.

#### Scenario: TypeScript strict mode is enabled
- GIVEN the app scaffold exists
- WHEN `pnpm tsc --noEmit` is run
- THEN it SHALL exit with code 0
- AND no implicit `any` types SHALL be present

### Requirement: Expo Router MUST provide file-based navigation

Navigation SHALL use Expo Router with file-based routing. The root layout MUST wrap the app in providers (Apollo, TanStack Query, Zustand) and an auth gate that redirects unauthenticated users to login.

#### Scenario: Unauthenticated user is redirected to login
- GIVEN the app is launched and no auth token exists
- WHEN the root layout renders
- THEN the user SHALL be redirected to the (auth) route group

### Requirement: Apollo Client MUST be configured for GraphQL with auth link

Apollo Client SHALL use an HttpLink pointing to the GraphQL endpoint from environment config. An auth link middleware MUST attach the access token to every request. On 401, the middleware SHALL trigger a token refresh via REST and retry the failed query exactly once.

#### Scenario: Expired token triggers silent refresh
- GIVEN a valid refresh token is stored
- WHEN a GraphQL request returns 401
- THEN the auth link SHALL call the refresh endpoint
- AND on success, retry the original query with the new access token
- AND on failure, clear tokens and redirect to login

### Requirement: TanStack Query MUST handle REST auth endpoints

TanStack Query SHALL be configured with a QueryClient for REST endpoints (login, register, refresh, file upload). Auth hooks (`useLogin`, `useRegister`, `useRefreshToken`, `useLogout`) MUST wrap these endpoints. Tokens SHALL be stored in expo-secure-store, never AsyncStorage.

#### Scenario: Login flow stores tokens securely
- GIVEN valid email and password
- WHEN `useLogin` is called
- THEN access and refresh tokens SHALL be stored in expo-secure-store
- AND auth state in Zustand SHALL update to authenticated

### Requirement: Zustand MUST own UI-only state

Zustand SHALL manage only UI state (auth status, active tab, onboarding step, theme preference). Server state MUST go through Apollo Client or TanStack Query, not Zustand.

#### Scenario: Auth state reflects token presence
- GIVEN tokens are stored in secure storage
- WHEN the app initialises
- THEN the Zustand auth store SHALL set `isAuthenticated: true`

### Requirement: GraphQL Code Generator MUST produce typed hooks

The project SHALL include a `codegen.ts` configuration that watches the server GraphQL schema and generates TypeScript types plus typed hooks for every query and mutation.

#### Scenario: Codegen runs without errors
- GIVEN the server GraphQL schema is accessible
- WHEN `pnpm codegen` is run
- THEN typed hooks SHALL be generated in `src/graphql/`

### Requirement: Project MUST follow the folder structure from architecture.md

The scaffold SHALL create the folder structure defined in `docs/architecture.md`: features (self-contained), components (shared UI), services (apollo + api), hooks, stores, lib. Features MUST NOT import from other features.

#### Scenario: Folder structure matches architecture
- GIVEN the scaffold is complete
- WHEN `ls app/src/` is run
- THEN `app/`, `features/`, `components/`, `services/`, `hooks/`, `stores/`, `lib/` SHALL all exist
