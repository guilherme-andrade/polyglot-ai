# Spec: App Store Connect & Google Play Console Setup

**Status**: draft
**Bounded contexts**: devops
**Issue**: [#32](https://github.com/guilherme-andrade/polyglot-ai/issues/32)
**Depends on**: `app-scaffold.md`, `eas-build-config.md`

## Overview

Create app listings on both stores so preview builds have somewhere to go.
Placeholder listings only — screenshots and polished descriptions come later.

## App Store Connect

- App registration with bundle ID: `ai.polyglot.app`
- iOS provisioning profiles: development, ad-hoc (preview), App Store (production)
- TestFlight internal testing group: "Polyglot Team"
- Placeholder metadata: name, description, privacy policy URL

## Google Play Console

- App registration with application ID: `ai.polyglot.app`
- Signing keys: let Google manage (Play App Signing)
- Internal testing track: "polyglot-team"
- Placeholder store listing: name, short description, content rating

## Acceptance criteria

- [ ] App Store Connect: app registered, bundle ID reserved
- [ ] Apple: provisioning profiles created and available in EAS
- [ ] Google Play Console: app registered, signing configured
- [ ] TestFlight internal testing group created
- [ ] Google Play internal testing track created
- [ ] Both stores have placeholder listings (name + description)
- [ ] Store credentials (API keys, etc.) stored in GitHub Secrets

## Out of scope

- Screenshots and promotional materials
- Full privacy policy (placeholder URL → real page)
- App review submission (only when we have a production build)
