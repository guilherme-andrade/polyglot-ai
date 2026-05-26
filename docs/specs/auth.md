# Authentication

## Purpose

Full authentication flow: email + password account creation, JWT-based login with token management, and silent token refresh for persistent sessions. Tokens MUST be stored in expo-secure-store, never unencrypted storage. The server SHALL rate-limit failed attempts and rotate refresh tokens on each use for security.

## Requirements

### Requirement: User MUST create account with email and password

The app SHALL present an email + password + confirm password registration form. Server-side validation MUST reject invalid emails, weak passwords (fewer than 8 characters or missing a letter/number), and duplicate emails. On success, the account SHALL be created with BCrypt-hashed password and a verification email sent.

#### Scenario: Duplicate email is rejected
- GIVEN a user account with email `test@polyglot.ai` exists
- WHEN registration is attempted with the same email
- THEN the server SHALL return 409 Conflict
- AND the message SHALL say "An account with this email already exists"

#### Scenario: Weak password is rejected
- GIVEN the registration form is submitted
- WHEN the password is "12345678" (no letter)
- THEN the server SHALL return 400 Bad Request
- AND the message SHALL indicate password strength requirements

### Requirement: User MUST log in with email and password to receive JWT tokens

The login form SHALL accept email and password. On valid credentials, the server SHALL return a short-lived access token (15 min) and a long-lived refresh token (30 days). Invalid credentials SHALL produce a generic error that does not reveal which field is wrong.

#### Scenario: Valid credentials return tokens
- GIVEN a verified user exists
- WHEN login is submitted with correct email and password
- THEN the server SHALL return access and refresh tokens
- AND the app SHALL store both tokens in expo-secure-store

#### Scenario: Five failed attempts triggers rate limiting
- GIVEN a user enters wrong credentials
- WHEN 5 failed attempts occur from the same IP within 15 minutes
- THEN the server SHALL return 429 Too Many Requests

### Requirement: Access token MUST be attached to every request via Apollo AuthLink

An Apollo auth link middleware SHALL attach the access token to every GraphQL request. TanStack Query REST calls SHALL use the same token from expo-secure-store. On 401, the auth link SHALL attempt a silent refresh exactly once before redirecting to login.

#### Scenario: Expired token triggers silent refresh
- GIVEN an expired access token and a valid refresh token
- WHEN a GraphQL request returns 401
- THEN the auth link SHALL call `POST /api/auth/refresh`
- AND on success, retry the failed request with the new access token
- AND the old refresh token SHALL be invalidated

#### Scenario: Refresh token reuse triggers full re-login
- GIVEN a refresh token that was already used (potential theft)
- WHEN `POST /api/auth/refresh` is called with that token
- THEN the server SHALL invalidate ALL tokens for that user
- AND the server SHALL return 401

### Requirement: Logout MUST clear all tokens

On logout, the app SHALL clear tokens from expo-secure-store, clear the Apollo Client cache, and redirect to the login screen. The server SHALL invalidate the refresh token.

#### Scenario: Logout removes local state
- GIVEN a user is authenticated
- WHEN logout is triggered
- THEN expo-secure-store SHALL be cleared of tokens
- AND the Zustand auth store SHALL set `isAuthenticated: false`

### Requirement: Auth state MUST live in a Zustand store

A Zustand store SHALL track `isAuthenticated`, `isLoading`, and `currentUser`. This store SHALL NOT hold tokens — tokens SHALL only exist in expo-secure-store.

#### Scenario: App initialisation checks for existing tokens
- GIVEN valid tokens exist in expo-secure-store
- WHEN the app starts
- THEN the Zustand store SHALL set `isAuthenticated: true`
- AND `currentUser` SHALL be populated
