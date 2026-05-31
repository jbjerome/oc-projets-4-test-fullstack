.PHONY: up rebuild down logs ps build restart seed clean prune ng-build test test-back test-front test-e2e test-e2e-down

E2E_COMPOSE := docker compose -f docker-compose.yml -f docker-compose.e2e.yml
MAVEN_IMAGE := maven:3.9-eclipse-temurin-21
MAVEN_CACHE := oc-maven-repo

up:
	docker compose up -d
	@echo ""
	@echo "  Frontend : http://localhost:4200"
	@echo "  Backend  : http://localhost:8080"
	@echo "  MySQL    : localhost:3306"

rebuild:
	docker compose up -d --build

down:
	docker compose down

restart:
	docker compose restart

build:
	docker compose build

logs:
	docker compose logs -f

ps:
	docker compose ps

seed:
	docker compose exec -T mysql sh -c 'mysql --force -u"$$MYSQL_USER" -p"$$MYSQL_PASSWORD" "$$MYSQL_DATABASE"' < back/src/main/resources/sql/insert_user.sql

ng-build:
	docker compose exec -T frontend npx ng build

# Tests back : JUnit + Mockito + intégration (H2 en mémoire), couverture JaCoCo.
# Exécuté dans un conteneur Maven jetable, cache .m2 persistant. Aucune base requise.
test-back:
	docker run --rm \
		-v "$(CURDIR)/back":/app \
		-v $(MAVEN_CACHE):/root/.m2 \
		-w /app \
		$(MAVEN_IMAGE) mvn -B clean verify
	@echo ""
	@echo "  Rapport JaCoCo : back/target/site/jacoco/index.html"

# Tests front unitaires / d'intégration (Jest) + couverture, dans un conteneur jetable.
test-front:
	docker compose run --rm --no-deps frontend npx jest --watch=false --coverage
	@echo ""
	@echo "  Rapport Jest : front/coverage/jest/lcov-report/index.html"

# Tests end-to-end (Cypress) avec couverture et rapport d'exécution mochawesome.
test-e2e:
	$(E2E_COMPOSE) --profile e2e up --build --abort-on-container-exit --exit-code-from cypress
	$(E2E_COMPOSE) --profile e2e down
	@echo ""
	@echo "  Couverture E2E  : front/coverage/lcov-report/index.html"
	@echo "  Rapport Cypress : front/cypress/reports/index.html"

# Lance l'ensemble des suites (back, front unit, e2e) en une commande.
test: test-back test-front test-e2e

test-e2e-down:
	$(E2E_COMPOSE) --profile e2e down -v

prune:
	docker image prune -f

clean:
	docker compose down --rmi local -v
