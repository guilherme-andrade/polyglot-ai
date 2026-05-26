# Spec: Push Notifications

**Status**: draft
**Bounded contexts**: gamification, user
**Issue**: [#55](https://github.com/guilherme-andrade/polyglot-ai/issues/55)

## Overview

Daily push notification reminding the user to complete their lesson. Motivational
messaging that references their streak. Respects notification permissions and
can be disabled.

## Notification triggers

### Daily reminder
- Sent at user's preferred time (default: 9am local time, configurable in settings)
- Only sent if user has not completed their daily goal yet
- Resent once if goal not met by evening (default: 7pm, configurable)
- Stops when daily goal is met

### Streak at-risk
- If it's 8pm and user hasn't completed a lesson, send "Streak is at risk!" notification
- References current streak count: "Don't lose your 12-day streak!"

### Milestone celebration
- When streak hits a milestone (7, 30, 100 days), send celebratory notification
- Example: "7-day streak! You're on fire!"

## Message templates

| Trigger | Title | Body |
|---------|-------|------|
| Morning reminder | "Time to learn!" | "Your daily {language} lesson is waiting. Keep your {N}-day streak going!" |
| Evening reminder | "Still time to learn" | "Complete today's lesson to keep your {N}-day streak alive!" |
| Streak at-risk | "Don't lose your streak!" | "You have {N} hours left to practice {language} today." |
| Milestone | "{N} day streak!" | "Amazing! You've been learning {language} for {N} days in a row." |
| Goal met | "Daily goal complete!" | "Great work! You completed {N}/{M} lessons today. +{XP} XP earned." |

## Configuration

- Push notification toggle: on/off (global)
- Reminder time picker
- Evening reminder toggle: on/off
- Streak-at-risk toggle: on/off
- Milestone celebration toggle: on/off

All accessible from Profile → Settings → Notifications.

## Technical

- Expo Notifications API for local scheduling + remote push
- Notification token stored server-side (user context)
- Server schedules/triggers notifications based on user timezone and activity
- Notification opened → deep link to Learn tab

## Acceptance criteria

- [ ] Daily reminder sent at user's preferred time
- [ ] Only sent if daily goal not met
- [ ] Evening reminder if goal still not met
- [ ] Streak-at-risk notification at 8pm
- [ ] Milestone celebration notifications
- [ ] All notification types can be toggled independently
- [ ] Notification time configurable
- [ ] Respects device notification permissions
- [ ] Notification opens app to Learn tab
- [ ] Messages reference user's streak and target language

## Out of scope

- Push notification A/B testing
- Notification analytics (open rate, etc.)
- Friend activity notifications
- New content available notifications
