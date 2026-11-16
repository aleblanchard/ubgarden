# UBGarden — « Sauve le hérisson ! »

[![CI](https://github.com/aleblanchard/ubgarden/actions/workflows/ci.yml/badge.svg)](https://github.com/aleblanchard/ubgarden/actions/workflows/ci.yml)
![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue)
![Gradle](https://img.shields.io/badge/Gradle-7.6-02303A?logo=gradle&logoColor=white)

Jeu 2D en Java/JavaFX réalisé pour le cours de **Programmation orientée objet** (L2 Informatique, Université de Bordeaux, 2025-2026), à partir du squelette fourni par l'équipe pédagogique.

Le joueur incarne un jardinier qui doit retrouver son hérisson à travers une série de jardins. Il faut ramasser toutes les carottes d'un jardin pour ouvrir la porte du suivant, tout en évitant guêpes et frelons, en gérant son énergie et en collectant des bonus.

## Lancer le jeu

Prérequis : JDK 17 (Gradle et JavaFX sont téléchargés automatiquement).

```bash
./gradlew run          # Linux / macOS
gradlew.bat run        # Windows
```

Le menu propose deux options :

- **Load default configuration** : charge la carte par défaut ;
- **Load from file** : charge un monde depuis un fichier `.properties` (exemples dans [`world/`](world/)).

### Commandes

| Touche | Action |
| --- | --- |
| Flèches | Se déplacer |
| Échap | Quitter |

## Fonctionnalités

**Jeu de base**
- Affichage de tous les éléments (décor, bonus, insectes, hérisson) et barre d'état : énergie, fatigue, bombes, niveau
- Collisions avec les bords et les obstacles (arbres, fleurs)
- Énergie consommée selon le type de sol, récupérée à l'arrêt
- Victoire en retrouvant le hérisson, défaite quand l'énergie tombe à zéro
- Bonus : pommes (soin + énergie) et trognons pourris (malus temporaire, minuteurs indépendants)

**Mondes et niveaux**
- Chargement de mondes depuis des fichiers `.properties`, compressés ou non
- Portes, ramassage des carottes et passage d'un niveau à l'autre
- Chaque jardin conserve son état quand on le quitte puis qu'on y revient

**Insectes et bombes**
- Guêpes : apparition depuis leur nid toutes les 5 s, déplacement aléatoire, meurent en piquant ou sur une bombe
- Frelons : apparition toutes les 10 s, deux piqûres avant de mourir, résistent à une bombe
- Fréquences de déplacement configurables (`waspMoveFrequency`, `hornetMoveFrequency`)
- Une bombe générée par guêpe, deux par frelon ; ramassage et déclenchement automatiques

## Conception

- **Patron Visiteur** : `WalkVisitor` / `Walkable` déterminent quelles cases chaque personnage (jardinier, insectes) peut traverser ; `PickupVisitor` / `Pickupable` gèrent le ramassage des bonus.
- **Insectes stockés par niveau** dans leur `Level`, ce qui préserve l'état de chaque jardin.
- **Une seule boucle de jeu active** : `GameLauncherView` arrête l'ancien `GameEngine` avant d'en lancer un nouveau, ce qui évite les blocages lors du chargement d'une partie.

## Structure

```
src/main/java/fr/ubx/poo/ubgarden/
├── Main.java
└── game/
    ├── engine/     # boucle de jeu, entrées clavier, minuteurs, barre d'état
    ├── go/         # objets du jeu : personnages, décor, bonus
    ├── launcher/   # chargement des mondes
    └── view/       # sprites et rendu JavaFX
world/              # mondes d'exemple (.properties)
```

## Limites connues

- Retour au niveau précédent : le jardinier n'arrive pas sur la bonne porte (position (0,0) en revenant au niveau 1, porte descendante au lieu de la porte d'arrivée pour les autres niveaux).
- L'éditeur de cartes n'est pas implémenté.

## Auteurs

- **Alexis Blanchard**
- **Félix Claus**
