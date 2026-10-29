#!/usr/bin/env bash
# Publie le starter kit sur GitHub. À lancer depuis le dossier starter-kit/.
# Prérequis : git installé, et un compte GitHub.
set -euo pipefail

REPO_NAME="${1:-spring-boot-angular-starter-kit}"

echo "▶ 1/6  Vérification de l'environnement"
command -v git >/dev/null || { echo "✗ git n'est pas installé."; exit 1; }

if [ -d .git ]; then
  echo "  Un dépôt git existe déjà ici — je continue avec celui-ci."
else
  git init -q
  echo "  Dépôt git initialisé."
fi

git config user.name  >/dev/null 2>&1 || { echo "✗ Configure d'abord ton identité git :"; \
  echo "    git config --global user.name  \"Ton Nom\""; \
  echo "    git config --global user.email \"ton@courriel.com\""; exit 1; }

echo "▶ 2/6  Vérification que rien de lourd ou de secret n'est committé"
BIG=$(find . -type f -size +1M -not -path "*/node_modules/*" -not -path "*/target/*" -not -path "*/.git/*" || true)
if [ -n "$BIG" ]; then echo "⚠  Fichiers > 1 Mo détectés :"; echo "$BIG"; fi
if [ -f .env ]; then
  echo "✗ .env existe et contient des secrets. Il est ignoré par .gitignore, mais vérifie :"
  git check-ignore -v .env || { echo "  .env n'est PAS ignoré — arrêt."; exit 1; }
fi
echo "  OK — $(find . -type f -not -path '*/node_modules/*' -not -path '*/target/*' \
  -not -path '*/.git/*' -not -path '*/dist/*' | wc -l) fichiers seront committés."

echo "▶ 3/6  Premier commit"
git add -A
git commit -q -m "feat: production-ready Spring Boot 3.5 + Angular 20 starter kit

Backend: JWT auth with revocable rotating refresh tokens, RBAC enforced at both
URL and method level, append-only audit trail, Flyway migrations, typed error
handling, OpenAPI docs, Docker, GitHub Actions CI.

Frontend: Angular 20 with signals, HTTP interceptor with automatic token refresh
and request retry, route guards, Tailwind CSS 4 design tokens, lazy routes.

10 passing tests covering the full auth lifecycle, authorisation, validation and
error handling. 7 architecture decisions documented with their trade-offs."

echo "▶ 4/6  Branche main"
git branch -M main

echo "▶ 5/6  Création du dépôt distant"
if command -v gh >/dev/null 2>&1; then
  gh repo create "$REPO_NAME" --public --source=. --remote=origin --push \
    --description "Production-ready Spring Boot + Angular starter kit: JWT auth with rotating refresh tokens, RBAC, audit trail, Flyway, Docker, CI."
  echo "  ✓ Dépôt créé et poussé via gh."
else
  echo "  La CLI 'gh' n'est pas installée. Fais ceci manuellement :"
  echo "    1. Va sur https://github.com/new"
  echo "    2. Nom du dépôt : $REPO_NAME   —   Visibilité : Public   —   Ne coche RIEN d'autre"
  echo "    3. Puis lance :"
  echo "         git remote add origin git@github.com:TON_PSEUDO/$REPO_NAME.git"
  echo "         git push -u origin main"
  echo
  read -r -p "  Appuie sur Entrée une fois le dépôt créé et le push effectué… " _
fi

echo "▶ 6/6  Terminé"
git remote -v || true
echo
echo "✓ Publie maintenant :"
echo "  • l'URL du dépôt dans ton profil Contra (section Work)"
echo "  • l'URL du dépôt dans ton profil LinkedIn (section Projets)"
echo "  • docs/PORTFOLIO-CASE-STUDY.md comme description du projet"
