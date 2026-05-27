# Curriculum

## Purpose

The curriculum context SHALL own skill assessment and personalised learning path generation. It MUST take a user's self-assessed level from onboarding, optionally refine it with an adaptive placement test, and generate a curriculum of ordered skill units that the lesson engine draws from. The curriculum SHALL adapt as the user progresses.

## Requirements

### Requirement: Placement test MUST be adaptive and skippable

An adaptive placement test SHALL present 10–20 questions that adjust difficulty based on running score. It MUST cover vocabulary recognition, grammar comprehension, and reading. The test SHALL produce a calibrated CEFR estimate (A1–C2) with sub-scores. The user MAY skip the test and fall back to their self-assessed level.

#### Scenario: Correct answers increase question difficulty
- GIVEN the user answers a B1-level question correctly
- WHEN the next question is requested
- THEN the next question SHALL be at B2 level

#### Scenario: Test skipped falls back to self-assessment
- GIVEN the user self-assessed as Beginner
- WHEN the placement test is skipped
- THEN the SkillProfile SHALL use the Beginner CEFR estimate

### Requirement: Curriculum MUST be an ordered sequence of skill units

A curriculum SHALL be a sequence of skill units ordered by prerequisites. Each unit SHALL target specific vocabulary groups and grammar points. Units SHALL include: title, description, vocabulary count, grammar points, CEFR level, and status (locked/unlocked/completed).

#### Scenario: Next unit unlocks when previous unit completes
- GIVEN the user completes unit "Greetings & Introductions"
- WHEN the curriculum is queried
- THEN unit "Family & Friends" SHALL transition from locked to unlocked

### Requirement: Curriculum generation MUST use skill gaps and user interests

Units SHALL be selected based on: known vs. unknown vocabulary (spaced repetition), grammar gaps identified in placement test or ongoing performance, and content matching user interests from onboarding. The curriculum SHALL recalibrate as the user progresses.

#### Scenario: User interests influence unit content selection
- GIVEN a user interested in "Music" and "Technology"
- WHEN curriculum units are generated
- THEN content for exercises SHALL prioritise music and technology topics

### Requirement: Curriculum context MUST respond to LessonCompleted events

When the lesson context publishes a `LessonCompleted` event, the curriculum context SHALL update unit progress and recalculate the user's SkillProfile. If proficiency in a unit's vocabulary exceeds the mastery threshold, the unit SHALL be marked complete.

#### Scenario: Lesson completion updates curriculum progress
- GIVEN a lesson covering "Greetings & Introductions" vocabulary is completed
- WHEN the `LessonCompleted` event is received
- THEN the curriculum SHALL update unit progress for that unit
