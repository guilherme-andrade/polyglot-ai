# Lesson Generation & Exercise Types

## Purpose

One lesson SHALL be generated per user per day from the curriculum's next unit, using content that matches the user's interests and CEFR level. Each lesson MUST include 5–10 exercises mixing vocabulary and grammar. Two exercise types SHALL be supported: multiple-choice and fill-in-the-blank.

## Requirements

### Requirement: One lesson MUST be generated per user per day

The lesson engine SHALL generate a lesson when the user opens the Learn tab and no lesson exists for today. Generation is idempotent — no duplicate per day. The lesson SHALL pull from the curriculum's next unit and use content matching the user's interests.

#### Scenario: Lesson is generated on first access
- GIVEN no lesson exists for today
- WHEN `POST /api/lessons/generate` is called
- THEN a lesson SHALL be created with 5–10 exercises
- AND the lesson SHALL reference the curriculum's next unlocked unit

#### Scenario: Second generation attempt is idempotent
- GIVEN a lesson already exists for today
- WHEN `POST /api/lessons/generate` is called again
- THEN the existing lesson SHALL be returned
- AND no duplicate SHALL be created

### Requirement: Lesson MUST be structured warm-up → core → challenge

Each lesson SHALL have a warm-up section (1–2 vocabulary recognition exercises), a core section (3–5 exercises mixing vocabulary + grammar), and a challenge section (1–2 harder exercises at the next CEFR level).

#### Scenario: Lesson structure follows three-part format
- GIVEN a lesson is generated for an A2 user
- WHEN the exercises are ordered
- THEN the first exercises SHALL be A1/A2 vocabulary recognition
- AND the last exercises SHALL target B1 level

### Requirement: Multiple-choice exercise MUST support 3 variants

Multiple-choice exercises SHALL support: translation (see word in target language, pick native equivalent), definition (see word in target language, pick definition in target language), and fill-context (sentence with blank, pick the right word). Each question MUST have 1 correct answer and 3 plausible distractors.

#### Scenario: Correct answer shows green feedback
- GIVEN a multiple-choice exercise is displayed
- WHEN the user taps the correct option
- THEN the option SHALL pulse green
- AND the exercise SHALL auto-advance after 1.5 seconds

#### Scenario: Incorrect answer shows red feedback with correct answer
- GIVEN a multiple-choice exercise is displayed
- WHEN the user taps an incorrect option
- THEN the tapped option SHALL shake red
- AND the correct option SHALL be highlighted green

### Requirement: Fill-in-the-blank exercise MUST be typo-tolerant

For words longer than 5 characters, answers within Levenshtein distance 1 SHALL be accepted. For words of 5 or fewer characters, exact match is required. Incorrect answers SHALL show the correct answer with a character diff.

#### Scenario: Minor typo in long word is accepted
- GIVEN the correct answer is "cocinar" (7 chars)
- WHEN the user types "cozinar" (Levenshtein distance 1)
- THEN the answer SHALL be accepted as correct

#### Scenario: Exact match required for short words
- GIVEN the correct answer is "el" (2 chars)
- WHEN the user types "la"
- THEN the answer SHALL be marked incorrect
- AND the correct answer SHALL be shown

### Requirement: Distractors MUST be plausible but clearly wrong

The server SHALL generate distractors from related vocabulary (same unit, similar CEFR level). Distractors MUST be plausible at a glance but clearly wrong to a learner at that level.

#### Scenario: Distractors come from same vocabulary unit
- GIVEN a multiple-choice question testing the word "cocinar" (A1, cooking unit)
- WHEN distractors are generated
- THEN they SHALL come from the same A1 cooking vocabulary unit
- AND SHALL NOT be from unrelated units or CEFR levels
