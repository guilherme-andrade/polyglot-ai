# Spec: Content Catalog

**Status**: draft
**Bounded contexts**: content
**Issues**: [#41](https://github.com/guilherme-andrade/polyglot-ai/issues/41), [#42](https://github.com/guilherme-andrade/polyglot-ai/issues/42)

## Overview

Build and seed a catalog of source content (videos, articles, song lyrics, etc.)
in supported languages. Extract vocabulary, grammar patterns, and difficulty
metadata from each content item so the lesson engine can match content to learners.

## Content metadata schema (#41)

Each content item stored in PostgreSQL (`content` context):

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Primary key |
| title | String | Display title |
| language | String | ISO 639-1 code |
| type | ContentType enum | VIDEO, ARTICLE, SONG_LYRICS, PODCAST_TRANSCRIPT |
| difficulty | CefrLevel | A1–C2 |
| topics | Set\<String\> | Music, sports, tech, etc. |
| source_url | String | Original URL |
| license | String | Public domain, CC, etc. |
| text_content | Text | Full transcript / lyrics / article text |

### Seed data requirements
- At least 3 content types represented
- At least 3 items per supported language (English, Spanish, French, German, Portuguese, Italian)
- Public domain or Creative Commons content only
- Queryable by: language + topic(s) + difficulty range

## Vocabulary and grammar extraction (#42)

### Pipeline
1. Tokenize + lemmatize text in target language
2. Extract vocabulary items:
   - Word/phrase, lemma, part of speech
   - Frequency in this content
   - Estimated CEFR level (based on word frequency lists)
   - Context sentence (the sentence it appeared in)
3. Identify grammar patterns:
   - Verb tenses and conjugations
   - Sentence structures (simple, compound, complex)
   - Common collocations

### Storage
- Vocabulary items → `content_vocabulary` table (content_id, lemma, pos, cefr_level, context_sentence)
- Grammar patterns → `content_grammar` table (content_id, pattern_type, pattern_value, example_sentence)
- Embeddings → pgvector columns on both tables for semantic search

### Implementation note
Initial extraction can use an LLM-based pipeline (Claude API) triggered
manually or via a simple admin endpoint. Batch processing infra comes later.

## Contracts

### Cross-context

| From | To | Contract |
|------|----|----------|
| content | curriculum | `GET /api/content/match?language=es&cefr=A2&topics=music,sports` → List\<ContentItemDTO\> |
| content | lesson | `GET /api/content/{id}/vocabulary?cefr=A2` → List\<VocabularyItemDTO\> |

## Acceptance criteria

- [ ] Content metadata schema defined and migrated (Flyway)
- [ ] Seed data: 3+ content types, 3+ items per language, 6 languages (#41)
- [ ] Content queryable by language + topic + difficulty
- [ ] Only public domain / CC-licensed content
- [ ] Vocabulary extraction pipeline produces structured data (#42)
- [ ] Grammar pattern extraction produces structured data (#42)
- [ ] Extracted data stored with context sentences
- [ ] pgvector columns present on vocabulary and grammar tables

## Out of scope

- Automated content ingestion (manual seed initially)
- Full-text search (future Elasticsearch)
- User-contributed content
- Content recommendation engine (curriculum handles matching)
