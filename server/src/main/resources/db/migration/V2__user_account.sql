-- user context: Account aggregate.
-- See docs/specs/auth.md and ADR 0007 (data model strategy).
-- The UNIQUE constraint on email also creates the index used for uniqueness/lookup checks.
CREATE TABLE user_account (
    id            UUID         PRIMARY KEY,
    email         VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    status        VARCHAR(32)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL
);
