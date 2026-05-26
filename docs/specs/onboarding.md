# Spec: Onboarding

**Status**: draft
**Bounded contexts**: user, curriculum
**Issues**: [#38](https://github.com/guilherme-andrade/polyglot-ai/issues/38), [#39](https://github.com/guilherme-andrade/polyglot-ai/issues/39), [#40](https://github.com/guilherme-andrade/polyglot-ai/issues/40)

## Overview

First-launch onboarding flow that captures: target language, skill level, and
content interests. Presented after account creation. Can be skipped/revisited.

## Flow

```
Account Created → Target Language → Skill Level → Interests → Home
```

### Step 1: Target language selection (#38)

- List of supported languages with flag icons and native names
- Search/filter by language name
- Single selection (one language at a time in v1)
- Saved to user profile
- Can be changed later in settings

Initial supported languages: English, Spanish, French, German, Portuguese, Italian.

### Step 2: Skill level self-assessment (#39)

- 4 options with clear descriptions:
  - Beginner: "I know nothing or a few words"
  - Elementary: "I know basic phrases"
  - Intermediate: "I can have simple conversations"
  - Advanced: "I can discuss most topics"
- Saved to user profile in the curriculum context as initial CEFR estimate
- Placement test deferred to M2 (optional, more accurate)

### Step 3: Interest selection (#40)

- Content type options presented as tappable chips/tags:
  - Music, Films/TV, Books, Podcasts, News, Sports, Technology, Travel, Food
- Multi-select (minimum 1, maximum unlimited)
- At least 3 encouraged but not enforced
- Saved to user profile preferences
- Used by curriculum engine to match content

## Design

- Progress dots at top showing current step (1/3, 2/3, 3/3)
- "Continue" button at bottom (disabled until selection made)
- "Skip" text button for skill level and interests (use defaults)
- Smooth slide transition between steps

## Defaults when skipped

- Skill level: "Beginner"
- Interests: "Music", "Films/TV" (broadest content coverage)

## Server

- `PUT /api/user/profile` — stores language, level, interests
- Curriculum context receives `UserRegistered` event to create initial SkillProfile

## Acceptance criteria

- [ ] Target language selection screen with flag icons and search (#38)
- [ ] Skill level self-assessment with 4 options and descriptions (#39)
- [ ] Interest selection with multi-select chips (#40)
- [ ] Progress dots showing current step
- [ ] "Continue" disabled until selection made
- [ ] "Skip" available for skill level and interests
- [ ] Data persisted to user profile on completion
- [ ] Can re-access from Profile → Edit Preferences
- [ ] Smooth slide transitions between steps

## Out of scope

- Placement test (M2, #43)
- "Why do you want to learn?" motivation question
- Daily goal setting (M3, #54)
- Social features during onboarding
