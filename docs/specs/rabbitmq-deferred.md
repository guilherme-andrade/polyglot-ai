# RabbitMQ Deferred

## Purpose

Align `server/README.md` with the manifesto on RabbitMQ's status. The manifesto states "RabbitMQ (if async work needed)" — i.e. deferred. The scaffold ships no AMQP dependency, no docker compose service, and no broker configuration. The README MUST accurately reflect that RabbitMQ is deferred, not active.

## Requirements

### Requirement: README MUST match the manifesto on RabbitMQ status

`server/README.md` SHALL list RabbitMQ as deferred with a note: "RabbitMQ (deferred — when async work needed)." No code changes, no dependency additions, and no docker compose changes SHALL be made.

#### Scenario: README shows RabbitMQ as deferred
- GIVEN a contributor reads the server stack table
- WHEN they look at the messaging row
- THEN it SHALL say "RabbitMQ (deferred — when async work needed)"
- AND SHALL NOT imply RabbitMQ is wired and running

### Requirement: No AMQP dependency SHALL be added until an async use case is spec'd

The `spring-boot-starter-amqp` dependency SHALL NOT be added to `build.gradle.kts`. A RabbitMQ service SHALL NOT be added to `docker-compose.yml`. These SHALL only be added when a concrete async workload has a spec and ADR.

#### Scenario: Deferred status remains until a spec exists
- GIVEN no spec requires async messaging
- WHEN the project is built
- THEN no AMQP classes SHALL be on the classpath
- AND `docker compose up` SHALL start only PostgreSQL and MongoDB
