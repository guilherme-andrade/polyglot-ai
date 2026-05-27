# i18n Infrastructure

## Purpose

Internationalization scaffolding MUST be set up from day one so no user-facing string is ever hardcoded. Retrofitting i18n after building screens is expensive — the scaffolding SHALL be in place before any feature screen is built. Every string MUST go through i18next.

## Requirements

### Requirement: i18next MUST be configured with namespace structure

The project SHALL install and configure i18next + react-i18next. Five namespaces MUST be defined: `common` (shared UI), `lesson` (exercise prompts), `profile` (settings), `onboarding` (welcome flow), and `errors` (API/validation messages).

#### Scenario: Translation key resolves from correct namespace
- GIVEN the i18next instance is configured
- WHEN `t('lesson:exercise.prompt')` is called
- THEN it SHALL resolve from the `lesson` namespace
- AND return the English string if the target language key is missing

### Requirement: English MUST be the fallback language

English (`en`) SHALL be the fallback language. When a key is missing in the target language, the English value SHALL be returned. No key SHALL silently render empty.

#### Scenario: Missing Spanish key falls back to English
- GIVEN the Spanish `common.json` is missing the key `submit`
- WHEN `t('common:submit')` is called with Spanish locale
- THEN the English value "Submit" SHALL be returned

### Requirement: Translation keys MUST be type-safe

TypeScript types SHALL be generated from the English translation files so that `t()` calls have autocomplete and compile-time checking. Invalid keys MUST produce a TypeScript error.

#### Scenario: Invalid key produces TypeScript error
- GIVEN type-safe translation keys are configured
- WHEN a component calls `t('common:nonexistent')`
- THEN `tsc --noEmit` SHALL report a type error

### Requirement: At least one target language MUST be populated

Spanish (`es`) SHALL have the `common` namespace populated as a proof-of-concept. Other namespaces and languages SHALL be added progressively with each feature.

#### Scenario: Spanish common strings are available
- GIVEN the app is set to Spanish locale
- WHEN a common UI element like a button renders
- THEN the button text SHALL display in Spanish

### Requirement: No user-facing string SHALL be hardcoded

Every user-visible string in the app SHALL go through the `t()` function. ESLint rules SHALL flag hardcoded strings in JSX where a translation key is expected.

#### Scenario: Hardcoded string in JSX is flagged
- GIVEN a component renders `<Text>Welcome back!</Text>`
- WHEN ESLint runs
- THEN it SHALL warn that the string should use i18next
