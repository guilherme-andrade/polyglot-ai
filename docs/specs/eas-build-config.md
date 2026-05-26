# EAS Build Configuration

## Purpose

Configure Expo Application Services (EAS) for building and distributing the mobile app. Two build profiles MUST be defined: preview (internal distribution pointing to staging) and production (store submission pointing to production). Environment variables SHALL differ per profile.

## Requirements

### Requirement: Preview profile MUST build for internal distribution

The preview build profile SHALL produce `.app` (iOS) and `.apk` (Android) artifacts for internal testing via TestFlight and Google Play Internal Testing. It SHALL point to the staging API. It SHALL use the `preview` channel.

#### Scenario: Preview build installs on test device
- GIVEN the preview profile is configured
- WHEN `eas build --profile preview` completes
- THEN a TestFlight-invited device SHALL be able to install the build
- AND the app SHALL connect to the staging API

### Requirement: Production profile MUST build for store submission

The production build profile SHALL produce store-ready artifacts for App Store and Google Play submission. It SHALL point to the production API. It SHALL use the `production` channel.

#### Scenario: Production build is store-submittable
- GIVEN the production profile is configured
- WHEN `eas build --profile production` completes
- THEN the artifact SHALL pass App Store validation (if iOS)
- AND the artifact SHALL pass Google Play pre-launch checks (if Android)

### Requirement: Environment variables MUST differ per profile

`API_URL` and `GRAPHQL_URL` SHALL be set per profile: preview profiles SHALL use staging URLs, production profiles SHALL use production URLs. These values SHALL come from EAS Secrets or `eas.json` env blocks.

#### Scenario: Preview build targets staging API
- GIVEN a preview build is installed
- WHEN the app makes a GraphQL request
- THEN it SHALL call the staging GraphQL endpoint
- AND it SHALL NOT call the production endpoint

### Requirement: Android keystore MUST be managed via EAS

Android signing keys SHALL be managed through EAS. The keystore SHALL NOT be committed to the repository. EAS SHALL handle signing automatically.

#### Scenario: Android build is signed automatically
- GIVEN the EAS project is configured with Android credentials
- WHEN `eas build --profile production` is run for Android
- THEN the resulting APK/AAB SHALL be signed
- AND the keystore SHALL NOT exist in the repository
