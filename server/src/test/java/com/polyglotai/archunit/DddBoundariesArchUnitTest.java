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
 * <p>This is intentionally minimal — the infrastructure is in place so that per-feature PRs
 * can add stricter rules (e.g. controllers may not return domain types) without having to
 * stand up ArchUnit from scratch.
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

    /** Layered architecture per context: domain ← application ← infrastructure/interfaces. */
    @ArchTest
    static final ArchRule layered_architecture_per_context = Architectures.layeredArchitecture()
            .consideringAllDependencies()
            .withOptionalLayers(true)
            .layer("Domain")
            .definedBy("..domain..")
            .layer("Application")
            .definedBy("..application..")
            .layer("Infrastructure")
            .definedBy("..infrastructure..")
            .layer("Interfaces")
            .definedBy("..interfaces..")
            .whereLayer("Interfaces")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Infrastructure")
            .mayOnlyBeAccessedByLayers("Interfaces")
            .whereLayer("Application")
            .mayOnlyBeAccessedByLayers("Infrastructure", "Interfaces")
            .whereLayer("Domain")
            .mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Interfaces");

    /** Contexts must not form dependency cycles. Tighter cross-context rules come per spec. */
    @ArchTest
    static final ArchRule contexts_are_free_of_cycles =
            SlicesRuleDefinition.slices().matching(ROOT + ".(*)..").should().beFreeOfCycles();
}
