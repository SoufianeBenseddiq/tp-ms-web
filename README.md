# Gestion École — V1 monolithique

Un seul projet Spring Boot, une seule base MySQL, un seul déploiement.

## Lancer

```bash
docker compose up -d                       # MySQL 8.4 sur localhost:3306 (root/root)
DB_PASSWORD=root ./mvnw spring-boot:run
```

Variables : `DB_URL`, `DB_USERNAME` (défaut `root`), `DB_PASSWORD`, `SERVER_PORT` (défaut `8080`).
Tests : `./mvnw test` (H2 en mémoire, MySQL non requis).

## Structure (un package par domaine, prêt pour le découpage en microservices)

| Package        | Rôle                                            |
|----------------|-------------------------------------------------|
| `student`      | Student Management                              |
| `course`       | Course Management (modules + capacité)          |
| `registration` | Inscription d'un étudiant à un module           |
| `exam`         | Exam & Grade (planification + saisie des notes) |
| `common`       | Exceptions + gestion d'erreurs (ProblemDetail)  |

## API REST

| Méthode | URL | Description |
|---|---|---|
| POST | `/api/students` | Créer un étudiant |
| PUT | `/api/students/{id}` | Modifier un étudiant |
| GET | `/api/students`, `/api/students/{id}` | Consulter |
| POST | `/api/courses` | Créer un module (`code`, `title`, `capacity`) |
| PUT | `/api/courses/{id}` | Modifier un module |
| GET | `/api/courses`, `/api/courses/{id}` | Consulter (avec `enrolled` / `availableSeats`) |
| POST | `/api/registrations` | Inscrire `{studentId, courseId}` |
| GET | `/api/registrations?studentId=&courseId=` | Lister les inscriptions |
| POST | `/api/exams` | Planifier `{courseId, title, examDate, room}` |
| GET | `/api/exams?courseId=`, `/api/exams/{id}` | Consulter les examens |
| POST | `/api/grades` | Saisir une note `{examId, studentId, score}` (0–20) |
| PUT | `/api/grades/{id}` | Corriger une note `{score}` |
| GET | `/api/grades?examId=&studentId=` | Consulter les notes |

## Règles métier

- Email étudiant et code module uniques → `409`
- Inscription refusée si déjà inscrit ou module complet → `409` (verrou pessimiste sur le module)
- Capacité d'un module non réductible sous le nombre d'inscrits → `409`
- Note uniquement pour un étudiant inscrit au module de l'examen, une seule note par examen/étudiant → `409`
- Entrées invalides → `400` avec le détail par champ ; ressource absente → `404`

## Exemple

```bash
curl -X POST localhost:8080/api/students -H 'Content-Type: application/json' \
  -d '{"firstName":"Sara","lastName":"Bennani","email":"sara@ecole.ma","birthDate":"2002-03-01"}'
curl -X POST localhost:8080/api/courses -H 'Content-Type: application/json' \
  -d '{"code":"MS101","title":"Microservices","capacity":30}'
curl -X POST localhost:8080/api/registrations -H 'Content-Type: application/json' \
  -d '{"studentId":1,"courseId":1}'
curl -X POST localhost:8080/api/exams -H 'Content-Type: application/json' \
  -d '{"courseId":1,"title":"Examen final","examDate":"2026-12-20T09:00:00","room":"Amphi A"}'
curl -X POST localhost:8080/api/grades -H 'Content-Type: application/json' \
  -d '{"examId":1,"studentId":1,"score":16.5}'
```
