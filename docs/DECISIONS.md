# Architecture Decisions

Every non-obvious choice, with the trade-off stated. This document is the difference
between "a repo" and "a senior engineer's repo" — it is also the raw material for your
portfolio case study (interviewers and technical leads read this first).

---

## ADR-001 — Modular monolith, not microservices

**Decision.** One deployable Spring Boot application, organised by feature package
(`auth`, `user`, `audit`), not by layer.

**Why.** Microservices solve an organisational problem — many teams deploying
independently. A five-person product team has no such problem, and splitting early
means paying distributed-transaction, network-latency and operational costs for a
benefit nobody can use. Package boundaries give 80% of the modularity at 5% of the cost.

**Trade-off.** You cannot scale one module independently, and package boundaries are
enforced by discipline, not the compiler. If a module genuinely needs its own scaling
profile, extract it then — with the boundary already proven in production.

**When to revisit.** When the team exceeds ~15 engineers, or one module needs a
materially different scaling/availability profile.

---

## ADR-002 — Flyway owns the schema; Hibernate validates

**Decision.** `spring.jpa.hibernate.ddl-auto=validate`, migrations in
`src/main/resources/db/migration`.

**Why.** Reviewable, versioned, one-way schema changes with a full history. `validate`
means a mapping that drifts from the real schema **fails the build** instead of silently
altering a production table. `update` is banned: it cannot drop columns, cannot rename
safely, and produces different results in different environments.

**Trade-off.** Slower iteration — every change needs a new migration file. Worth it the
first time a deploy goes wrong at 2am and you need to know exactly what the schema was.

---

## ADR-003 — Refresh tokens are stored server-side and rotated

**Decision.** Access token TTL 15 minutes, stateless. Refresh tokens (7 days) live in a
`refresh_tokens` table with a `revoked` flag, and are rotated on every use.

**Why.** A pure stateless JWT cannot be revoked: logout is a lie, and a stolen token is
valid until it expires. Storing refresh tokens costs one indexed lookup per refresh —
once every 15 minutes per user — and buys real revocation.

Rotation matters more than storage: if a stolen refresh token is used, the legitimate
user's next refresh fails, and **reusing a already-rotated token revokes every session
for that user**. That converts silent account takeover into a loud, recoverable event.
This is implemented in `AuthService.refresh()` and covered by the tests.

**Trade-off.** A database round-trip, a table that needs periodic cleanup, and slightly
more code. Non-negotiable for anything handling real user data.

---

## ADR-004 — Tokens in localStorage (and when to change it)

**Decision.** The Angular app stores both tokens in `localStorage`.

**Why.** Simplest correct implementation, no CSRF surface, works identically in
development and behind a reverse proxy.

**Trade-off — and this is a real one.** `localStorage` is readable by any JavaScript on
the page, so a single XSS vulnerability leaks both tokens.

**Change it for high-security applications:** keep the short-lived access token in
memory only, put the refresh token in an `httpOnly; Secure; SameSite=Strict` cookie, and
re-enable CSRF protection for the refresh endpoint. That costs a cookie-based CORS
setup and a CSRF token flow — real work, justified only when the data warrants it.

**Stated rather than hidden**, because a reviewer who spots this themselves and finds no
acknowledgement will assume you did not know. See it, name it, explain the alternative.

---

## ADR-005 — Authorisation is enforced twice

**Decision.** URL rules in `SecurityConfig` **and** `@PreAuthorize` on the controller.

**Why.** Defence in depth. A URL pattern is a string; one typo, or one new controller
added to a package nobody re-read, silently opens an endpoint. Method-level annotations
travel with the code. The cost is one duplicated line per controller.

**Trade-off.** Two places to update when a permission changes. Worth it — broken
authorisation is a breach, broken UX is a bug report.

---

## ADR-006 — UUID primary keys

**Decision.** `GenerationType.UUID` everywhere, not auto-incrementing longs.

**Why.** Sequential ids leak business volume (a competitor can estimate your user count
from an invoice number), make enumeration attacks trivial (`/api/orders/1043` invites
`/api/orders/1044`), and complicate merging data across environments.

**Trade-off.** 16 bytes vs 8, slightly worse index locality, and uglier URLs. At the
scale this kit targets, none of those matter; at 100M rows you would revisit the layout.

---

## ADR-007 — Auditing via `@EntityListeners` and Spring Security events

**Decision.** Audit *columns* (`createdBy`, `updatedBy`) come from a JPA
`AuditorAware`. Business *events* are recorded by a listener on Spring Security's own
`AuthenticationSuccessEvent` / `AbstractAuthenticationFailureEvent`.

**Why.** An audit trail that depends on developers remembering to call `auditService.record()`
will have holes within a month. Hooking the framework's own events means failed logins —
the most security-relevant thing most applications never record — are captured for free.
`AuditService` uses `Propagation.REQUIRES_NEW` so a record survives a rolled-back business
transaction, and swallows its own exceptions so auditing can never break a request.

**Trade-off.** Somewhat indirect — a new developer will not see the audit code on the
happy path. Documented here for exactly that reason.

---

## What is deliberately *not* here

| Omitted | Because | Add it when |
|---|---|---|
| Kafka / messaging | No consumer yet. Unused infrastructure is just operational debt. | You have a real asynchronous workload. |
| Redis cache | One database, one app. Premature caching hides N+1 query bugs. | You can measure the query it fixes. |
| Kubernetes manifests | `docker compose` covers single-node deployment. | You actually run a cluster. |
| Full i18n pipeline | The structure is there; translation management is a product decision. | You have a second language requirement. |
| Testcontainers | Adds Docker as a test dependency; H2 keeps CI simple. | You want integration tests against the real Postgres dialect. |
