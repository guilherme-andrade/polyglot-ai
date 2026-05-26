# Spec: Progress Dashboard

**Status**: draft
**Bounded contexts**: user, gamification, curriculum
**Issue**: [#52](https://github.com/guilherme-andrade/polyglot-ai/issues/52)

## Overview

A dashboard screen (Progress tab) that visualizes the user's learning journey:
XP growth, streaks, vocabulary expansion, CEFR progress, and lesson completion
stats.

## Sections

### XP over time
- Line or bar chart: weekly and monthly views
- Toggle between "Week" and "Month"
- X-axis: days/weeks, Y-axis: cumulative XP

### Streak
- Current streak (flame + number)
- Best streak ever
- "Next milestone: 30 days (12 to go)"

### Vocabulary
- Total words learned
- Words learned this week
- Mini word cloud or list of recent words

### CEFR progress
- Current CEFR level estimate (A1–C2)
- Progress bar to next level (e.g. "A2 — 60% to B1")
- Sub-scores: vocabulary, grammar, reading

### Lessons
- Lessons completed this week / this month
- Average score
- Total time spent learning

## Data sources

| Metric | Source context | Query |
|--------|---------------|-------|
| XP history | gamification | `GET /api/gamification/xp-history?period=week\|month` |
| Streak | gamification | `GET /api/gamification/player-profile` |
| Vocabulary count | curriculum | `GET /api/curriculum/vocabulary-stats` |
| CEFR level | curriculum | `GET /api/curriculum/skill-profile` |
| Lesson stats | lesson | `GET /api/lessons/stats` |

## UI

- Scrollable dashboard with sections
- Pull-to-refresh
- Accessible from Progress tab (2nd tab in bottom nav)

## Acceptance criteria

- [ ] XP chart: weekly and monthly views
- [ ] Streak: current streak, best streak, next milestone
- [ ] Vocabulary: total words, words this week
- [ ] CEFR: current level, progress to next level, sub-scores
- [ ] Lessons: completed count, average score, total time
- [ ] All data fetched from server, not computed client-side
- [ ] Pull-to-refresh

## Out of scope

- Sharing progress to social media
- Detailed per-skill breakdowns (listening, speaking)
- Comparison to "other learners like you"
- Export progress report
