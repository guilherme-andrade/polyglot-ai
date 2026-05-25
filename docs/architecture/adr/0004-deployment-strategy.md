# ADR 0004: Deployment & Infrastructure Strategy

**Status**: proposed
**Date**: 2026-05-25

## Context

We need a deployment strategy for a Spring Boot backend and a React Native mobile app,
managed by a 3-person team. The infrastructure must be reproducible (IaC), cost-effective
for early-stage usage, and simple enough that the team can operate it without a dedicated
DevOps person.

The cloud provider is still TBD, so this ADR defines the **strategy and criteria**
rather than the specific implementation.

## Decisions

### Backend: Containerized, single-server to start

The Spring Boot server runs as a Docker container on a single cloud VM, with managed
database services.

```
┌─────────────────────────────────┐
│  Cloud VM (Docker)               │
│  ┌───────────────────────────┐  │
│  │  polyglot-server:latest   │  │
│  │  (Spring Boot fat JAR)    │  │
│  └───────────────────────────┘  │
│  ┌───────────────────────────┐  │
│  │  Nginx (reverse proxy)    │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
          │
    ┌─────┴─────┐
    │           │
┌───┴───┐  ┌───┴───┐
│  RDS  │  │  DocDB│
│  (PG) │  │(Mongo)│
└───────┘  └───────┘
```

**Why not Kubernetes?** For a single backend service, Kubernetes adds operational
complexity with no benefit. If we later split into multiple services, Docker Compose
on a larger VM is the next step before considering k8s.

**Why not a PaaS (Heroku, Railway, Fly.io)?**
PaaS is simpler but the project already commits to Terraform + Ansible for learning
and control. A PaaS would make the IaC investment redundant. However, if the team
finds operational overhead is slowing development, switching to a PaaS is cheap.

### Database: Managed services

PostgreSQL and MongoDB run as managed cloud services, not self-hosted on the VM.

**Rationale**: Managed databases handle backups, patching, and high availability. The
cost difference is small at this scale, and a 3-person team should not be doing DBA work.

### Environments & Deployment Pipeline

See **[ADR 0005](0005-environments-and-deployment-pipeline.md)** for the full
environments and deployment trigger specification. In summary:

| Environment | Trigger |
|-------------|---------|
| `local` | Docker Compose (per-developer) |
| `staging` | Automatic on push to `main` |
| `prod` | Manual via `workflow_dispatch`, contributors only |

Mobile testing is handled via EAS preview builds + OTA updates pointing to the
staging backend.

### Infrastructure as Code

```
terraform/
├── environments/
│   ├── dev/
│   ├── staging/
│   └── prod/
├── modules/
│   ├── compute/          # VM / container host
│   ├── database/         # PostgreSQL (RDS) + MongoDB (DocumentDB)
│   ├── networking/       # VPC, subnets, security groups
│   └── dns/              # DNS + SSL certs
└── main.tf
```

### Ansible

Ansible configures the VM after Terraform provisions it:
- Docker installation
- JDK 21 installation (if not using Docker)
- Nginx configuration (TLS termination, reverse proxy)
- Application deployment (pull Docker image, restart container)
- Firewall rules
- Log forwarding to cloud provider's log service

**Why Ansible and not just a cloud-init script or Packer?**
The team chose Ansible early. For a single VM, cloud-init would be simpler. Ansible's
value grows if we add more servers or need consistent configuration across environments.
The team acknowledges this is slightly over-engineered for day one, but the learning
investment is intentional.

### DNS & SSL

- Domain: TBD (e.g. `polyglot.ai`)
- API subdomain: `api.polyglot.ai` → Nginx → Spring Boot
- SSL: Let's Encrypt via certbot, automated renewal via cron

## Cloud Provider Selection Criteria

Decision deferred to a separate ADR. Evaluation criteria (priority order):

1. **Managed PostgreSQL + pgvector support** — pgvector is available on RDS (AWS) and
   Cloud SQL (GCP). Verify pgvector extension availability and performance.
2. **Cost at low scale** — free tier and low-traffic pricing matter more than
   enterprise features.
3. **Team experience** — if someone already knows AWS or GCP well, that tips the scale.
4. **GPU availability for future ML workloads** — curriculum generation and content
   matching may need GPU inference. Both AWS (SageMaker, Bedrock) and GCP (Vertex AI)
   offer this.
5. **Managed MongoDB** — AWS DocumentDB (MongoDB-compatible) vs GCP MongoDB Atlas
   integration.

**Lean recommendation**: AWS. The free tier covers the first year of t3.micro RDS +
EC2, DocumentDB is mature, and the team is more likely to have AWS experience. But
this is not decided — the team should write ADR 0005 once the evaluation is complete.

## Consequences

- Docker is a prerequisite for local development and deployment
- Managed databases add ~$30-50/month at the low end. Acceptable for a project that
  expects to generate revenue
- Manual prod promotion creates a release bottleneck. Mitigated by EAS Update OTA
  for the mobile app (JS changes skip app review)
- Single VM is a single point of failure. Acceptable for pre-revenue stage. Add
  redundancy in ADR when we have paying users
- Terraform + Ansible toolchain requires both tools to be learned by the team. The
  operational overhead is intentional but real
