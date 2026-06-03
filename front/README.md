# Yoga App — Frontend

Frontend de l'application Yoga App (Angular 19).

- port : 4200

## Prérequis

- **Docker** et **Docker Compose**
- **Make**

Tout (build, exécution, tests) passe par Docker via le `Makefile` situé à la racine du dépôt :
aucune installation locale de Node ni d'Angular CLI n'est nécessaire.

> Un fichier `.env` doit être présent à la racine du dépôt (voir `back/.env.example`).

## Lancer le projet

Depuis la **racine du dépôt** :

```bash
make up      # démarre MySQL + backend + frontend
make seed    # insère l'utilisateur admin par défaut
```

Application disponible sur : http://localhost:4200

Identifiants de l'utilisateur admin créé par `make seed` :
- login : `yoga@studio.com`
- password : `test!1234`

Autres commandes utiles :

```bash
make logs    # suivre les logs
make down    # arrêter la stack
```

## Lancer les tests

Toutes les commandes s'exécutent dans des conteneurs jetables, depuis la **racine du dépôt**.

### Tests unitaires et d'intégration (Jest)

```bash
make test-front
```

Rapport de couverture :

```
front/coverage/jest/lcov-report/index.html
```

### Tests end-to-end (Cypress)

```bash
make test-e2e
```

> Exécuté sur l'hôte (nécessite **Node** et **Chrome** ; `npm ci` est lancé automatiquement
> si `node_modules` est absent) afin que la collecte de couverture istanbul soit fiable.

Cette commande lance l'application instrumentée puis Cypress, et produit :
- la **couverture e2e** : `front/coverage/lcov-report/index.html`
- le **rapport d'exécution** (mochawesome) : `front/cypress/reports/index.html`

> Les sélecteurs des tests Cypress s'appuient sur des attributs `data-testid`.
> `make test` lance l'ensemble des suites du projet (back + front unitaire + e2e).
