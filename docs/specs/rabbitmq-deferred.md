# Spec: RabbitMQ Deferred

**Status**: draft
**Bounded contexts**: server (infrastructure)
**Issue**: [#57](https://github.com/guilherme-andrade/polyglot-ai/issues/57)

## Overview

Align `server/README.md` with the manifesto on RabbitMQ's status. The manifesto
says "RabbitMQ (if async work needed)" — i.e. deferred. The scaffold ships no
AMQP dependency, no compose service, and no broker config. Update the README to
reflect this accurately.

## Decision

Defer. No async workloads exist yet that justify the operational cost of a message
broker.

## What to do

1. Update `server/README.md` stack table: `RabbitMQ *(deferred — when async work needed)*`
2. No code changes, no dependency additions, no docker-compose changes

## When to reconsider

Open a new ADR when any of these arise:
- Multi-step lesson generation (request → queue → generate → notify)
- Batch content processing
- Push notification fan-out at scale
- Cross-context async workflows needing a durable bus

## Acceptance criteria

- [ ] `server/README.md` says RabbitMQ is deferred
- [ ] Manifesto and README agree on RabbitMQ's status

## Out of scope

- Wiring RabbitMQ (deferred until an actual async use case has a spec)
