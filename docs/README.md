# Polyglot AI — Documentation

## Structure

```
docs/
├── specs/                    # OpenSpec feature specifications
│   └── <feature-name>.md     # One spec file per feature
├── architecture/             # Architecture Decision Records, diagrams
│   └── adr/                  # ADRs (numbered, one per decision)
└── README.md                 # This file
```

## OpenSpec Format

Every feature spec in `specs/` follows this template:

```markdown
# <Feature Name>

## Summary
2-3 sentences describing the feature.

## Motivation
Why this matters to users / business.

## Bounded Contexts Touched
- `context-a`
- `context-b`

## API Contracts
GraphQL schema changes, REST endpoints, event schemas.

## Data Model Changes
New tables, collections, indexes, migrations.

## UI Behaviour
Screen flows, state handling (loading, empty, error, edge cases).

## Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2
```

## ADR Format

Architecture Decision Records in `architecture/adr/` follow this naming convention:
`NNNN-slug.md` (e.g. `0001-use-postgresql-for-primary-store.md`).

Each ADR covers:
- **Status**: proposed | accepted | deprecated | superseded
- **Context**: What problem are we solving?
- **Decision**: What did we choose and why?
- **Consequences**: What are the trade-offs?
