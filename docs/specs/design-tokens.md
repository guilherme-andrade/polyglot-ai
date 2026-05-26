# Design Tokens

## Purpose

Define the design token system that underpins all UI components. Tokens MUST be the single source of truth for colors, typography, spacing, border radius, and shadows. No component SHALL use hardcoded visual values — every value MUST reference a design token.

## Requirements

### Requirement: Color palette MUST be defined as semantic tokens

A color palette SHALL be defined with brand, semantic (success, error, warning, info), and neutral scales. Colors MUST be exported to the NativeWind theme. Dark mode slots SHALL be reserved via semantic token naming even though dark mode implementation is deferred.

#### Scenario: Component references a semantic color
- GIVEN a Button component needs a primary color
- WHEN the component is styled
- THEN it SHALL reference `colors.brand.primary` from the design token system
- AND it SHALL NOT use a hardcoded hex value like `#58CC02`

### Requirement: Typography scale MUST cover h1 through caption

A typography scale SHALL define tokens from h1 (28px extrabold) through caption (12px regular), each with size, weight, and line height. Font family SHALL default to the system font (San Francisco on iOS, Roboto on Android).

#### Scenario: Heading uses typography token
- GIVEN a screen needs an h2 heading
- WHEN the Heading component renders
- THEN it SHALL apply the h2 typography token (22px, bold, 28px line height)

### Requirement: Spacing MUST use a 4px base grid

All spacing SHALL derive from a 4px base grid: 4, 8, 12, 16, 20, 24, 32, 40, 48, 56, 64. The NativeWind theme SHALL be extended with these spacing values.

#### Scenario: Component padding uses spacing token
- GIVEN a Card component needs standard padding
- WHEN the padding is applied
- THEN it SHALL use `p-4` (16px) from the spacing scale
- AND it SHALL NOT use an arbitrary value like `p-[15px]`

### Requirement: Border radius and shadow tokens MUST be defined

Border radius tokens SHALL include: 4, 8, 12, 16, 24, 9999 (pill). Shadow tokens SHALL include sm, md, lg with both iOS shadow properties and Android elevation.

#### Scenario: Card uses shadow token
- GIVEN a Card with variant "elevated"
- WHEN it renders
- THEN it SHALL apply the `md` shadow token
- AND the shadow SHALL render correctly on both iOS and Android

### Requirement: Light mode MUST be complete; dark mode slots MUST be reserved

All tokens SHALL support light mode fully. Semantic token names (e.g. `surface`, `textPrimary`) SHALL be used so dark mode values can be added without renaming.

#### Scenario: Color tokens use semantic names
- GIVEN a background color is needed
- WHEN the token is defined
- THEN it SHALL be named `surface` or `background` rather than `white`
- AND the light mode value SHALL be the light color
