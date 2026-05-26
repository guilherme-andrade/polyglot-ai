/**
 * Scaffold-time bootstrap package — <strong>not a bounded context</strong>.
 *
 * <p>Holds wiring that needs to exist for the application to start cleanly before any
 * bounded context has feature code. Every class in this package is temporary: when the
 * relevant feature spec lands, the class either moves into the owning context's
 * {@code interfaces} or {@code infrastructure} layer, or is deleted outright.
 *
 * <p>See {@code docs/specs/server-scaffold.md} for the removal conditions per class.
 *
 * <p>Code added here should:
 *
 * <ul>
 *   <li>document, in its class Javadoc, the exact condition under which it can be removed;
 *   <li>not be imported from any bounded-context package (use ArchUnit to enforce when
 *       the cross-context rules tighten — see issue #22);
 *   <li>be reviewed sceptically — most new wiring belongs in a bounded context, not here.
 * </ul>
 */
package com.polyglotai.bootstrap;
