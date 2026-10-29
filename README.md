# Spring Boot + Angular Starter Kit

A production-ready starting point for business web applications. Not a tutorial —
the boring decisions are already made, tested, and documented, so a new project
starts at "build the feature" instead of "wire up authentication again".

**Backend:** Java 17 · Spring Boot 3.5 · Spring Security 6 · PostgreSQL · Flyway · JWT
**Frontend:** Angular 20 (LTS) · TypeScript · Tailwind CSS 4 · signals

---

## What's included

| Concern | What you get |
|---|---|
| **Authentication** | JWT access tokens (15 min) + refresh tokens with **rotation and revocation**, stored server-side. Reuse of a revoked token kills every session for that user. |
| **Authorisation** | Role-based (`USER` / `MANAGER` / `ADMIN`), enforced **twice**: URL rules in `SecurityConfig` and `@PreAuthorize` on controllers. |
| **Audit trail** | Append-only `audit_logs` table. Login success and failure are recorded automatically from Spring Security events, via a listener — no service has to remember to call it. |
| **Audit columns** | Every entity extends `BaseEntity`: UUID id, `createdAt/By`, `updatedAt/By`, `@Version` optimistic locking. |
| **Error handling** | One `ApiError` shape for the whole API, `@RestControllerAdvice`, per-field validation messages. Exception messages and stack traces are never leaked. |
| **Migrations** | Flyway owns the schema; Hibernate runs in `validate` mode so a mapping drift fails the build instead of corrupting data. |
| **API docs** | OpenAPI/Swagger UI with bearer auth wired in. |
| **Frontend auth** | Signals-based `AuthService`, functional HTTP interceptor that **auto-refreshes on 401 and retries once**, de-duplicated so ten parallel 401s cause one refresh. |
| **UI kit** | Tailwind v4 design tokens, `btn` / `field` / `card` utilities, toasts, lazy-loaded routes with guards. |
| **Ops** | Docker Compose (Postgres + API), multi-stage Dockerfiles, non-root API container, healthchecks, GitHub Actions CI. |

## Quick start

```bash
make env          # creates .env with a freshly generated JWT secret
make up           # starts Postgres + API (builds images on first run)
make web          # in a second terminal: Angular dev server on :4200
```

Then open **http://localhost:4200**.

Create your first admin by putting these in `.env` **before** `make up`, then remove them:

```env
BOOTSTRAP_ADMIN_EMAIL=you@example.com
BOOTSTRAP_ADMIN_PASSWORD=a-strong-password
```

| URL | What |
|---|---|
| http://localhost:4200 | Angular app |
| http://localhost:8080/swagger-ui.html | API documentation |
| http://localhost:8080/actuator/health | Health check |

### Running without Docker

```bash
docker compose up -d db          # or your own Postgres
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
cd frontend && npm install && npm start
```

## Tests

```bash
make test        # 10 tests: auth lifecycle, RBAC, validation, error handling, service unit tests
```

The API tests are genuine end-to-end tests through the HTTP layer — filters, security
config, validation, exception handling and JPA all included. They run on H2, so no
Docker is needed in CI.

```
AuthFlowTest      register → login → protected call → refresh rotation → reuse rejected → logout
                  duplicate email → 409 · validation → 400 with field errors
                  bad password → 401 without leaking which field was wrong
                  tampered token → 401 · USER hitting /api/admin/** → 403
UserServiceTest   email normalisation, password hashing, duplicate detection, role mapping
```

## Project layout

```
backend/src/main/java/com/starterkit/
├── config/     SecurityConfig, AppProperties, OpenApiConfig, BootstrapAdminSeeder
├── common/     BaseEntity, ApiError, GlobalExceptionHandler, PageResponse
├── auth/       JWT, refresh tokens, filter, entry point, DTOs
├── user/       User, Role, repository, service, controllers
└── audit/      AuditLog, AuditService, AuthenticationAuditListener

frontend/src/app/
├── core/       AuthService, interceptors, guards, models, notifications
├── features/   login, register, dashboard, admin-users (all lazy-loaded)
└── app.config.ts, app.routes.ts, app.component.ts
```

## Design decisions

Every non-obvious choice is recorded with its trade-offs in **[docs/DECISIONS.md](docs/DECISIONS.md)**.
architecture: why a modular monolith, why Flyway, why JWT in localStorage and what you
should change for high-security apps, why double authorisation, why UUID keys.

## Reusing this for a client project

1. `cp -r starter-kit my-client-project`
2. Rename the package: `find . -name "*.java" -exec sed -i 's/com\.starterkit/com.clientname/g' {} +`
3. Rename the app in `application.yml` and `package.json`.
4. `make env && make up`
5. Start building features. Delete `dashboard`/`admin-users` when they stop being useful.

That rename-and-go step is the whole point: it turns a three-week project into a one-week one.
