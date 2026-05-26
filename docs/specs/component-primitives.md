# Component Primitives

## Purpose

Build the UI component library on top of design tokens. Every component MUST be accessible (labels, roles, minimum 44pt touch targets), typed with strict TypeScript props, and tested with React Native Testing Library. The library SHALL cover typography, actions, inputs, containers, feedback, navigation, progress indicators, lists, exercise-specific components, and gamification elements.

## Requirements

### Requirement: Typography components MUST use design token scale

Heading (h1–h4), Body (regular, small), Caption, and Label components SHALL apply typography tokens from the design system. Each SHALL accept `color`, `align`, `numberOfLines`, and `accessibilityRole` props.

#### Scenario: h2 renders with correct token
- GIVEN `<Heading variant="h2">Welcome</Heading>`
- WHEN the component renders
- THEN it SHALL apply 22px, bold (700), 28px line height from design tokens

### Requirement: Button MUST have 4 variants, 3 sizes, and loading state

Button SHALL support variants: primary, secondary, ghost, danger. Sizes: sm (36px), md (44px), lg (52px). Loading state SHALL show a spinner and disable interaction. Disabled state SHALL reduce opacity. Minimum touch target SHALL be 44pt. Press feedback SHALL be scale-down to 0.97 with spring return.

#### Scenario: Loading button is not tappable
- GIVEN `<Button loading>Submit</Button>`
- WHEN the user taps it
- THEN no action SHALL fire
- AND a spinner SHALL be visible inside the button

#### Scenario: Primary button uses brand color
- GIVEN `<Button variant="primary">Start Lesson</Button>`
- WHEN it renders
- THEN the background SHALL use the brand primary color token
- AND text SHALL be white

### Requirement: TextInput MUST support label, error, hint, and icon slots

TextInput SHALL render with: label, placeholder, error message (red, below input), hint text, leading icon, trailing icon, clear button, character count, and secure text toggle (for passwords). Error state SHALL show red border and error message.

#### Scenario: Error state shows red border and message
- GIVEN `<TextInput error="Email is required" />`
- WHEN it renders
- THEN the border SHALL be red (semantic error token)
- AND "Email is required" SHALL appear below the input

### Requirement: Feedback components MUST cover all states

Toast/Snackbar SHALL support success, error, info, warning variants with auto-dismiss and action slot. EmptyState SHALL have illustration, title, description, and action button slots. ErrorState SHALL include a retry action. SkeletonLoader SHALL provide shimmer variants for text lines, cards, and circles.

#### Scenario: Toast auto-dismisses after configurable duration
- GIVEN `<Toast variant="success" duration={3000}>Saved!</Toast>`
- WHEN it renders
- THEN it SHALL slide in from the top
- AND SHALL auto-dismiss after 3 seconds

### Requirement: Exercise components MUST provide Duolingo-style feedback

WordBank SHALL render selectable word tiles for sentence construction with tap to select/deselect and shake animation on incorrect. MultipleChoiceTile SHALL show green pulse on correct and red shake on incorrect. ExercisePrompt SHALL render text with inline blank slots for fill-in-the-blank exercises.

#### Scenario: WordBank tile shakes on incorrect submission
- GIVEN the user has selected tiles in wrong order
- WHEN they submit
- THEN the tiles SHALL shake
- AND SHALL return to the word bank area

### Requirement: Gamification components MUST animate

XPCounter SHALL show XP total as a pill with animated increment and "+N XP" popup on earn. AchievementBadge SHALL show locked (grey) and unlocked (color + glow) states with unlock animation. LeaderboardRow SHALL highlight the current user.

#### Scenario: XPCounter shows animated popup on earn
- GIVEN the user earns 45 XP
- WHEN the XPCounter updates
- THEN a "+45 XP" pill SHALL animate upward and fade out
- AND the counter SHALL increment from old to new value

### Requirement: Motion presets MUST be consistent across components

The library SHALL define spring animation presets: gentle, medium, bouncy. Press animations SHALL scale to 0.97. Haptic feedback SHALL fire on correct answer, incorrect answer, button press, and milestone.

#### Scenario: All buttons use the same press animation
- GIVEN any interactive component in the library
- WHEN pressed
- THEN it SHALL scale to 0.97 on touch down
- AND spring back on release

### Requirement: Every interactive component MUST have minimum 44pt touch target

Any tappable element (buttons, list items, checkboxes) SHALL have a minimum 44pt touch target. Accessibility labels, roles, and hints SHALL be set on all interactive elements.

#### Scenario: Small icon button has padded touch area
- GIVEN an IconButton with a 20px icon
- WHEN it renders
- THEN the touchable area SHALL be at least 44pt
- AND it SHALL have an `accessibilityLabel` prop
