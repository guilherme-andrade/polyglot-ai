# Content Catalog

## Purpose

Build and seed a catalog of source content (videos, articles, song lyrics, etc.) in all supported languages. The system MUST extract vocabulary items, grammar patterns, and difficulty metadata from each content item so the lesson engine can match content to learners. Only public domain or Creative Commons content SHALL be used.

## Requirements

### Requirement: Content metadata MUST be stored in PostgreSQL

Each content item SHALL be stored with: id, title, language (ISO 639-1), type (VIDEO, ARTICLE, SONG_LYRICS, PODCAST_TRANSCRIPT), difficulty (CEFR A1–C2), topics (set of strings), source URL, license, and full text content. Items MUST be queryable by language + topic(s) + difficulty range.

#### Scenario: Query returns content matching filters
- GIVEN the catalog has Spanish music content at A2 level
- WHEN `GET /api/content/match?language=es&cefr=A2&topics=music` is called
- THEN the response SHALL include Spanish music items at A2 difficulty
- AND SHALL NOT include French content or B1 content

### Requirement: Seed data MUST cover 3+ content types in all 6 supported languages

The initial seed SHALL include at least 3 content types and at least 3 items per language (English, Spanish, French, German, Portuguese, Italian). All content MUST be public domain or Creative Commons licensed.

#### Scenario: Seed data is queryable after migration
- GIVEN Flyway migrations have run
- WHEN the content catalog is queried for German articles
- THEN at least 3 German content items SHALL be returned

### Requirement: Vocabulary extraction MUST produce structured data

For each content item, the pipeline SHALL tokenise and lemmatise the text, then extract vocabulary items with: word/phrase, lemma, part of speech, frequency in this content, estimated CEFR level, and context sentence. Results SHALL be stored in the `content_vocabulary` table.

#### Scenario: Vocabulary extraction runs on a Spanish article
- GIVEN a Spanish article about cooking
- WHEN the extraction pipeline runs
- THEN vocabulary items like "cocinar" (lemma: cocinar, POS: verb, CEFR: A1) SHALL be produced
- AND each item SHALL include the sentence it appeared in

### Requirement: Grammar pattern extraction MUST identify reusable patterns

The pipeline SHALL identify grammar patterns: verb tenses/conjugations, sentence structures (simple, compound, complex), and common collocations. Results SHALL be stored in the `content_grammar` table with pattern type, value, and example sentence.

#### Scenario: Grammar extraction identifies present tense pattern
- GIVEN a French article with multiple present tense verbs
- WHEN the extraction pipeline runs
- THEN grammar patterns for "présent de l'indicatif" SHALL be produced
- AND each pattern SHALL include an example sentence

### Requirement: pgvector columns MUST be present for semantic search

Vocabulary and grammar tables SHALL include pgvector columns for embedding storage. This enables future semantic search over content. The vector columns SHALL be added via Flyway migration.

#### Scenario: pgvector extension is available
- GIVEN the database is initialised
- WHEN `SELECT vector(3, ARRAY[1,2,3])` is executed
- THEN it SHALL return without error
