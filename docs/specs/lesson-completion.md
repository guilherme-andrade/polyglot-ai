# Lesson Completion

## Purpose

After finishing all exercises in a lesson, the user SHALL see a results summary with score, XP earned, new words learned, and per-exercise breakdown. The completion event MUST trigger XP award in the gamification context and curriculum progress update via domain events.

## Requirements

### Requirement: Completion screen MUST show score, XP, and time

The completion screen SHALL display: X/Y correct, percentage, XP earned with animated increment, and time taken to complete. The XP counter SHALL animate from 0 to the earned value.

#### Scenario: XP counter animates on screen
- GIVEN the user earned 45 XP
- WHEN the completion screen renders
- THEN the XP counter SHALL animate from 0 to 45
- AND the animation SHALL complete within 2 seconds

### Requirement: New words learned MUST be listed with translations

The completion screen SHALL list all new words introduced in this lesson, each with target language word and native language translation. Words the user got wrong SHALL appear in a separate "Words to review" section.

#### Scenario: Words to review are separated from new words
- GIVEN the user got 3 words wrong during the lesson
- WHEN the completion screen renders
- THEN those 3 words SHALL appear under "Words to review"
- AND correctly answered new words SHALL appear under "New words learned"

### Requirement: Per-exercise breakdown MUST show correct/incorrect

The completion screen SHALL list every exercise with: prompt, user's answer, and correct/incorrect indicator. The user MAY tap an exercise to expand and see the full correct answer.

#### Scenario: Tapping an incorrect exercise shows the correct answer
- GIVEN an exercise was answered incorrectly
- WHEN the user taps it in the breakdown
- THEN the full correct answer SHALL be revealed

### Requirement: Lesson completion MUST publish a domain event

When the lesson is marked complete via `POST /api/lessons/{id}/complete`, the server SHALL publish a `LessonCompleted` domain event consumed by: gamification context (award XP, update streak), curriculum context (update SkillProfile, unlock next unit), and analytics context (log completion).

#### Scenario: Completing a lesson awards XP
- GIVEN a lesson with 8/10 correct at B1 level
- WHEN the lesson is completed
- THEN the gamification context SHALL receive `LessonCompleted`
- AND XP SHALL be calculated as: (exercises × 5 × accuracyMultiplier × cefrMultiplier) + completionBonus

### Requirement: Continue button MUST return to Learn tab

A "Continue" button SHALL be the primary CTA on the completion screen. Tapping it SHALL return the user to the Learn tab, where the daily goal progress ring reflects the completed lesson.

#### Scenario: Continue returns to Learn tab
- GIVEN the user views the completion screen
- WHEN "Continue" is tapped
- THEN the app SHALL navigate to the Learn tab
- AND the daily goal ring SHALL show updated progress
