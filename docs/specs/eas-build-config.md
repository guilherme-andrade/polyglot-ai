# Spec: EAS Build Configuration

**Status**: draft
**Bounded contexts**: devops
**Issue**: [#31](https://github.com/guilherme-andrade/polyglot-ai/issues/31)
**Depends on**: `app-scaffold.md`, `deploy-staging.md`, `deploy-prod.md`

## Overview

Configure Expo Application Services (EAS) for building and distributing the
mobile app. Two profiles: preview (internal distribution) and production
(app store submission).

## Profiles

### Preview
- Purpose: internal testing via TestFlight / Google Play Internal Testing
- Build type: `.app` (iOS) / `.apk` (Android)
- API target: staging
- Provisioning: iOS ad-hoc provisioning profile
- Channel: `preview`

### Production
- Purpose: App Store + Google Play submission
- Build type: store-ready
- API target: production
- Provisioning: iOS App Store provisioning profile
- Channel: `production`

## `eas.json`

```json
{
  "cli": { "version": ">= 5.0.0" },
  "build": {
    "preview": {
      "distribution": "internal",
      "channel": "preview",
      "env": { "API_URL": "$STAGING_API_URL", "GRAPHQL_URL": "$STAGING_GRAPHQL_URL" }
    },
    "production": {
      "distribution": "store",
      "channel": "production",
      "env": { "API_URL": "$PROD_API_URL", "GRAPHQL_URL": "$PROD_GRAPHQL_URL" }
    }
  },
  "submit": {
    "production": {}
  }
}
```

## Environment variable management

- API URLs per profile set in `eas.json` or EAS Secrets
- `EXPO_TOKEN` stored in GitHub Secrets for CI-triggered builds
- `.env.example` documents all required vars

## Acceptance criteria

- [ ] `eas.json` committed with `preview` and `production` build profiles
- [ ] `eas build --profile preview` produces installable artifact
- [ ] `eas build --profile production` produces store-submittable artifact
- [ ] Environment variables resolved correctly per profile
- [ ] Android keystore managed via EAS (not committed)

## Out of scope

- `eas submit` configuration (needs store connect setup first — separate spec: `app-store-setup.md`)
- OTA update configuration (`eas update` — covered in `deploy-staging.md`)
