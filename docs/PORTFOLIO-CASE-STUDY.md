# Case study — ready to publish

Copy this to your Contra "Work" section, your LinkedIn, and your portfolio.
Replace `[brackets]` and add 2 screenshots + a 60-second video.

---

## Full-stack starter kit: from "new project" to "first feature" in a day

**The problem.** Every business web application needs the same six things before anyone
can work on the actual product: authentication, authorisation, auditing, error handling,
database migrations, and a deploy pipeline. Built from scratch, that's two to three weeks
of a senior developer's time — paid for by the client, invisible to them, and repeated on
every single project.

**What I built.** A production-ready foundation that reduces that to a package rename.
Java 17 / Spring Boot 3.5 / PostgreSQL on the back end, Angular 20 / TypeScript / Tailwind 4
on the front, Docker and CI included.

**Engineering decisions a non-technical reader can still evaluate:**
- **Refresh tokens are revocable and rotate on use.** If a stolen token is ever used, the
  system detects it and terminates every session for that account. A purely stateless JWT
  cannot do this — logout would be a lie until the token expires.
- **The database schema cannot drift from the code.** Migrations are versioned and
  reviewed; the ORM is configured to *fail the build* if a mapping stops matching the
  real schema, rather than silently altering a live table.
- **Authorisation is enforced in two independent places**, because a single misconfigured
  URL rule is one of the most common causes of real-world data breaches.
- **A developer cannot forget to audit.** Security events are captured from the framework's
  own event stream — including failed login attempts, which most applications never record
  and every incident investigation asks for first.
- **Bad security decisions are documented rather than hidden.** The token storage approach
  and its XSS trade-off are written down, along with exactly what to change for
  high-security applications.

**Results**

| Metric | Value |
|---|---|
| Java source files / TypeScript files | 40 / 17 |
| Lines of code (backend + frontend + migrations) | ~2,700 |
| API endpoints wired and secured | 11 |
| Automated tests, all passing | 10 |
| Backend build + full test suite | **19 seconds** |
| Frontend production build | **3 seconds** · 91 kB transferred |
| Architecture decisions documented | 7 |
| Time from clone to running stack (`make env && make up`) | one command |

The test suite is genuinely end-to-end — real HTTP requests through the security filters,
not mocked controllers. It covers the full auth lifecycle (register → login → protected
call → token rotation → reuse rejection → logout), role-based access denial, validation
errors, and the requirement that a failed login never reveals whether the email or the
password was wrong.

**Impact on delivery time.** A new project using this foundation skips an estimated two to
three weeks of undifferentiated setup. On a fixed-price engagement, that is the difference
between a project that needs padding and one that is genuinely profitable.

**Stack:** Java 17 · Spring Boot 3.5 · Spring Security 6 · PostgreSQL 16 · Flyway · JWT ·
JUnit 5 · Angular 20 · TypeScript · Tailwind CSS 4 · Docker · GitHub Actions

**Links:** [GitHub repo] · [architecture decisions doc] · [60s walkthrough video]

---

### Why this case study works

Most developer case studies say "I used React and Node and it was a fun project". This one
does four things they don't:

1. **It opens with a business cost**, not technology — two to three weeks of paid time,
   repeated on every project.
2. **It quantifies everything**, including build times. Numbers are verifiable and they
   signal rigour.
3. **It explains decisions in terms a project manager can judge** — "logout would be a lie
   until the token expires" needs no security background to understand.
4. **It shows judgement about trade-offs**, including one decision that is *not* ideal for
   every context and what to do differently. Admitting a limitation is the strongest
   seniority signal available.
