# Onboarding

## Purpose

First-launch onboarding flow that captures the user's target language, skill level, and content interests. The flow MUST complete before the user enters the main app. Every step SHALL be skippable with sensible defaults so the user is never blocked.

## Requirements

### Requirement: User MUST select a target language

The first onboarding step SHALL present supported languages (English, Spanish, French, German, Portuguese, Italian) with flag icons and native names. A search/filter field SHALL allow narrowing by name. Single selection is required — the user learns one language at a time in v1.

#### Scenario: Language selection persists to profile
- GIVEN the user selects "Spanish" on the language screen
- WHEN they tap Continue
- THEN the selection SHALL be saved to user profile
- AND the curriculum context SHALL receive a `UserRegistered` event

#### Scenario: Search filters language list
- GIVEN the user types "ger" in the search field
- WHEN the list filters
- THEN only "German" SHALL remain visible

### Requirement: User MUST self-assess their skill level

The second step SHALL present 4 skill level options with clear descriptions: Beginner ("I know nothing or a few words"), Elementary ("I know basic phrases"), Intermediate ("I can have simple conversations"), Advanced ("I can discuss most topics"). A more accurate placement test is deferred to M2.

#### Scenario: Skill level defaults to Beginner when skipped
- GIVEN the user taps Skip on the skill level screen
- WHEN the onboarding flow completes
- THEN the skill level SHALL default to "Beginner"

### Requirement: User MUST select at least one content interest

The third step SHALL present content type options as tappable chips: Music, Films/TV, Books, Podcasts, News, Sports, Technology, Travel, Food. Minimum 1 selection is required. At least 3 SHALL be encouraged but not enforced. These are used by the curriculum engine to match content.

#### Scenario: Continue disabled until at least one interest selected
- GIVEN the user has not tapped any interest chip
- WHEN the screen renders
- THEN the Continue button SHALL be disabled

#### Scenario: Defaults apply when skipped
- GIVEN the user taps Skip on the interest screen
- WHEN onboarding completes
- THEN interests SHALL default to "Music" and "Films/TV"

### Requirement: Progress MUST be visible with step indicator

Progress dots SHALL show the current step (1/3, 2/3, 3/3). Slide transitions SHALL animate between steps. A Continue button SHALL advance to the next step.

#### Scenario: Step indicator shows current position
- GIVEN the user completes language selection
- WHEN the skill level screen appears
- THEN the progress dots SHALL show step 2 of 3
