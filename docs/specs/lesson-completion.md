# Spec: Lesson Completion Screen

**Status**: draft
**Bounded contexts**: lesson, gamification
**Issue**: [#48](https://github.com/guilherme-andrade/polyglot-ai/issues/48)

## Overview

After finishing all exercises in a lesson, show a results summary with score,
XP earned, new words learned, and per-exercise breakdown. The completion event
triggers XP award and curriculum progress update.

## UI

### Score section
- X/Y correct (e.g. "7/10")
- Percentage
- XP earned ("+45 XP" with animated increment)
- Time taken to complete

### Word summary
- New words learned in this lesson (list with target + native)
- Words to review (ones the user got wrong)

### Exercise breakdown
- List of exercises: prompt shown, user's answer, correct/incorrect indicator
- Tap to expand and see full correct answer

### CTA
- "Continue" button → returns to Learn tab
- "Review mistakes" button → shows only incorrect exercises with correct answers (deferred to v2)

## Server

- `POST /api/lessons/{id}/complete` — marks lesson complete, triggers:
  - `LessonCompleted` event → gamification context (award XP)
  - `LessonCompleted` event → curriculum context (update SkillProfile, unlock next unit)
  - `LessonCompleted` event → analytics context (log completion)

## XP calculation

See `xp-streaks.md` (M3) for full formula. Summary:
- Base XP per exercise
- Accuracy bonus (100% = 2x XP)
- CEFR multiplier (higher level = more XP)

## Acceptance criteria

- [ ] Score display: X/Y correct, percentage, XP earned, time taken
- [ ] New words learned section with target + native pairs
- [ ] Words to review section (incorrect answers)
- [ ] Per-exercise breakdown with correct/incorrect indicators
- [ ] "Continue" button returns to Learn tab
- [ ] Server: lesson completion triggers XP award (gamification context)
- [ ] Server: lesson completion triggers curriculum update
- [ ] XP animated counter on the completion screen

## Out of scope

- "Review mistakes" interactive mode (post-v1)
- Share results (social)
- Lesson rating / feedback from user
