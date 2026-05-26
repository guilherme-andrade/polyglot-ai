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
- **Project tracker**: [GitHub Issues](https://github.com/guilherme-andrade/polyglot-ai/issues) — source of truth for roadmap, task status, and priorities
- **Wiki**: [github.com/guilherme-andrade/polyglot-ai/wiki](https://github.com/guilherme-andrade/polyglot-ai/wiki)
- **Testing guide**: [`docs/testing.md`](docs/testing.md)
- **Review workflow**: `/review` skill (`.claude/skills/review/`) — PR reviews with manual testing

## Repository Map

```
polyglot-ai/
├── app/          React Native (Expo) — mobile app
├── server/       Java 21 + Spring Boot — backend
├── docs/         Specs, architecture, ADRs, manifesto
├── terraform/    Infra-as-code
├── ansible/      Server configuration
├── .github/      CI/CD workflows
├── .claude/      Claude Code agent settings, skills, and MCP config
│   ├── skills/   Custom slash commands (/review, etc.)
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
5. **Always test your work. Work is not ready until it's been tested.**
   Read [`docs/testing.md`](docs/testing.md) for how to run servers, test locally,
   and automate tests. Include test evidence in your PR description — curl commands,
   CI results, screenshots, or Playwright traces. Every acceptance criteria in the
   spec must be verified.
6. **Check GitHub Issues for project status.** When asked about the state of the
   project, roadmap, priorities, or "what's next", consult
   [GitHub Issues](https://github.com/guilherme-andrade/polyglot-ai/issues) first.
   Issues are the canonical tracker — labels map to bounded contexts and layers.
   Cross-reference with open branches for work-in-flight.

## Quick Start

```bash
# Server
cd server && ./gradlew bootRun

# App
cd app && pnpm start
```
