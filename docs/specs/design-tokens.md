# Spec: Design Tokens

**Status**: draft
**Bounded contexts**: app (cross-cutting)
**Issue**: [#28](https://github.com/guilherme-andrade/polyglot-ai/issues/28)

## Overview

Define the design token system that underpins all UI components. Tokens are the
single source of truth for visual decisions, exported for both NativeWind and
Figma.

## Color palette

### Brand
- Primary: warm green (#58CC02 — Duolingo-inspired)
- Primary dark: (#4CAF00)
- Secondary: deep blue (#1C64F2)
- Accent: warm orange (#FF9600)

### Semantic
- Success: (#58CC02)
- Error: (#EA2E2E)
- Warning: (#FF9600)
- Info: (#1C64F2)

### Neutral
- White, gray-50 through gray-900, black

## Typography scale

| Token | Size | Weight | Line height |
|-------|------|--------|-------------|
| h1 | 28px | 800 (extrabold) | 34px |
| h2 | 22px | 700 (bold) | 28px |
| h3 | 18px | 700 (bold) | 24px |
| h4 | 16px | 600 (semibold) | 22px |
| body | 16px | 400 (regular) | 22px |
| body-sm | 14px | 400 (regular) | 20px |
| caption | 12px | 400 (regular) | 16px |
| label | 14px | 600 (semibold) | 20px |

Font family: System default (San Francisco on iOS, Roboto on Android).

## Spacing scale

4px base grid: 4, 8, 12, 16, 20, 24, 32, 40, 48, 56, 64.

## Border radius

4, 8, 12, 16, 24, 9999 (pill).

## Shadows

sm, md, lg — each with elevation for Android and shadow properties for iOS.

## Design token export

- NativeWind: extend `tailwind.config.js` theme with these tokens
- Figma: tokens exported as Figma styles (manual sync initially)

## Acceptance criteria

- [ ] Color palette defined and exported to NativeWind theme
- [ ] Typography scale defined and exported
- [ ] Spacing scale defined and exported
- [ ] Border radius and shadow tokens defined
- [ ] Tailwind theme extended with all tokens
- [ ] Light mode complete (dark mode slots reserved via semantic tokens)
- [ ] Tokens referenced by name in all components (no hardcoded hex values)

## Out of scope

- Dark mode implementation (post-v1)
- Automated Figma sync (manual initially)
