# Spec: Curriculum (Placement Test + Generation)

**Status**: draft
**Bounded contexts**: curriculum
**Issues**: [#43](https://github.com/guilherme-andrade/polyglot-ai/issues/43), [#44](https://github.com/guilherme-andrade/polyglot-ai/issues/44)

## Overview

The curriculum context owns skill assessment and personalized learning path
generation. It takes a user's self-assessed level (from onboarding), optionally
refines it with a placement test, and generates an adaptive curriculum that the
lesson engine draws from.

## Placement test (#43)

### Design
- Adaptive test: 10–20 questions, difficulty adjusts based on running score
- Covers: vocabulary recognition (multiple choice), grammar comprehension (fill-in-gap), reading (short passage + questions)
- Produces calibrated CEFR estimate (A1–C2) with sub-scores: vocabulary, grammar, reading
- Test can be skipped (falls back to self-assessed level from onboarding)
- Results stored as user's baseline `SkillProfile` in the curriculum context

### Flow
1. Start at self-assessed level
2. Each correct answer → next question slightly harder
3. Each incorrect answer → next question slightly easier
4. Converge after 10–20 questions
5. Display result: "You're at A2 level! Here's your starting curriculum."

### Server
- `POST /api/curriculum/placement-test/start` → returns first question
- `POST /api/curriculum/placement-test/answer` → returns next question or result
- Question bank: generated/curated set of questions per level per language

## Curriculum generation (#44)

### Model
A curriculum is an ordered sequence of skill units, each targeting specific
vocabulary groups and grammar points, with prerequisites between units.

```
Curriculum
├── Unit: Greetings & Introductions (A1)
│   ├── Vocabulary: hello, goodbye, my name is, please, thank you
│   └── Grammar: subject pronouns, verb "to be" present tense
├── Unit: Family & Friends (A1)
│   ├── Vocabulary: mother, father, friend, brother, sister
│   └── Grammar: possessive adjectives, present simple
├── ...
└── Unit: Abstract Discussion (B2)
    ├── Vocabulary: hypothesis, implication, conversely, nevertheless
    └── Grammar: subjunctive mood, passive constructions
```

### Generation algorithm
1. Start from user's CEFR baseline
2. Select units that:
   - Match the user's known vs. unknown vocabulary (spaced repetition)
   - Target grammar gaps identified in placement test or ongoing performance
   - Use content matching the user's interests (from onboarding)
3. Order by prerequisite chain
4. Recalibrate as user progresses (not static)

### Server
- `GET /api/curriculum` → returns current curriculum with unit list and progress
- Each unit has: id, title, description, vocabulary count, grammar points, CEFR level, status (locked/unlocked/completed)
- Curriculum generated on first access after onboarding; regenerated when skill profile changes significantly

### Contracts

| From | To | Contract |
|------|----|----------|
| curriculum | content | Requests content matching unit's vocabulary + user interests |
| curriculum | lesson | `CurriculumService.getNextUnit(userId)` → UnitDTO (called by lesson generator) |
| lesson | curriculum | `LessonCompleted` event → update unit progress, recalculate SkillProfile |

## Acceptance criteria

- [ ] Placement test: adaptive 10–20 questions (#43)
- [ ] Placement test: produces CEFR estimate with sub-scores
- [ ] Placement test: can be skipped (falls back to self-assessment)
- [ ] Placement test: results stored as SkillProfile baseline
- [ ] Curriculum: ordered sequence of skill units with prerequisites (#44)
- [ ] Curriculum: units selected based on skill gaps + user interests
- [ ] Curriculum: adapts as user progresses (units unlock, difficulty adjusts)
- [ ] Unit content: vocabulary list + grammar points per unit
- [ ] GraphQL API: query curriculum with unit progress

## Out of scope

- Multi-language curriculum (user learns one language at a time in v1)
- User-created custom units
- Offline curriculum access (requires server for generation)
