.DEFAULT_GOAL := help
SHELL := /bin/bash

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-16s\033[0m %s\n", $$1, $$2}'

env: ## Create .env from the example with a freshly generated JWT secret
	@test -f .env || (cp .env.example .env && \
		SECRET=$$(openssl rand -base64 48 | tr -d '\n') && \
		sed -i.bak "s|^JWT_SECRET=.*|JWT_SECRET=$$SECRET|" .env && rm -f .env.bak && \
		echo "Created .env with a generated JWT secret")

up: ## Start Postgres + API
	docker compose up -d --build

down: ## Stop everything
	docker compose down

logs: ## Tail API logs
	docker compose logs -f api

db: ## Start only Postgres (for running the API from your IDE)
	docker compose up -d db

api: ## Run the API locally (needs Postgres running)
	cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

web: ## Run the Angular dev server
	cd frontend && npm start

test: ## Run backend tests
	cd backend && ./mvnw test

verify: ## Full backend verification
	cd backend && ./mvnw verify

reset-db: ## Drop and recreate the database volume
	docker compose down -v && docker compose up -d db

openapi: ## Open the Swagger UI
	@echo "http://localhost:8080/swagger-ui.html"
