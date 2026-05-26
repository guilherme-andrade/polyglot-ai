package com.polyglotai.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

/**
 * Enforces the DDD boundaries described in {@code docs/architecture.md}.
 *
 * <p>What is enforced today:
 *
 * <ul>
 *   <li>The {@code domain} layer of every context is framework-free.
 *   <li>Each bounded context independently respects the layered architecture
 *       {@code domain ← application ← infrastructure / interfaces}. The rules are emitted
 *       per-context so a class in e.g. {@code user.interfaces} cannot satisfy them by
 *       depending on {@code lesson.infrastructure} — that's still a cross-context
 *       violation even though both packages match {@code interfaces} / {@code infrastructure}
 *       globs.
 *   <li>Context slices are free of cycles.
 * </ul>
 *
 * <p>What is <strong>not</strong> yet enforced: a blanket ban on cross-context dependencies.
 * Per {@code docs/architecture.md}, contexts may communicate through application services or
 * domain events, so the precise rule needs the cross-context contract spec (#22) before it
 * can be added here.
 */
@AnalyzeClasses(
        packages = "com.polyglotai",
        importOptions = {ImportOption.DoNotIncludeTests.class})
class DddBoundariesArchUnitTest {

    private static final String ROOT = "com.polyglotai";

    /** Domain layer of every context must remain framework-free. */
    @ArchTest
    static final ArchRule domain_has_no_framework_dependencies = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..", "jakarta.persistence..", "org.hibernate..", "org.springframework.data..");

    /** Context slices may not form dependency cycles. */
    @ArchTest
    static final ArchRule contexts_are_free_of_cycles =
            SlicesRuleDefinition.slices().matching(ROOT + ".(*)..").should().beFreeOfCycles();

    // ---- per-context layered architecture ------------------------------------------------

    @ArchTest
    static final ArchRule user_layered_architecture = layeredArchitectureFor("user");

    @ArchTest
    static final ArchRule curriculum_layered_architecture = layeredArchitectureFor("curriculum");

    @ArchTest
    static final ArchRule content_layered_architecture = layeredArchitectureFor("content");

    @ArchTest
    static final ArchRule lesson_layered_architecture = layeredArchitectureFor("lesson");

    @ArchTest
    static final ArchRule gamification_layered_architecture = layeredArchitectureFor("gamification");

    @ArchTest
    static final ArchRule analytics_layered_architecture = layeredArchitectureFor("analytics");

    private static ArchRule layeredArchitectureFor(String context) {
        String base = ROOT + "." + context;
        return Architectures.layeredArchitecture()
                .consideringAllDependencies()
                .withOptionalLayers(true)
                .layer("Domain")
                .definedBy(base + ".domain..")
                .layer("Application")
                .definedBy(base + ".application..")
                .layer("Infrastructure")
                .definedBy(base + ".infrastructure..")
                .layer("Interfaces")
                .definedBy(base + ".interfaces..")
                .whereLayer("Interfaces")
                .mayNotBeAccessedByAnyLayer()
                .whereLayer("Infrastructure")
                .mayOnlyBeAccessedByLayers("Interfaces")
                .whereLayer("Application")
                .mayOnlyBeAccessedByLayers("Infrastructure", "Interfaces")
                .whereLayer("Domain")
                .mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Interfaces")
                .as("Layered architecture for the " + context + " context");
    }
}
