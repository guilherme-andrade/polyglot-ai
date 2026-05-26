# Spec: Component Primitives

**Status**: draft
**Bounded contexts**: app (cross-cutting)
**Issue**: [#29](https://github.com/guilherme-andrade/polyglot-ai/issues/29)
**Depends on**: `design-tokens.md`

## Overview

Build the UI component library on top of design tokens. Every component must be
accessible, typed, and tested. Duolingo-level polish: playful micro-interactions,
satisfying feedback animations, clear visual hierarchy, warm tone.

## Component catalog

### Typography
- **Heading**: h1–h4, each with size/weight/line-height from tokens
- **Body**: regular, small, caption variants
- **Label**: for form labels, badges, metadata
- All accept: `color`, `align`, `numberOfLines`, `accessibilityRole`

### Actions
- **Button**: primary, secondary, ghost, danger; sm/md/lg; loading (spinner); disabled; full-width; icon + label slots; min 44pt touch target; scale-down press animation
- **IconButton**: same variants as Button, with accessible label
- **LinkButton**: inline text-styled tappable
- **FloatingActionButton**: prominent circular button for primary action

### Inputs
- **TextInput**: label, placeholder, error, hint, leading/trailing icons, clear button, character count, secure text toggle
- **SearchInput**: TextInput variant with search icon, debounced onChange, clear
- **Select**: label, options list with checkmark, error, disabled
- **Checkbox**: label, checked/unchecked, indeterminate, error
- **Radio**: radio group with label, description, error
- **Toggle/Switch**: on/off, label, disabled

### Containers
- **Card**: default, elevated, outlined, interactive (tappable with scale); header/body/footer slots; padding variants
- **ScreenWrapper**: SafeAreaView, keyboard-avoiding, scroll or fixed, loading/error/empty overlay slots
- **Modal/BottomSheet**: overlay, backdrop, title, close, scrollable content, snap points (25%/50%/90%)
- **Section**: grouped content with optional header and footer

### Feedback
- **Toast/Snackbar**: success, error, info, warning; auto-dismiss; action slot; slide-in from top
- **Alert**: inline banner; icon + title + description + action; dismissible
- **EmptyState**: illustration, title, description, action button
- **ErrorState**: icon, title, description, retry action
- **SkeletonLoader**: shimmer; variants for text, card, circle; matches real component shapes
- **ConfettiOverlay**: celebration animation for milestones

### Navigation
- **TabBar**: bottom tabs, icon + label, active/inactive color transition, badge slot, min 44pt
- **TopBar/Header**: title, back button, trailing actions (max 2); collapsible deferred
- **Stepper/ProgressDots**: onboarding step indicator; current/complete/upcoming states; animated

### Progress & Loading
- **ProgressBar**: determinate (0–100%) and indeterminate; color variants; animated fill
- **ProgressRing**: circular; determinate and indeterminate; size variants; animated
- **ActivityIndicator**: spinner; sm/md/lg
- **StreakCounter**: flame icon + count; animated increment; frozen/saved state

### Lists
- **ListItem**: leading icon/avatar, title, subtitle, trailing element (chevron, badge, toggle, XP); tappable with highlight; swipe actions deferred
- **SectionList**: grouped with sticky headers; collapsible

### Exercise-specific
- **WordBank**: selectable word tiles for sentence construction; tap to select/deselect; shake on incorrect
- **MultipleChoiceTile**: tappable card; selected state; correct (green pulse) / incorrect (red shake)
- **AudioButton**: play/pause with progress ring; waveform animation; speed control
- **ExercisePrompt**: rendered text with blank slots for fill-in-the-blank
- **CorrectAnswerOverlay**: green banner; checkmark + correct answer + brief explanation
- **IncorrectAnswerOverlay**: red banner; correct answer shown; encouragement text
- **Flashcard**: front/back flip animation; tap to reveal; swipe left/right for "still learning"/"got it"

### Gamification
- **XPCounter**: pill with XP total; animated increment; "+N XP" popup; particle burst on level-up
- **AchievementBadge**: circular badge; locked (grey) / unlocked (color + glow); unlock animation
- **LeaderboardRow**: rank, avatar, name, XP; current-user highlight
- **DailyQuestCard**: quest title, icon, progress bar, XP reward; complete state

### Misc
- **Badge/Chip**: label, icon, color variants; dismissible; scale-down on press
- **Tag**: shorter chip without icon; outlined or filled
- **Avatar**: image with initials fallback; sm/md/lg/xl
- **Divider**: horizontal and vertical; with/without label
- **LanguageFlag**: circular flag + 2-letter language code overlay
- **LevelBadge**: CEFR level indicator (A1–C2); colored bar + label
- **Heart/Lives**: row of heart icons; filled/empty; shake on loss; refill animation

### Motion & Micro-interactions
- Consistent spring animation preset (gentle, medium, bouncy)
- Press: scale to 0.97 on touch down, spring back on release
- List entrance: staggered fade+slide (subtle)
- Haptic feedback on: correct answer, incorrect answer, button press, milestone

## Cross-cutting requirements

- All components: strict TypeScript props, accessibility labels/roles/hints, min 44pt touch targets
- Unit tests: render + interaction (React Native Testing Library)
- Follow NativeWind conventions; accept `className` override
- Support light mode; dark mode prepared (semantic tokens, no hardcoded colors)
- RTL support: components mirror layout when locale is RTL (post-v1, but don't block it)

## Acceptance criteria

- [ ] All Typography components implemented and tested
- [ ] All Action components (Button, IconButton, LinkButton, FAB) implemented and tested
- [ ] All Input components implemented and tested
- [ ] All Container components implemented and tested
- [ ] All Feedback components implemented and tested
- [ ] All Navigation components implemented and tested
- [ ] All Progress & Loading components implemented and tested
- [ ] All List components implemented and tested
- [ ] All Exercise-specific components implemented and tested
- [ ] All Gamification components implemented and tested
- [ ] All Misc components implemented and tested
- [ ] Motion presets defined and documented
- [ ] Storybook or equivalent set up for isolated development
- [ ] Every interactive component has minimum 44pt touch target
- [ ] Every component has accessibility labels on interactive elements

## Out of scope

- Swipe actions on ListItem (post-v1)
- Collapsible TopBar (post-v1)
- Dark mode (post-v1, but semantic tokens ready)
- RTL language support (post-v1, but layout mirroring prepared)
- Visual regression tests (post-v1)
