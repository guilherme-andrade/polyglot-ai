# Spec: Profile & Settings

**Status**: draft
**Bounded contexts**: user
**Issues**: [#53](https://github.com/guilherme-andrade/polyglot-ai/issues/53), [#54](https://github.com/guilherme-andrade/polyglot-ai/issues/54)

## Overview

Profile screen for viewing and editing account information, preferences, and
learning settings. Accessible from the Profile tab (3rd tab in bottom nav).

## Profile view (#53)

### Display fields
- Avatar (image or initials fallback)
- Display name
- Email
- Target language (with flag)
- Skill level (CEFR)
- Member since (date)

### Editable fields
- Display name (inline edit)
- Avatar (photo picker or initials fallback)
- Target language (change with warning: "Progress is per-language. Starting a new language creates a separate profile.")
- Email (change requires re-verification)
- Password change (current password + new password + confirm)

## Daily goal setting (#54)

### Located in Profile → Learning Settings
- Default: 1 lesson per day
- Options: 1, 2, 3, or 5 lessons per day
- Goal displayed on Learn tab with progress ring (e.g. "2/3 today")
- Push notification frequency respects the goal (remind until goal met)
- Can be changed anytime

## Server

- `GET /api/user/profile` → profile data
- `PUT /api/user/profile` → update name, avatar
- `PUT /api/user/preferences` → update language, daily goal, notification settings
- `PUT /api/user/change-password` → password change
- `PUT /api/user/change-email` → email change (triggers re-verification)

## UI

- Profile tab: avatar, name, stats summary at top; settings sections below
- Settings sections: Account, Learning, Notifications, About
- Inline editing where possible (tap to edit name)
- Modal/sheet for password change, email change

## Acceptance criteria

- [ ] Profile displays: avatar, name, email, target language, skill level (#53)
- [ ] Display name editable inline
- [ ] Avatar: photo picker + initials fallback
- [ ] Target language changeable with warning
- [ ] Email change with re-verification flow
- [ ] Password change: current + new + confirm
- [ ] Daily goal selector: 1/2/3/5 lessons per day (#54)
- [ ] Daily goal shown on Learn tab with progress ring
- [ ] Goal respects push notification frequency

## Out of scope

- Account deletion (GDPR requirement — add before public launch)
- Data export
- Multiple target languages simultaneously
- Social account linking
