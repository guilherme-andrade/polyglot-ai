# Spec: i18n Infrastructure

**Status**: draft
**Bounded contexts**: app (cross-cutting)
**Issue**: [#30](https://github.com/guilherme-andrade/polyglot-ai/issues/30)

## Overview

Internationalization scaffolding from day one so strings are never hardcoded.
Retrofitting i18n after building screens is expensive — set up now, add
translations progressively.

## Setup

- i18next + react-i18next
- Namespace structure: `common`, `lesson`, `profile`, `onboarding`, `errors`
- Fallback language: English (`en`)
- Type-safe translation keys via TypeScript (autocomplete on `t()` calls)
- String extraction: script to find missing keys (static analysis)
- RTL support: i18next language detector sets `dir` attribute (deferred but not blocked)

## Namespace structure

| Namespace | Scope |
|-----------|-------|
| `common` | Shared UI: buttons, labels, errors, navigation |
| `lesson` | Exercise prompts, feedback, lesson screens |
| `profile` | Settings, preferences, account |
| `onboarding` | Welcome flow, language selection, interest picker |
| `errors` | API errors, validation messages |

## File layout

```
app/src/i18n/
├── index.ts          # i18next init, language detector
├── resources/
│   ├── en/
│   │   ├── common.json
│   │   ├── lesson.json
│   │   ├── profile.json
│   │   ├── onboarding.json
│   │   └── errors.json
│   └── es/           # Example target language
│       └── common.json
└── types.ts          # Auto-generated key types
```

## Acceptance criteria

- [ ] i18next + react-i18next installed and configured
- [ ] All 5 namespaces created with English strings
- [ ] `useTranslation` hook provides type-safe keys
- [ ] Fallback to English when key missing in target language
- [ ] At least one target language namespace populated (Spanish) as proof-of-concept
- [ ] String extraction script identifies missing keys
- [ ] No hardcoded user-facing strings in any component

## Out of scope

- Full translations for all languages (add progressively per feature)
- RTL layout support (post-v1)
- Automated translation pipeline (manual initially)
