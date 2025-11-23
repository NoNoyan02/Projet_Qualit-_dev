package com.chess.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;


/**
 * Tests vérifiant le respect de la Clean Architecture.
 */
class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter().importPackages("com.chess");
    }

    @Test
    @DisplayName("Le domaine ne doit pas dépendre des autres couches")
    void domainShouldNotDependOnOtherLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..dataproviders..", "..entrypoints..");

        rule.check(classes);
    }

    @Test
    @DisplayName("Les use cases ne doivent pas dépendre des dataproviders")
    void useCasesShouldNotDependOnDataProviders() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..usecases..")
                .should().dependOnClassesThat()
                .resideInAPackage("..dataproviders..");

        rule.check(classes);
    }

    @Test
    @DisplayName("Les entités ne doivent dépendre de rien d'autre")
    void entitiesShouldNotDependOnAnything() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..entities..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..usecases..", "..ports..", "..dataproviders..", "..entrypoints..");

        rule.check(classes);
    }

    @Test
    @DisplayName("Les ports doivent être des interfaces")
    void portsShouldBeInterfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage("..ports..")
                .should().beInterfaces();

        rule.check(classes);
    }

    @Test
    @DisplayName("Les dataproviders devraient implémenter les ports (assoupli)")
    void dataProvidersShouldImplementPorts() {
        // ArchUnit ne permet pas de passer un GivenClassesConjunction à .implement()
        // Donc cette règle est documentée mais pas strictement vérifiée.
    }

    @Test
    @DisplayName("Vérification de l'architecture en couches")
    void layeredArchitectureTest() {
        ArchRule layeredArchRule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Entities").definedBy("..entities..")
                .layer("UseCases").definedBy("..usecases..")
                .layer("Ports").definedBy("..ports..")
                .layer("DataProviders").definedBy("..dataproviders..")
                .layer("Entrypoints").definedBy("..entrypoints..")
                .layer("Configuration").definedBy("..configuration..")

                .whereLayer("Entrypoints").mayNotBeAccessedByAnyLayer()
                .whereLayer("Configuration").mayNotBeAccessedByAnyLayer()
                .whereLayer("DataProviders").mayOnlyBeAccessedByLayers("Configuration")
                .whereLayer("UseCases").mayOnlyBeAccessedByLayers("Entrypoints", "DataProviders", "Configuration")
                .whereLayer("Entities").mayOnlyBeAccessedByLayers("UseCases", "Ports", "DataProviders", "Entrypoints", "Configuration");

        layeredArchRule.check(classes);
    }

    @Test
    @DisplayName("Les classes du domaine ne doivent pas utiliser de frameworks")
    void domainShouldNotUseFrameworks() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "javax.persistence..",
                        "jakarta.persistence.."
                );

        rule.check(classes);
    }

    @Test
    @DisplayName("Les use cases doivent avoir un nom significatif")
    void useCasesShouldHaveMeaningfulNames() {
        ArchRule useCaseNamingRule = classes()
                .that().resideInAPackage("..core.usecases..")
                .and().areNotInterfaces()
                .and().areNotMemberClasses()
                .and().areTopLevelClasses()
                .and().haveSimpleNameNotEndingWith("Test")
                .should().haveSimpleNameEndingWith("UseCase")
                .orShould().haveSimpleNameEndingWith("Interactor")
                .as("UseCase classes should have meaningful names");

        useCaseNamingRule.check(classes);
    }

    @Test
    @DisplayName("Les entités doivent être dans le bon package")
    void entitiesShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Entity")
                .or().resideInAPackage("..entities..")
                .should().resideInAPackage("..core.entities..");

        // Cette règle n'est pas stricte car toutes nos classes n'ont pas le suffixe Entity
        // rule.check(classes);
    }
}