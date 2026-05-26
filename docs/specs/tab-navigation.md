# Spec: Tab Navigation Shell

**Status**: draft
**Bounded contexts**: app (cross-cutting)
**Issue**: [#37](https://github.com/guilherme-andrade/polyglot-ai/issues/37)
**Depends on**: `auth.md`, `app-scaffold.md`

## Overview

Bottom tab bar with 3 tabs that forms the main navigation skeleton of the app.
Present after authentication, visible on all main screens.

## Tabs

| Tab | Icon | Label | Screen |
|-----|------|-------|--------|
| Learn | Book | "Learn" | Today's lesson / lesson history |
| Progress | Chart | "Progress" | Curriculum view / stats dashboard |
| Profile | Person | "Profile" | Settings, account, preferences |

## Behavior

- Tab bar always visible on main screens (hidden during onboarding, auth, lesson taking)
- Tab state preserved when switching (don't remount screens unless necessary)
- Smooth fade/slide transitions between tabs (default Expo Router animation)
- Active tab has filled icon + brand color; inactive tabs use outline icon + neutral gray
- Each tab preserves its scroll position when switching away and back

## Layout

```
(tabs)/
├── _layout.tsx    # Tab bar config (icons, labels, auth gate)
├── learn/
│   ├── _layout.tsx
│   └── index.tsx  # Today's lesson
├── progress/
│   ├── _layout.tsx
│   └── index.tsx  # Progress dashboard
└── profile/
    ├── _layout.tsx
    └── index.tsx  # Profile/settings
```

## Acceptance criteria

- [ ] 3-tab bottom navigation with Learn, Progress, Profile
- [ ] Each tab has icon + label
- [ ] Tab state preserved on switch (no remount)
- [ ] Smooth transitions between tabs
- [ ] Active/inactive icon styling (filled vs outline)
- [ ] Tab bar hidden during onboarding and auth flows
- [ ] Tab bar hidden during lesson taking (immersive mode)

## Out of scope

- Badge counts on tab icons (notification count, streak indicator — add in M3)
- Additional tabs (future: Leaderboard, Content)
- Tab bar customization by user
