# Polyglot AI

A gamified language-learning mobile app that builds daily lessons from content the user
loves (videos, music, films, books, podcasts). React Native (Expo) + Spring Boot 3.5.
Monorepo, 3-person team, heavy AI collaboration.

## Quick Reference

- **Vision & stack**: [`docs/project-manifesto.md`](docs/project-manifesto.md)
- **Architecture (DDD)**: [`docs/architecture.md`](docs/architecture.md)
- **Feature specs**: [`docs/specs/`](docs/specs/)
- **ADR records**: [`docs/architecture/adr/`](docs/architecture/adr/)
- **App conventions**: [`app/README.md`](app/README.md)
- **Server conventions**: [`server/README.md`](server/README.md)
- **Wiki**: [github.com/guilherme-andrade/polyglot-ai/wiki](https://github.com/guilherme-andrade/polyglot-ai/wiki)
- **Playbooks**: [`scripts/`](scripts/) — setup, verify, and other automation scripts

## Repository Map

```
polyglot-ai/
├── app/          React Native (Expo) — mobile app
├── server/       Java 21 + Spring Boot — backend
├── docs/         Specs, architecture, ADRs, manifesto
├── terraform/    Infra-as-code
├── ansible/      Server configuration
├── scripts/      Playbooks — setup, verify, run-server, etc.
├── .github/      CI/CD workflows
└── .claude/      Claude Code agent settings
```

## Global Rules

These apply to **any** task in this repo, regardless of context:

1. **Read the spec first.** Before touching code, read the relevant spec in
   `docs/specs/`. If no spec exists, one must be written and agreed upon first.
2. **Read the subdirectory README** before working in that part of the codebase.
   Start at `app/README.md` or `server/README.md` depending on what you're touching.
3. **Check `docs/architecture.md`** for DDD conventions, layering rules, and
   cross-context boundaries before writing server code.
4. **Keep the wiki updated.** If you change the architecture, deployment flow,
   workflow, or any convention documented in the wiki, update the relevant
   wiki page in the same PR.
5. **Use playbooks from `scripts/`.** Before manually running commands to set up,
   verify, start servers, or run tests, check `scripts/` for a playbook that
   does it. `ls scripts/` to see what's available. Playbooks are the single
   source of truth for how to operate the project — if a command isn't in a
   playbook, add one rather than pasting it inline.

## Quick Start

```bash
# First-time setup
./scripts/setup.sh

# Verify environment
./scripts/verify.sh

# Server
cd server && docker compose up -d && ./gradlew bootRun

# App
cd app && pnpm install && pnpm start
```
