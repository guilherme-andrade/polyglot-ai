# Profile & Settings

## Purpose

The Profile screen SHALL display and allow editing of account information, preferences, and learning settings. Daily goal setting MUST be configurable here with options from 1 to 5 lessons per day. Sensitive operations like email and password change SHALL require re-authentication.

## Requirements

### Requirement: Profile MUST display avatar, name, email, language, and level

The profile screen SHALL display: avatar (image or initials fallback), display name, email, target language with flag, CEFR skill level, and member since date.

#### Scenario: Initials shown when no avatar set
- GIVEN the user has not uploaded an avatar
- WHEN the profile screen renders
- THEN the user's initials SHALL be displayed in a colored circle

### Requirement: Display name and avatar MUST be editable

The user SHALL edit their display name inline (tap to edit). The avatar SHALL be changeable via photo picker. If no photo is selected, initials fallback SHALL be used.

#### Scenario: Tapping name enables inline editing
- GIVEN the user views their profile
- WHEN they tap their display name
- THEN an inline text input SHALL appear
- AND the name SHALL persist on save

### Requirement: Target language change MUST show a warning

The user MAY change their target language from Profile. Changing the language SHALL show a warning: "Progress is per-language. Starting a new language creates a separate profile." The user MUST confirm before the change takes effect.

#### Scenario: Language change requires confirmation
- GIVEN the user selects a new target language
- WHEN they attempt to save
- THEN a confirmation dialog SHALL appear with the warning
- AND the change SHALL only apply after confirmation

### Requirement: Email change MUST require re-verification

When the user changes their email, the server SHALL send a verification email to the new address. The change SHALL NOT take effect until verified.

#### Scenario: Email change triggers verification
- GIVEN the user enters a new email address
- WHEN they save
- THEN a verification email SHALL be sent to the new address
- AND the old email SHALL remain active until verification completes

### Requirement: Password change MUST require current password

The password change flow SHALL require: current password, new password, and new password confirmation. The new password MUST meet the same strength requirements as registration.

#### Scenario: Wrong current password blocks change
- GIVEN the user enters an incorrect current password
- WHEN the password change is submitted
- THEN the server SHALL return 400
- AND the message SHALL say "Current password is incorrect"

### Requirement: Daily goal MUST be configurable from 1 to 5 lessons

The daily goal selector SHALL offer options: 1, 2, 3, or 5 lessons per day. Default is 1. The goal SHALL be displayed on the Learn tab with a progress ring (e.g. "2/3 today"). Push notifications SHALL respect the goal (remind until goal met).

#### Scenario: Progress ring reflects daily goal
- GIVEN the daily goal is set to 3 and the user has completed 2 lessons
- WHEN the Learn tab renders
- THEN the progress ring SHALL show 2/3 (67% filled)

#### Scenario: Goal change takes effect immediately
- GIVEN the user changes daily goal from 1 to 3
- WHEN they save and return to the Learn tab
- THEN the progress ring SHALL now target 3 lessons
