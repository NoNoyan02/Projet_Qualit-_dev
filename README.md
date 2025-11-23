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
|   pom.xml                     # Configuration Maven : dépendances, plugins, version Java, build, etc.
|   README.md                    # Documentation du projet, instructions, architecture, équipe
\---src
    +---main
    |   +---java
    |   |   |   ChessGame.java                  # Classe principale pour lancer le jeu
    |   |   |
    |   |   \---com
    |   |       \---chess
    |   |           +---configuration
    |   |           |       AppConfig.java     # Configuration globale du projet (paramètres)
    |   |           |
    |   |           +---core
    |   |           |   +---entities
    |   |           |   |   |   Color.java       # Enum des couleurs des pièces (Blanc/Noir)
    |   |           |   |   |   Position.java    # Représente la position d'une case (ex : e4)
    |   |           |   |   |
    |   |           |   |   +---game
    |   |           |   |   |       Board.java       # Représentation du plateau de jeu
    |   |           |   |   |       GameState.java   # État actuel de la partie (en cours, échec, mat)
    |   |           |   |   |       Move.java        # Représente un mouvement de pièce
    |   |           |   |   |       Tile.java        # Représente une case du plateau
    |   |           |   |   |
    |   |           |   |   +---pieces
    |   |           |   |   |       Bishop.java     # Classe pour le Fou
    |   |           |   |   |       King.java       # Classe pour le Roi
    |   |           |   |   |       Knight.java     # Classe pour le Cavalier
    |   |           |   |   |       Pawn.java       # Classe pour le Pion
    |   |           |   |   |       Piece.java      # Classe abstraite ou interface pour toutes les pièces
    |   |           |   |   |       Queen.java      # Classe pour la Reine
    |   |           |   |   |       Rook.java       # Classe pour la Tour
    |   |           |   |   |
    |   |           |   |   \---player
    |   |           |   |           AIPlayer.java   # Joueur IA utilisant Stockfish
    |   |           |   |           Player.java     # Joueur humain
    |   |           |   |
    |   |           |   +---ports
    |   |           |   |       ChessEngine.java    # Interface pour moteur d’échecs
    |   |           |   |       GameRepository.java # Interface pour la persistance des parties
    |   |           |   |       MoveLogger.java     # Interface pour journalisation des coups
    |   |           |   |
    |   |           |   \---usecases
    |   |           |           AnalyzePositionUseCase.java   # Analyse d’une position pour évaluation
    |   |           |           GetBestMoveUseCase.java       # Récupération du meilleur coup
    |   |           |           MovePieceInteractor.java      # Application d’un coup sur le plateau
    |   |           |           MovePieceUseCase.java         # Cas d’usage pour déplacer une pièce
    |   |           |
    |   |           +---dataproviders
    |   |           |   +---file
    |   |           |   |       FileGameRepository.java      # Persistance des parties en fichiers
    |   |           |   |       FileMoveLogger.java          # Journalisation des coups en fichiers
    |   |           |   |
    |   |           |   \---stockfish
    |   |           |           StockfishEngine.java         # Implémentation de l’interface ChessEngine pour Stockfish
    |   |           |           StockfishProcess.java        # Gestion du processus Stockfish externe
    |   |           |
    |   |           \---entrypoints
    |   |               +---console
    |   |               |       ConsoleController.java       # Interface console pour jouer
    |   |               |
    |   |               +---gui
    |   |               |       GuiController.java           # Interface graphique Swing/JavaFX
    |   |               |
    |   |               \---web
    |   |                   +---controllers                 # Controllers REST pour l’interface web
    |   |                   \---dto                         # Objets de transfert de données pour API
    |   \---resources
    |       \---images                                    # Images utilisées pour l’interface (pièces, plateau)
    \---test
        +---java
        |   \---com
        |       \---chess
        |           +---acceptance                           # Tests d’acceptation / fonctionnels
        |           +---architecture                         # Tests de structure et respect des patterns
        |           +---core
        |           |   +---entities                         # Tests unitaires pour entités du domaine
        |           |   \---usecases                         # Tests unitaires pour use cases
        |           +---dataproviders                        # Tests pour persistence et moteurs (Stockfish)
        |           +---entrypoints                           # Tests pour consoles, GUI ou Web
        |           \---integration                           # Tests d’intégration entre modules
        \---resources                                       # Ressources nécessaires aux tests (ex : JSON de test)
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
