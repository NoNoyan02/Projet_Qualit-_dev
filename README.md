# ♟️ chess-qualitedev

**2025 - Projet Groupe 207 - Qualité & Développement 2025 (R3.04)**

Ce projet montre comment les principes de **Clean Architecture** et **SOLID** peuvent être appliqués pour développer un jeu d’échecs en Java.

## 📑 Table des matières

- [Architecture](#-architecture)
- [Installation](#%EF%B8%8F-installation)
- [Technologies](#-technologies)
- [Équipe](#-équipe)

## 🏛️ Pourquoi Clean Architecture ?

### Problèmes évités

- Logique métier dispersée partout
- Changements qui cassent tout
- Tests lents et fragiles
- Dette technique qui s'accumule

### Solution

La Clean Architecture centre l'application sur les **règles métier** :

- Domaine isolé et testable
- Changement de framework sans impact
- Tests unitaires rapides
- Code évolutif

## 🧱 Architecture

```
chess-qualitedev/
│ ( à modifié)
```

### Core : Entités

Représentent le domaine du jeu d'échecs :

- `Piece`
  - `King`, `Queen`, `Rook`, `Bishop`, `Knight`, `Pawn`
- `Board`
- `Move`
- `Player`

### Core : Use Cases

Représentent les actions du jeu :

- calcul des mouvements possibles
- vérifier l'état du roi (échec / mat)
- appliquer un coup
- promotion du pion
- roque, prise en passant

Ils contiennent uniquement la **logique métier** et communiquent via des **interfaces** vers les dataproviders.

### Dataproviders

Responsables de la persistance :

- chargement/sauvegarde de parties
- stockage fichiers ou base de données
- mapping des objets métiers → stockage

Découplés grâce à des **interfaces** définies par les use cases.

### Entrypoints

Mécanismes d'interaction avec l'application :

- interface console
- interface web (REST, HTML)

Ils passent par les **use cases** et ne contiennent **aucune logique métier**.

## ⚙️ Installation

### Prérequis

- Java 17+
- Maven 3.8+

### Compilation

```bash
mvn clean install
```

### Exécution console

```bash
java -jar target/chess-qualitedev.jar
```

### Exécution web

```bash
mvn spring-boot:run
```

Puis : `http://localhost:8080`

### Stockfish (moteur d’échecs)

- La dépendance Java io.github.guillaumcn:chess-stockfish:1.0.3 est utilisée.
- Aucun besoin d’installer Stockfish sur Windows, le moteur est inclus dans le jar.
- L’API Java permet de lancer Stockfish 17.1, calculer les coups et lire les résultats directement depuis ton code.

## 💻 Technologies

### Core

- **Java 17** - Langage principal
- **JUnit 5** - Tests unitaires
- **Mockito, ArchUnit, AssertJ** - Tests et validation architecture

### Persistance
- **JSON** 

### Logging
- SLF4J + Logback

### Web

- **Spring Boot** - Framework web
- **Thymeleaf** - Templates HTML

### Persistance

- **Fichiers JSON/CSV** - Sauvegarde simple
- Ou base de données selon besoins

## ♟️ Règles implémentées

### Règles de base

- Plateau 8×8
- 6 types de pièces
- Mouvements spécifiques
- Alternance des tours

### Règles avancées

- Échec & mat
- Roque
- Promotion du pion
- Prise en passant

## 📚 Ressources

### Architecture

- [Clean Architecture - Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [SOLID Principles](http://butunclebob.com/ArticleS.UncleBob.PrinciplesOfOod)

### Testing

- [Testing Pyramid - Martin Fowler](https://martinfowler.com/bliki/TestPyramid.html)
- [Refactoring Catalog](https://refactoring.com/catalog/)

### Échecs

- [FIDE - Règles officielles](https://www.fide.com/)
- [Chess Rules - Wikipedia](https://en.wikipedia.org/wiki/Rules_of_chess)

## 👥 Équipe

**Groupe 207**

- TAYLAN Noyan
- SE Donald
- SIVASEKARAN Aswin
- SERIEYS Dorian

**Encadrant** : Mikal Ziane - R3.04

## 📝 Statut

- [ ] Architecture définie
- [ ] Core implémenté
- [ ] Tests unitaires
- [ ] Interface console
- [ ] Interface web
- [ ] Documentation complète

_Développé avec TAYLAN Noyan - SE Donald - SIVASEKARAN Aswin - SERIEYS Dorian par le Groupe 207 - R3.04 Qualité de Développement 2025_
