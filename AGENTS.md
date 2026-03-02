# Allauch Simulator (Backend)

## Objectif produit

Allauch Simulator est le coeur (moteur + règles + persistance) d'un jeu de gestion de basketball.
L'API REST exposée par le module `api` sert un frontend Angular (dans un autre repo) qui ne fait que présenter une UI.

### MVP (cible actuelle)

- 8 joueurs (utilisateurs) peuvent se connecter.
- Chaque utilisateur possède exactement 1 club.
- Jour après jour, chaque utilisateur ajuste ses `GamePlan` pour les matchs à venir.
- Un joueur actif (choix cohérents) doit battre un joueur AFK de manière fiable, tout en gardant une simulation "crédible dans l'esprit".

### Temps (cible actuelle)

- Temps réel (les événements sont planifiés en horodatage réel).
- Échelle visée: ~1 an in-game = ~2 semaines IRL.

## Architecture du repo (multi-modules Gradle)

- `api`: application Spring Boot + controllers REST + sécurité (JWT Auth0) + configuration runtime.
- `application-core`: orchestration / cas d'usage (ex: initialisation de saison, scheduling/TimeEvents, exécuteurs).
- `domain`: coeur métier (records/entités, calculateurs/simulateurs, règles de jeu) et contrats (`service`, `repository`).
- `infra`: implémentations techniques (persistence JPA, mappers MapStruct, repositories concrets).

### Séparation stricte (intention)

- `domain` doit rester le plus "pur" possible (pas de dépendance Spring/infra).
- `application-core` orchestre les use-cases et dépend du `domain` via interfaces.
- `infra` branche la persistence et l'IO.
- `api` reste une couche d'adaptation (DTO, endpoints, auth).

## Database (Postgres)

- `schema.sql` est la référence: on doit pouvoir reconstruire la base from scratch à tout moment.
- Les scripts `truncate_db.sql` / `reset_schema_postgres.sql` existent pour aider au cycle dev.
- Configuration locale actuelle dans `api/src/main/resources/application.yml` (Postgres `localhost:5432`, user `postgres`, password `password`).
- Note: `spring.jpa.hibernate.ddl-auto` est en `update` dans la config actuelle, mais l'intention long-terme est de s'appuyer sur `schema.sql` comme source de verite.

## Auth / sécurité

- Auth0 JWT (resource server) en place.
- Actuellement, seule la route `POST /users/associate` est authentifiée; le reste est permissif.
- À terme, l'objectif est de sécuriser progressivement les endpoints.

## Simulation / temps / déterminisme

- La simulation utilise de l'aléatoire (ex: `ShotSimulator` avec `Random`).
- L'objectif est de pouvoir fournir/choisir une seed pour rendre des runs reproductibles (debug + tests).
- Le scheduling actuel est orienté "test rapide" (exécution immédiate des `TimeEvent` après init), avec l'intention future de passer à un vrai scheduler.

## Endpoints clés (high-level)

- `POST /users/associate`: associe l'utilisateur Auth0 à un club (1 club par user).
- `POST /gameplans/init`: initialise une saison (usage dev/admin).
- `GET /gameplans/club/{clubId}`: récupère le prochain match (GamePlan) d'un club.
- Autres ressources: clubs, teams, players, games, trainings, badges.

### Flux d'initialisation (intention produit)

- `POST /gameplans/init` crée le "monde" de départ (clubs/équipes/joueurs) et la saison.
- Les 8 clubs sont créés lors de l'init; ensuite `POST /users/associate` rattache un utilisateur à un club existant.
- Comportement choisi: chaque appel à `POST /gameplans/init` crée une nouvelle saison (pas idempotent).

## Commandes utiles (repo)

- Lancer l'API: `./gradlew :api:bootRun` (Windows: `.\gradlew :api:bootRun`)
- Tests: `./gradlew test` (ou `./gradlew :domain:test` pour cibler la simu)

## Notes d'outillage (workspace)

- `rg` (ripgrep) n'est pas disponible dans cet environnement; utiliser PowerShell `Select-String` pour les recherches.

## Guidelines techniques (code)

## Workflow de l'agent (a appliquer a chaque tache)

1. Reformuler le besoin en 1-2 phrases.
2. Proposer une solution simple (pas d'overengineering).
3. Implementer en respectant les couches (API/Application/Domain/Infra).
4. Ajouter/adopter les tests.
5. Verifier: formatage, gestion d'erreurs, logs, null-safety, conventions.
6. Livrer: code + courte explication + hypotheses/risques.

### Frontières de modules

- `domain`: code métier pur (règles, simulateurs, types). Pas d'annotations Spring, pas de JPA, pas d'accès DB, pas d'HTTP.
- `application-core`: use-cases et orchestration (saison, exécution d'événements, workflow). Dépend de `domain` via interfaces.
- `infra`: implémentations techniques (JPA entities/repositories, mappers, adaptateurs). C'est le seul endroit où `jakarta.persistence.*` doit apparaître.
- `api`: adaptateur HTTP (controllers, DTO, config sécurité). Pas de logique métier lourde; déléguer à `application-core`/services.

### DB: schema-first

- `schema.sql` est la source de vérité du modèle relationnel.
- Toute évolution de données doit commencer par une mise à jour de `schema.sql`, puis aligner `infra` (entities/mappings) et `domain` (types).
- Éviter de s'appuyer sur `hibernate.ddl-auto=update` pour introduire des changements de schéma: c'est toléré en dev, mais pas une stratégie.

### API REST (patterns)

- Controllers minces: validation/formatage + appel service + mapping DTO.
- Mapping: `api` mappe DTO <-> `domain` (MapStruct), `infra` mappe Entity <-> `domain` (MapStruct).
- Erreurs: privilégier des statuts HTTP explicites (`ResponseStatusException`) et des messages stables (consommés par le front).
- CORS: `http://localhost:4201` correspond au front Angular (repo séparé).

### Auth / sécurité (direction)

- Auth0 JWT est définitif.
- À court terme, certains endpoints restent ouverts pour itérer vite.
- À moyen terme, objectif: sécuriser progressivement les endpoints et baser l'accès sur "1 user = 1 club".

### Simulation: crédible mais "skill-based"

- Les choix utilisateur doivent avoir un impact majeur sur la performance (un bon joueur bat un AFK).
- Garder une base de cohérence statistique (pas besoin d'ultra-réalisme), mais éviter les comportements aberrants.

### Déterminisme / seed

- La simulation doit pouvoir tourner avec une seed contrôlable (debug + tests + reproductibilité).
- En tests, préférer des runs déterministes (seed fixe) + assertions sur propriétés (bornes, invariants) plutôt que sur du bruit aléatoire.

### Temps & scheduling

- Cible actuelle: temps réel; échelle visée `1 an in-game ≈ 2 semaines IRL`.
- Implémentation actuelle orientée "résultats rapides" (exécution immédiate d'événements après init).
- Évolution prévue: scheduler réel (sans casser l'API du coeur), donc éviter les dépendances fortes à `Instant.now()` dans le `domain`.

### Tests

- `domain`: tests unitaires/prop-based sur calculateurs/simulateurs (rapides, déterministes via seed).
- `api`/`application-core`: tests d'intégration ciblés sur endpoints et use-cases critiques (init saison, association user->club, récupération prochain match).

## 10) Logging & observabilite

- Utiliser SLF4J (`log.info/debug/warn/error`), jamais `System.out.println`.
- Logs:
- `debug`: details de diagnostic
- `info`: evenements metier importants
- `warn`: degradations, inputs inattendus mais geres
- `error`: echecs et exceptions non recuperables
- Ajouter un correlation id (MDC) si le projet le supporte.
- Logs d'erreur: inclure l'exception (`log.error(\"...\", e)`), plus contexte.

### Pratiques de code (règles explicites)

- Éviter les null-checks "défensifs" partout: ne pas changer le comportement implicitement (ex: transformer un bug en silence). Préférer des invariants clairs, des exceptions explicites, ou des types qui empêchent l'état invalide.
- Pas de `clamp` / normalisation silencieuse (ex: borner une valeur) sauf si une règle produit l'exige explicitement. Si un input est invalide, le faire échouer clairement.
- Invariant global: les notes/attributs des joueurs sont **toujours** dans l'intervalle `[0..99]` inclus.
  - Ne pas "corriger" une note invalide par défaut; considérer ça comme une erreur de données ou de logique à traiter à la source.
