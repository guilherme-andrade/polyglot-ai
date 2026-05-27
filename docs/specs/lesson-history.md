# Lesson History & Review

## Purpose

Users SHALL be able to browse their completed lessons in reverse chronological order, grouped by week, and review any past lesson in read-only mode. The history SHALL support pagination for performance.

## Requirements

### Requirement: History MUST display lessons in reverse chronological order grouped by week

The history list SHALL show completed lessons newest first, grouped by week ("This Week", "Last Week", "May 12–18", etc.). Each entry SHALL show: date, lesson topic, score (X/Y), XP earned, and a mini progress bar.

#### Scenario: History groups by week
- GIVEN the user completed lessons on Monday and the previous Friday
- WHEN viewing history
- THEN the Monday lesson SHALL appear under "This Week"
- AND the Friday lesson SHALL appear under "Last Week"

### Requirement: Tapping a lesson MUST open read-only review mode

Tapping a history entry SHALL navigate to a full lesson view showing all exercises with the user's original answers and correct answers. Correct answers SHALL have a green checkmark; incorrect answers SHALL have a red X with the correct answer shown. No interaction SHALL be possible.

#### Scenario: Review shows user's incorrect answer alongside correct answer
- GIVEN the user answered "la mesa" but the correct answer was "el libro"
- WHEN the lesson is reviewed
- THEN the exercise SHALL show the user's answer "la mesa" with a red X
- AND the correct answer "el libro" SHALL be displayed

### Requirement: History MUST be paginated

The server SHALL return paginated history (`page` and `size` parameters). The client SHALL load more entries on scroll (infinite scroll). Each page SHALL return at most 20 lessons.

#### Scenario: Scrolling to bottom loads more history
- GIVEN the user has 45 completed lessons
- WHEN the user scrolls to the bottom of the history list
- THEN the next page SHALL load
- AND 20 more lessons SHALL appear
