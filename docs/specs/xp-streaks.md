# XP & Daily Streaks

## Purpose

Core gamification loop: users earn XP for completing lessons and maintain a daily streak by learning every day. These are the primary motivation mechanics in v1. XP MUST be calculated from exercise count, accuracy, and CEFR level. Streaks SHALL reset on missed days.

## Requirements

### Requirement: XP MUST be awarded on lesson completion

When a lesson is completed, the gamification context SHALL calculate XP as: `(exercisesCompleted × 5 × accuracyMultiplier × cefrMultiplier) + completionBonus`. Accuracy multiplier: 2x for 100%, 1.5x for ≥80%, 1x otherwise. CEFR multiplier: 1x for A1, 1.2x for A2, 1.5x for B1, 2x for B2, 2.5x for C1–C2. Completion bonus: 10 XP.

#### Scenario: Perfect score at B1 level
- GIVEN a lesson with 10 exercises, 100% accuracy, B1 level
- WHEN the lesson is completed
- THEN XP SHALL be: (10 × 5 × 2.0 × 1.5) + 10 = 160 XP

### Requirement: XP SHALL be persisted and displayed

XP SHALL be persisted to the PlayerProfile aggregate. The XPCounter component SHALL display the current total as a pill. On earn, a "+N XP" popup SHALL animate. On level-up, a particle burst or confetti animation SHALL play.

#### Scenario: XP counter animates on earn
- GIVEN the user has 500 XP
- WHEN a lesson awards 45 XP
- THEN the counter SHALL animate from 500 to 545
- AND a "+45 XP" popup SHALL appear briefly

### Requirement: Streak MUST increment on first lesson each day

The streak SHALL increment when the user completes at least one lesson in a calendar day. If a day is missed, the streak SHALL reset to 0. Best streak SHALL be tracked separately. Streak milestones (7, 30, 100 days) SHALL trigger a celebration animation.

#### Scenario: Missed day resets streak
- GIVEN the user has a 12-day streak and last played 2 days ago
- WHEN the user completes a lesson today
- THEN the streak SHALL reset to 1
- AND the best streak SHALL remain 12

#### Scenario: 7-day streak triggers celebration
- GIVEN the user has a 6-day streak
- WHEN they complete a lesson on day 7
- THEN the streak SHALL increment to 7
- AND a confetti celebration SHALL play

### Requirement: Streak SHALL be displayed prominently

The StreakCounter component SHALL show a flame icon with the current streak count. It SHALL animate on increment (flame grows). It SHALL be displayed on the Learn tab. Best streak SHALL be visible on the Progress dashboard.

#### Scenario: Flame icon grows on streak increment
- GIVEN the streak increments from 6 to 7
- WHEN the StreakCounter updates
- THEN the flame icon SHALL animate (scale up briefly)

### Requirement: LessonCompleted event MUST trigger gamification updates

The gamification context SHALL subscribe to `LessonCompleted` events from the lesson context. On receipt, it SHALL award XP, update the streak, and check for milestone achievements.

#### Scenario: Gamification updates on LessonCompleted event
- GIVEN a `LessonCompleted` event is published
- WHEN the gamification context receives it
- THEN XP SHALL be calculated and added
- AND the streak SHALL be updated if this is the first lesson today
