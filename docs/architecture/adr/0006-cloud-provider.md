# ADR 0006: Cloud Provider — Hetzner

**Status**: accepted
**Date**: 2026-05-26

## Context

We need a cloud provider to host PostgreSQL, MongoDB, and the Spring Boot server.
The team is 3 developers, pre-revenue, with an initial European user base.

## Decision

**Hetzner** — best cost profile for a pre-revenue 3-person team. European DCs keep
latency low for the initial EU user base. Simpler infrastructure (VMs + Docker)
matches our scale; we can migrate to a managed cloud when revenue justifies it.

### Alternatives considered

| Criterion | Hetzner | AWS | GCP |
|-----------|---------|-----|-----|
| Managed PostgreSQL + pgvector | Self-host on VM | RDS + pgvector | Cloud SQL + pgvector |
| Managed MongoDB | Self-host on VM | DocumentDB (not MongoDB-compatible) | Self-host or Atlas |
| Container runtime | Self-host on VM (Docker) | ECS Fargate / EKS | Cloud Run / GKE |
| Cost at pre-revenue scale | ~€5–20/mo per VM | Moderate–high | Moderate |
| European DCs | Nuremberg, Falkenstein, Helsinki | Frankfurt, Stockholm | Multiple |
| GitHub Actions integration | SSH-based deploy | OIDC, full SDK | OIDC, full SDK |
| CDN for mobile content | Use BunnyCDN / Cloudflare | CloudFront | Cloud CDN |
| Complexity | Low | High | Medium |

### Why not AWS

Cost and complexity at small scale is disproportionate. RDS for a small instance
alone runs ~$30–50/mo. Overkill for pre-revenue.

### Why not GCP

Better developer experience than AWS but similar cost concerns. Cloud SQL minimum
~$25/mo. No killer feature over Hetzner for current needs.

## Consequences

### We manage PostgreSQL and MongoDB ourselves

Backups, monitoring, and upgrades are our responsibility on VMs. Acceptable trade
for pre-revenue cost savings.

### CDN is a separate provider

BunnyCDN or Cloudflare. This is neutral — neither AWS nor GCP CDN would be cheaper.

### No managed Kubernetes

We deploy Docker containers on VMs (simpler for our scale). If we later need k8s,
we can migrate to a managed offering or self-host.

### Deployment is SSH-based

Ansible pulls new images, restarts containers. Less elegant than Cloud Run / Fargate,
but fully automatable via GitHub Actions.

## Migration path

If we outgrow Hetzner:
- PostgreSQL → RDS / Cloud SQL (standard pg_dump migration)
- MongoDB → Atlas (managed, cloud-agnostic)
- Container hosting → ECS Fargate / Cloud Run / GKE

No lock-in: Hetzner uses standard VMs with standard Linux.
