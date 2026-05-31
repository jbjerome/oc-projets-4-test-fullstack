.PHONY: up rebuild down logs ps build restart seed clean prune ng-build test-frontend test-frontend-coverage test-e2e test-e2e-down

E2E_COMPOSE := docker compose -f docker-compose.yml -f docker-compose.e2e.yml

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

test-frontend:
	docker compose exec -T frontend npm test -- --watch=false

test-frontend-coverage:
	docker compose exec -T frontend npx jest --watch=false --coverage
	@echo ""
	@echo "  HTML report: front/coverage/jest/lcov-report/index.html"

test-e2e:
	$(E2E_COMPOSE) --profile e2e up --build --abort-on-container-exit --exit-code-from cypress
	$(E2E_COMPOSE) --profile e2e down
	@echo ""
	@echo "  HTML report: front/coverage/lcov-report/index.html"

test-e2e-down:
	$(E2E_COMPOSE) --profile e2e down -v

prune:
	docker image prune -f

clean:
	docker compose down --rmi local -v
