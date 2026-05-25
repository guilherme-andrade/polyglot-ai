# Polyglot AI — Mobile App

React Native app built with Expo SDK 52+ and TypeScript.

## Stack

- **Framework**: React Native (Expo managed workflow)
- **Language**: TypeScript (strict mode)
- **Navigation**: Expo Router (file-based routing)
- **State**: Zustand (UI) + TanStack Query (server state)
- **Styling**: NativeWind (Tailwind for RN)
- **Testing**: Jest + React Native Testing Library + Maestro (E2E)

## Getting Started

```bash
# Install dependencies
pnpm install

# Start Expo dev server
pnpm start

# Run on iOS simulator
pnpm ios

# Run on Android emulator
pnpm android

# Run tests
pnpm test

# Run E2E tests
pnpm test:e2e
```

## Project Structure

```
app/
├── src/
│   ├── app/                  # Expo Router file-based routes
│   ├── features/             # Feature modules
│   │   ├── lesson/           # Lesson-taking experience
│   │   ├── profile/          # User profile & preferences
│   │   ├── curriculum/       # Curriculum view & progress
│   │   └── onboarding/       # First-time user flow
│   ├── components/           # Shared UI components
│   ├── hooks/                # Shared hooks
│   ├── services/             # API client, auth
│   ├── stores/               # Zustand stores
│   └── lib/                  # Utilities, types, constants
├── assets/                   # Static assets (images, fonts, lottie)
├── app.json                  # Expo config
├── tsconfig.json
└── package.json
```

## Conventions

- Features are self-contained — never import from another feature.
- All API calls go through TanStack Query hooks.
- Use Expo Router's file-based routing; one component per route file.
- Follow the design system in `src/components/` for UI consistency.
