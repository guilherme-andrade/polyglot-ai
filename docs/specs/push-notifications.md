# Push Notifications

## Purpose

Daily push notifications SHALL remind users to complete their lesson. Messages MUST be motivational, reference the user's streak, and respect notification permissions. Notifications SHALL be configurable per type (reminder, streak-at-risk, milestone). The system SHALL NOT spam — notifications stop when the daily goal is met.

## Requirements

### Requirement: Daily reminder MUST be sent at the user's preferred time

A daily reminder notification SHALL be sent at the user's preferred time (default 9am local). It SHALL only fire if the daily goal has not been met. An evening reminder SHALL fire at 7pm if the goal is still not met. Both times SHALL be configurable.

#### Scenario: Morning reminder fires when goal not met
- GIVEN the user's reminder time is 9am and they haven't completed a lesson
- WHEN 9am arrives in the user's timezone
- THEN a notification SHALL be delivered: "Time to learn! Your daily Spanish lesson is waiting."

#### Scenario: No reminder when goal already met
- GIVEN the user completed their daily goal at 8:30am
- WHEN the 9am reminder is scheduled
- THEN no notification SHALL be sent

### Requirement: Streak-at-risk notification MUST fire at 8pm

If the user has not completed a lesson by 8pm, a streak-at-risk notification SHALL fire referencing the current streak: "Don't lose your 12-day streak!" This SHALL be configurable (on/off toggle).

#### Scenario: Streak-at-risk notification fires
- GIVEN the user has a 12-day streak and has not completed a lesson today
- WHEN 8pm arrives
- THEN a notification SHALL be sent: "Don't lose your streak! You have 4 hours left."

### Requirement: Milestone celebration SHALL fire on streak thresholds

When a streak milestone is reached (7, 30, 100 days), a celebratory notification SHALL be sent. This SHALL be configurable (on/off toggle).

#### Scenario: 7-day milestone notification
- GIVEN the user completes a lesson and reaches a 7-day streak
- WHEN the streak increments
- THEN a notification SHALL be sent: "7-day streak! You're on fire!"

### Requirement: Notification preferences MUST be granular

The user SHALL toggle each notification type independently: daily reminder (on/off), evening reminder (on/off), streak-at-risk (on/off), and milestone celebrations (on/off). Reminder time SHALL be a time picker. All settings SHALL be in Profile → Settings → Notifications.

#### Scenario: User disables all notifications
- GIVEN the user toggles all notification types off
- WHEN the next notification event occurs
- THEN no notification SHALL be delivered

### Requirement: Notifications MUST use Expo Notifications API

Notifications SHALL use the Expo Notifications API for local scheduling and remote push. The device push token SHALL be registered with the server on login. Tapping a notification SHALL deep-link to the Learn tab.

#### Scenario: Tapping notification opens Learn tab
- GIVEN a daily reminder notification is received
- WHEN the user taps it
- THEN the app SHALL open to the Learn tab
