# Yoga App — Backend

Backend de l'application Yoga App (Spring Boot 3.5.5, Java 21).

- name : back
- port : 8080

## Prérequis

- **Docker** et **Docker Compose**
- **Make**

Tout (build, exécution, tests) passe par Docker via le `Makefile` situé à la racine du dépôt :
aucune installation locale de JDK ni de Maven n'est nécessaire.

> Un fichier `.env` doit être présent à la racine du dépôt (variables `DB_*`, `DB_ROOT_PASSWORD`,
> `TOKEN_SECRET`). Voir `back/.env.example` pour les clés attendues.

## Lancer le projet

Depuis la **racine du dépôt** :

```bash
make up      # démarre MySQL + backend (+ frontend)
make seed    # insère l'utilisateur admin par défaut
```

Services disponibles :
- Backend : http://localhost:8080
- MySQL : localhost:3306

Identifiants de l'utilisateur admin créé par `make seed` :
- login : `yoga@studio.com`
- password : `test!1234`

Autres commandes utiles :

```bash
make logs    # suivre les logs
make down    # arrêter la stack
```

## Lancer les tests du back

Tests unitaires (JUnit 5 + Mockito) et d'intégration (Spring Boot Test + MockMvc).
Les tests d'intégration utilisent une base **H2 en mémoire** : aucune base MySQL ni stack
démarrée n'est requise.

Depuis la **racine du dépôt** :

```bash
make test-back
```

Cette commande exécute tous les tests dans un conteneur Maven jetable, puis génère le
rapport de couverture **JaCoCo** :

```
back/target/site/jacoco/index.html
```

> `make test` lance l'ensemble des suites du projet (back + front unitaire + e2e).

## Ressources

### Collection Postman

Importez la collection Postman :

> postman/yoga.postman_collection.json

Documentation Postman :
https://learning.postman.com/docs/getting-started/importing-and-exporting-data/#importing-data-into-postman
