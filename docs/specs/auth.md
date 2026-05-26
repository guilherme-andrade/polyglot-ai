# Spec: Authentication (Account Creation, Login, Token Refresh)

**Status**: draft
**Bounded contexts**: user, app
**Issues**: [#34](https://github.com/guilherme-andrade/polyglot-ai/issues/34), [#35](https://github.com/guilherme-andrade/polyglot-ai/issues/35), [#36](https://github.com/guilherme-andrade/polyglot-ai/issues/36)

## Overview

Full authentication flow: email + password account creation, JWT-based login/logout,
and silent token refresh for persistent sessions.

## Create account (#34)

### UI
- Email + password + confirm password form
- Validation: email format, password strength (min 8 chars, 1 letter, 1 number), passwords match
- On success: account created in pending state, verification email sent, redirect to login

### Server
- `POST /api/auth/register` — creates user with encrypted password (BCrypt)
- Email must be unique (409 Conflict if duplicate)
- Email verification token generated and stored
- Welcome email sent (stub in dev)

### Error handling
- Duplicate email: "An account with this email already exists"
- Weak password: "Password must be at least 8 characters with a letter and a number"
- Network failure: "Unable to connect. Check your internet connection."

## Login (#35)

### UI
- Email + password form
- "Forgot password?" link (deferred to post-v1)
- On success: tokens stored in expo-secure-store, redirect to app shell
- Invalid credentials: generic error ("Invalid email or password") — don't leak which is wrong

### Server
- `POST /api/auth/login` — validates credentials, returns access + refresh tokens
- Access token: short-lived (15 min), signed JWT
- Refresh token: long-lived (30 days), opaque, stored hashed in DB
- Rate limiting: 5 failed attempts per 15 min per IP (429 if exceeded)

### Logout
- App: clear tokens from expo-secure-store, clear Apollo cache, redirect to login
- Server: `POST /api/auth/logout` — invalidates refresh token

## Silent token refresh (#36)

### Flow
1. Apollo AuthLink attaches access token to every GraphQL request
2. On 401 response, AuthLink calls `POST /api/auth/refresh` with refresh token
3. On success: new access + refresh tokens returned, old refresh token invalidated (rotation)
4. Failed request retried with new access token (exactly once — no infinite loops)
5. On refresh failure: clear tokens, redirect to login

### TanStack Query side
- REST calls use the same token store
- `useRefreshToken` hook available for manual refresh
- Auth state in Zustand: `isAuthenticated`, `isLoading`, `currentUser`

### Server
- `POST /api/auth/refresh` — validates refresh token, issues new pair
- Refresh token rotation: each refresh returns a new refresh token and invalidates the old one
- Detect refresh token reuse (stolen token) → invalidate all tokens for that user, force re-login

## Token storage

- expo-secure-store for access + refresh tokens (encrypted on device)
- Never store tokens in AsyncStorage (unencrypted)

## Acceptance criteria

- [ ] Create account form with validation (#34)
- [ ] Server: `/api/auth/register` creates user, returns success
- [ ] Duplicate email and weak password handled with clear errors
- [ ] Login form with email + password (#35)
- [ ] Server: `/api/auth/login` returns access + refresh tokens
- [ ] Rate limiting on failed login attempts
- [ ] Logout clears tokens and redirects (#35)
- [ ] Apollo AuthLink attaches token and handles 401 with refresh (#36)
- [ ] Refresh token rotation implemented server-side (#36)
- [ ] Stolen refresh token detection invalidates all user tokens (#36)
- [ ] Tokens stored in expo-secure-store, never AsyncStorage
- [ ] Auth state in Zustand store

## Out of scope

- Social login (Google, Apple) — post-v1
- "Forgot password" flow — post-v1
- Email verification (stub the email send, verify the token flow works)
- Biometric unlock to access tokens
