# ADR 0001: Monorepo with Domain-Driven Design

**Status**: accepted
**Date**: 2026-05-25

## Context

Polyglot AI is built by a 3-person team with heavy AI agent assistance. We need a
repository and architecture structure that:

1. Allows 3 humans + multiple AI agents to work in parallel without conflicts
2. Shares types, contracts, and tooling between the mobile app and backend where useful
3. Keeps the operational overhead low enough for a small team
4. Scales in complexity as the domain grows (curriculum generation, skill assessment,
   content matching are non-trivial domains)

## Decision

### Monorepo

A single private repository (`polyglot-ai`) containing the mobile app, backend server,
infrastructure code, and documentation.

**Alternatives considered:**

| Option | Verdict | Reason |
|--------|---------|--------|
| Polyrepo (separate repos per component) | Rejected | Cross-repo coordination overhead is too high for a 3-person team. Shared tooling, CI, and contracts become harder to manage. |
| Monorepo with build-system tooling (Nx, Turborepo) | Deferred | Worth revisiting when build times become painful. Not needed at project start. |

### Domain-Driven Design (strict bounded contexts)

The server is organised into bounded contexts, each with its own domain, application,
infrastructure, and interface layers. Contexts communicate only through application
services or domain events — never by importing each other's internals.

**Why DDD and not flat layered (controller → service → repository)?**

| Factor | DDD | Flat layered |
|--------|-----|-------------|
| AI agent safety | Clear boundaries mean agents can't accidentally couple contexts | Agents can import anything, leading to spaghetti |
| Domain complexity | Curriculum generation, skill assessment, content matching are genuinely complex domains | Treats everything as CRUD, pushing complexity into service classes that grow unboundedly |
| Refactoring | Contexts can be extracted into services later without rewriting | Requires a rewrite to extract |
| Initial cost | More boilerplate (entities, value objects, mappers, DTOs) | Faster to start |

The deciding factor is **AI agent safety**. With 3 humans and multiple AI agents working
simultaneously, clear, enforceable boundaries are worth the boilerplate cost.

**Why not microservices?**

A 3-person team cannot absorb the operational cost of distributed systems (network
reliability, deployment coordination, observability, data consistency). DDD in a
modular monolith gives us the same cognitive boundaries without the infrastructure tax.
If a context later outgrows the monolith, the DDD boundaries make extraction
straightforward.

**Enforcement**: ArchUnit tests verify layer and context boundaries at build time.
Violations fail the build.

**Pragmatic exception**: Simple CRUD contexts (`user` profiles, settings) may use a
simpler internal structure. The DDD boundary is the important part — what happens inside
the context is that context's business.

## Consequences

- Every feature spec must declare which bounded contexts it touches
- ArchUnit rules must be maintained alongside the code
- Agents must be told to read `docs/architecture.md` before writing server code
- Cross-context work requires explicit contracts (domain events or application service interfaces)
