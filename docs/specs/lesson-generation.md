# Spec: Lesson Generation & Exercise Types

**Status**: draft
**Bounded contexts**: lesson
**Issues**: [#45](https://github.com/guilherme-andrade/polyglot-ai/issues/45), [#46](https://github.com/guilherme-andrade/polyglot-ai/issues/46), [#47](https://github.com/guilherme-andrade/polyglot-ai/issues/47)

## Overview

Generate a daily lesson from the curriculum's next unit, using content that matches
the user's interests and current CEFR level. Each lesson has 5–10 exercises
covering vocabulary and grammar from that content.

## Daily lesson generation (#45)

### Trigger
- User opens the Learn tab and no lesson exists for today
- Generated fresh each day (one per user per day in v1)

### Lesson structure
```
Lesson
├── Metadata: topic, CEFR level, source content title
├── Warm-up: 1–2 vocabulary recognition exercises
├── Core: 3–5 exercises mixing vocabulary + grammar
└── Challenge: 1–2 harder exercises (next CEFR level)
```

### Generation pipeline
1. `curriculum.getNextUnit(userId)` → current unit with target vocabulary + grammar
2. `content.match(language, cefr, topics)` → content items for exercise material
3. Build exercises from content items targeting unit vocabulary/grammar
4. Order by difficulty (easy → hard)
5. Return lesson with exercises to client

### Server
- `GET /api/lessons/today` → returns today's lesson or 404 (not yet generated)
- `POST /api/lessons/generate` → generates and persists today's lesson (idempotent — no duplicate per day)

## Multiple-choice exercise (#46)

### Variants
| Type | Prompt | Options |
|------|--------|---------|
| Translation | See word in TL, pick native equivalent | 1 correct + 3 distractors |
| Definition | See word in TL, pick definition in TL | 1 correct + 3 distractors |
| Fill context | Sentence with blank, pick the right word | 1 correct + 3 distractors |

### Distractor generation
- Server generates distractors from related vocabulary (same unit, similar CEFR level)
- Distractors must be plausible but clearly wrong to a learner at that level

### UI
- Question stem at top
- 4 tappable options (MultipleChoiceTile component)
- On select: show correct (green pulse) / incorrect (red shake with correct answer shown)
- Auto-advance after 1.5s or tap to continue

## Fill-in-the-blank exercise (#47)

### Variants
| Type | Prompt | Input |
|------|--------|-------|
| Vocabulary | Sentence with target word blanked | Type the missing word |
| Grammar | Sentence with conjugation/declension blanked | Type the correct form |
| Listening | Hear audio, type what you hear | Type the phrase (deferred to post-v1) |

### Typo tolerance
- Accept answers within Levenshtein distance 1 for words > 5 chars
- Accept answers within Levenshtein distance 0 for words ≤ 5 chars (exact match)
- Show correct answer with character diff if wrong

### UI
- ExercisePrompt component: sentence with inline blank
- TextInput for typing the answer
- Submit button or keyboard return
- Feedback: correct (green highlight) / incorrect (red, show correct + diff)

## Data model

```java
// Simplified — full model in ADR-0007
@Entity
public class Lesson {
    UUID id;
    UUID userId;
    LocalDate date;
    String unitId;
    String sourceContentTitle;
    List<Exercise> exercises;
    LessonStatus status; // GENERATED, IN_PROGRESS, COMPLETED
}

@Entity
public class Exercise {
    UUID id;
    ExerciseType type; // MULTIPLE_CHOICE, FILL_IN_BLANK
    String prompt;
    String correctAnswer;
    List<String> options; // for multiple choice
    String contextSentence; // from source content
}
```

## Acceptance criteria

- [ ] One lesson generated per user per day (#45)
- [ ] Lesson pulls from curriculum's next unit
- [ ] Exercises built from content matching user's interests
- [ ] Lesson difficulty calibrated to user's CEFR level
- [ ] 5–10 exercises per lesson covering vocabulary + grammar
- [ ] Multiple-choice: 3 variants (translation, definition, fill context) (#46)
- [ ] Multiple-choice: plausible distractors from related vocabulary
- [ ] Multiple-choice: visual feedback on selection (green/red)
- [ ] Fill-in-the-blank: 2 variants (vocabulary, grammar) (#47)
- [ ] Fill-in-the-blank: typo tolerance (Levenshtein ≤1)
- [ ] Fill-in-the-blank: correct answer with diff shown on error
- [ ] GraphQL: query today's lesson, submit exercise answers

## Out of scope

- Listening exercises (post-v1)
- Speaking/pronunciation exercises (post-v1)
- User-adjusted lesson difficulty
- Multiple lessons per day (M3 feature)
