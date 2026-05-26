# Tab Navigation Shell

## Purpose

The bottom tab bar MUST form the main navigation skeleton of the app, visible on all primary screens after authentication. Tabs SHALL preserve their state when switching so the user's scroll position and data are not lost. The tab bar SHALL hide during immersive experiences (lesson taking) and auth/onboarding flows.

## Requirements

### Requirement: Three tabs MUST provide primary navigation

The tab bar SHALL include three tabs: Learn (Book icon), Progress (Chart icon), and Profile (Person icon). Each tab SHALL have an icon and label. Active tabs SHALL use filled icons with brand color; inactive tabs SHALL use outline icons with neutral gray.

#### Scenario: Tab bar renders after login
- GIVEN the user has authenticated
- WHEN the main screen renders
- THEN the bottom tab bar SHALL be visible with 3 tabs
- AND the Learn tab SHALL be active by default

#### Scenario: Tapping a tab switches screens
- GIVEN the user is on the Learn tab
- WHEN the Progress tab is tapped
- THEN the screen SHALL switch to the Progress view
- AND the Progress icon SHALL become filled with brand color

### Requirement: Tab state MUST be preserved on switch

When switching between tabs, the inactive tab's scroll position and rendered content SHALL be preserved. Switching back SHALL NOT cause a remount.

#### Scenario: Scroll position preserved
- GIVEN the user scrolls down on the Progress tab
- WHEN they switch to Learn and then back to Progress
- THEN the scroll position SHALL be where they left it

### Requirement: Tab bar MUST hide during auth, onboarding, and lesson taking

The tab bar SHALL NOT be visible on auth screens (login, register), onboarding screens, or during active lesson taking (immersive mode).

#### Scenario: Tab bar hidden during lesson
- GIVEN the user starts a lesson from the Learn tab
- WHEN the lesson screen renders
- THEN the bottom tab bar SHALL be hidden

### Requirement: File-based routing MUST use Expo Router route groups

The tab layout SHALL be defined in `(tabs)/_layout.tsx`. Auth screens SHALL be in `(auth)/`. The root layout SHALL handle routing between auth and tabs based on authentication state.

#### Scenario: File structure matches route groups
- GIVEN the app scaffold is complete
- WHEN `ls app/src/app/` is run
- THEN `(auth)/` and `(tabs)/` route groups SHALL exist
