# App Store Setup

## Purpose

Create placeholder app listings on App Store Connect and Google Play Console so preview builds have distribution targets. Screenshots and polished descriptions SHALL come later — this spec covers only the minimum registration and provisioning needed for the build pipeline to work.

## Requirements

### Requirement: App MUST be registered on App Store Connect with bundle ID

An app registration SHALL be created on App Store Connect with bundle ID `ai.polyglot.app`. iOS provisioning profiles for development, ad-hoc (preview), and App Store (production) MUST be created and available to EAS. A TestFlight internal testing group named "Polyglot Team" SHALL be created.

#### Scenario: EAS build can use App Store provisioning profile
- GIVEN the app is registered on App Store Connect
- WHEN `eas build --profile production` is run for iOS
- THEN it SHALL use the App Store provisioning profile
- AND the signed IPA SHALL be submittable to the App Store

### Requirement: App MUST be registered on Google Play Console with signing configured

An app registration SHALL be created on Google Play Console with application ID `ai.polyglot.app`. Google Play App Signing SHALL be enabled (let Google manage keys). An internal testing track named "polyglot-team" SHALL be created.

#### Scenario: Internal test track receives preview builds
- GIVEN the internal testing track exists
- WHEN a preview build is uploaded
- THEN testers on the track SHALL receive the update

### Requirement: Placeholder store listings MUST exist

Both stores SHALL have placeholder listings with the app name and a brief description. Full screenshots and promotional content SHALL be out of scope.

#### Scenario: Placeholder listing is visible to internal testers
- GIVEN a placeholder listing exists on TestFlight
- WHEN an internal tester views the app
- THEN they SHALL see the app name and placeholder description

### Requirement: Store credentials MUST be stored in GitHub Secrets

App Store Connect API keys, Google Play service account JSON, and any other store credentials SHALL be stored in GitHub Secrets. No credentials SHALL be committed.

#### Scenario: EAS build uses credentials from CI
- GIVEN store credentials exist in GitHub Secrets
- WHEN a CI-triggered build runs
- THEN EAS SHALL authenticate using those credentials
