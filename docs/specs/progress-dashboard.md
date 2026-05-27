# Progress Dashboard

## Purpose

The Progress tab SHALL visualise the user's learning journey: XP growth over time, streak status, vocabulary expansion, CEFR progress, and lesson completion stats. All data MUST come from the server; no metrics SHALL be computed client-side.

## Requirements

### Requirement: XP chart MUST show weekly and monthly views

An XP chart SHALL display cumulative XP over time. The user SHALL toggle between "Week" and "Month" views. X-axis SHALL show days (week view) or weeks (month view). Y-axis SHALL show cumulative XP.

#### Scenario: Toggle switches between week and month views
- GIVEN the dashboard is showing the weekly XP chart
- WHEN the user taps "Month"
- THEN the chart SHALL switch to monthly view
- AND the X-axis SHALL show weeks instead of days

### Requirement: Streak section MUST show current and best streaks

The dashboard SHALL display current streak (flame + count), best streak ever, and the next milestone with progress ("Next milestone: 30 days — 12 to go").

#### Scenario: Streak milestone shows countdown
- GIVEN the user has an 18-day streak
- WHEN the dashboard renders
- THEN it SHALL show "Next milestone: 30 days — 12 to go"

### Requirement: Vocabulary section MUST show total and weekly count

The dashboard SHALL display total words learned and words learned this week. A mini word cloud or recent words list MAY be included.

#### Scenario: Vocabulary count updates after lesson
- GIVEN the user learned 8 new words in today's lesson
- WHEN the dashboard refreshes
- THEN "Total words learned" SHALL increase by 8
- AND "Words this week" SHALL increase by 8

### Requirement: CEFR section MUST show current level and progress to next

The dashboard SHALL display the current CEFR level estimate (A1–C2) with a progress bar to the next level (e.g. "A2 — 60% to B1"). Sub-scores for vocabulary, grammar, and reading SHALL be displayed.

#### Scenario: Progress bar fills toward next level
- GIVEN the user is at A2 with 60% proficiency toward B1
- WHEN the dashboard renders
- THEN the progress bar SHALL be filled to 60%

### Requirement: Lesson stats MUST show completed count, average score, and total time

The dashboard SHALL display: lessons completed this week and this month, average score across all lessons, and total time spent learning.

#### Scenario: Lesson stats reflect weekly activity
- GIVEN the user completed 4 lessons this week with scores 7, 8, 9, and 10
- WHEN the dashboard renders
- THEN "Lessons this week" SHALL show 4
- AND "Average score" SHALL show 8.5/10
