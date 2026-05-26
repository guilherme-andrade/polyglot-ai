# Spec: XP & Daily Streaks

**Status**: draft
**Bounded contexts**: gamification
**Issues**: [#50](https://github.com/guilherme-andrade/polyglot-ai/issues/50), [#51](https://github.com/guilherme-andrade/polyglot-ai/issues/51)

## Overview

Core gamification loop: earn XP for completing lessons, maintain a daily streak.
These are the primary motivation mechanics in v1.

## XP system (#50)

### Earning rules
| Factor | Value |
|--------|-------|
| Per exercise completed | 5 XP |
| Accuracy bonus (100%) | 2x base XP |
| Accuracy bonus (≥80%) | 1.5x base XP |
| CEFR multiplier (A1) | 1x |
| CEFR multiplier (A2) | 1.2x |
| CEFR multiplier (B1) | 1.5x |
| CEFR multiplier (B2) | 2x |
| CEFR multiplier (C1–C2) | 2.5x |
| Lesson completion bonus | +10 XP |

Formula: `XP = (exercises × 5 × accuracyMultiplier × cefrMultiplier) + completionBonus`

### Display
- XPCounter component: pill showing current total XP
- "+N XP" popup animation on earn
- Particle burst / confetti on level-up
- XP total visible in Profile tab and on lesson completion screen

## Daily streak (#51)

### Rules
- Streak increments when user completes at least 1 lesson in a calendar day
- Streak resets to 0 if a day is missed
- Streak freeze: user can miss 1 day per week without resetting (deferred to post-v1)
- Milestone celebrations: 7 days, 30 days, 100 days, 365 days

### Display
- StreakCounter component: flame icon + count
- Animated increment on lesson completion (flame grows)
- Displayed prominently on Learn tab
- Milestone: confetti + "7 day streak!" badge

## Data model

```java
// Gamification context
@Entity
public class PlayerProfile {
    UUID userId;
    int totalXp;
    int currentStreak;
    int bestStreak;
    LocalDate lastLessonDate;
    List<XpTransaction> transactions;
}
```

## Contracts

| From | To | Contract |
|------|----|----------|
| lesson | gamification | `LessonCompleted` event → award XP, update streak |
| gamification | user | `AchievementUnlocked` event → notify profile (for badges) |

## Acceptance criteria

- [ ] XP awarded on lesson completion with formula (#50)
- [ ] XP persisted to PlayerProfile
- [ ] XP total visible on Profile tab and lesson completion screen
- [ ] Animated XP increment on earn
- [ ] Streak increments on first lesson of the day (#51)
- [ ] Streak resets on missed day
- [ ] Streak count displayed on Learn tab (flame + number)
- [ ] Milestone celebrations at 7, 30, 100 days
- [ ] Best streak tracked separately from current streak

## Out of scope

- Streak freeze (post-v1)
- Leaderboards (future)
- Achievements/badges (future, beyond streak milestones)
- XP decay (we don't penalize inactivity beyond streak loss)
