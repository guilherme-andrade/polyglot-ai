# Spec: Lesson History & Review

**Status**: draft
**Bounded contexts**: lesson
**Issue**: [#49](https://github.com/guilherme-andrade/polyglot-ai/issues/49)

## Overview

Browse and re-review completed lessons. Reverse-chronological list with summary
stats, grouped by week. Tap to see full lesson in read-only review mode.

## UI

### History list
- Reverse chronological (newest first)
- Grouped by week: "This Week", "Last Week", "May 12–18", ...
- Each entry: date, lesson topic, score (X/Y), XP earned, mini progress bar
- Pull-to-refresh

### Lesson review (read-only)
- Tap a history entry → full lesson view
- All exercises shown with user's original answer and correct answer
- Correct answers: green checkmark
- Incorrect answers: red X with correct answer shown
- No interaction (read-only)
- Back button returns to history list

## Server

- `GET /api/lessons/history?page=0&size=20` → paginated lesson summaries
- `GET /api/lessons/{id}` → full lesson with exercises and user's answers

## Acceptance criteria

- [ ] History list: reverse chronological, grouped by week
- [ ] Each entry shows: date, topic, score, XP
- [ ] Tap entry → read-only review mode
- [ ] Review mode shows all exercises with user's answers and correct answers
- [ ] Correct/incorrect visual indicators
- [ ] Pagination (load more on scroll)

## Out of scope

- Filtering by topic / date range
- Search within lessons
- Anki-style spaced repetition export (future feature)
