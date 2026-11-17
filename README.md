# Gestion École — V1 (monolith)

One Spring Boot project, one MySQL database, one deployment.

- **Student Management**: create, update, view students
- **Course Management**: create modules (courses) and set their capacity
- **Registration**: register a student to a module
- **Exam & Grade**: schedule exams and record grades

---

## Run

```bash
docker compose up -d                       # MySQL 8.4 on localhost:3306 (root/root)
DB_PASSWORD=root ./mvnw spring-boot:run    # API on http://localhost:8080
```

| Variable | Default |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/gestion_ecole?createDatabaseIfNotExist=true&serverTimezone=UTC` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | *(empty)* |
| `SERVER_PORT` | `8080` |

Tests: `./mvnw test` (in-memory H2, MySQL not required).

---

## Architecture

```
ms.tp.gestionecole
├── entity          JPA entities: Student, Course, Registration, Exam, Grade
├── repository      Spring Data JPA repositories
├── dto             Request / Response records (input validation)
├── converter       Entity <-> DTO mapping
├── service
│   ├── facade      Service interfaces (used by controllers)
│   └── impl        Implementations (business rules, transactions)
├── controller      REST controllers (/api/**)
└── exception       BusinessException, ResourceNotFoundException, GlobalExceptionHandler
```

Request flow: `controller → service.facade → service.impl → repository → entity`, with `converter` building DTOs.

---

## Errors

Errors are returned in the **RFC 7807 (ProblemDetail)** format.

| HTTP | Cause |
|---|---|
| `400 Bad Request` | Validation failed (details per field in `errors`) |
| `404 Not Found` | Resource does not exist |
| `409 Conflict` | Business rule violated (duplicate, module full...) |

```json
// 404
{ "type": "about:blank", "title": "Not Found", "status": 404,
  "detail": "Étudiant introuvable (id=99)", "instance": "/api/students/99" }

// 400
{ "type": "about:blank", "title": "Bad Request", "status": 400,
  "detail": "Données invalides", "instance": "/api/students",
  "errors": { "firstName": "must not be blank", "email": "must be a well-formed email address" } }
```

---

## 1. Students — `/api/students`

| Method | URL | Description | Success |
|---|---|---|---|
| POST | `/api/students` | Create a student | `201` |
| PUT | `/api/students/{id}` | Update a student | `200` |
| GET | `/api/students/{id}` | Get one student | `200` |
| GET | `/api/students` | List all students | `200` |

**Payload (POST / PUT)**

| Field | Type | Rules |
|---|---|---|
| `firstName` | string | required, max 100 |
| `lastName` | string | required, max 100 |
| `email` | string | required, valid email, max 150, **unique** |
| `birthDate` | date `yyyy-MM-dd` | optional, in the past |

```json
POST /api/students
{
  "firstName": "Sara",
  "lastName": "Bennani",
  "email": "sara@ecole.ma",
  "birthDate": "2002-03-01"
}
```

**Response `201`**
```json
{
  "id": 1,
  "firstName": "Sara",
  "lastName": "Bennani",
  "email": "sara@ecole.ma",
  "birthDate": "2002-03-01"
}
```

`GET /api/students` returns an array of these objects.

**Errors**: `409` if the email already exists · `404` if the id does not exist (PUT/GET) · `400` if the data is invalid.

---

## 2. Courses (modules) — `/api/courses`

| Method | URL | Description | Success |
|---|---|---|---|
| POST | `/api/courses` | Create a module | `201` |
| PUT | `/api/courses/{id}` | Update a module | `200` |
| GET | `/api/courses/{id}` | Get one module | `200` |
| GET | `/api/courses` | List all modules | `200` |

**Payload (POST / PUT)**

| Field | Type | Rules |
|---|---|---|
| `code` | string | required, max 20, **unique** |
| `title` | string | required, max 150 |
| `capacity` | integer | required, ≥ 1 |

```json
POST /api/courses
{
  "code": "MS101",
  "title": "Microservices",
  "capacity": 30
}
```

**Response `201`**
```json
{
  "id": 1,
  "code": "MS101",
  "title": "Microservices",
  "capacity": 30,
  "enrolled": 0,
  "availableSeats": 30
}
```

`enrolled` = number of registered students, `availableSeats` = `capacity - enrolled`.

**Errors**: `409` if the code already exists, or if the new `capacity` is lower than the number of registered students · `404` if the id does not exist · `400` if the data is invalid.

---

## 3. Registrations — `/api/registrations`

| Method | URL | Description | Success |
|---|---|---|---|
| POST | `/api/registrations` | Register a student to a module | `201` |
| GET | `/api/registrations/{id}` | Get one registration | `200` |
| GET | `/api/registrations?studentId=&courseId=` | List registrations (optional filters) | `200` |

**Payload (POST)**

| Field | Type | Rules |
|---|---|---|
| `studentId` | long | required |
| `courseId` | long | required |

```json
POST /api/registrations
{
  "studentId": 1,
  "courseId": 1
}
```

**Response `201`**
```json
{
  "id": 1,
  "studentId": 1,
  "studentName": "Sara Bennani",
  "courseId": 1,
  "courseCode": "MS101",
  "registeredAt": "2026-11-17T14:57:46.766697"
}
```

**Filter examples**
- `GET /api/registrations?courseId=1` → students registered to module 1
- `GET /api/registrations?studentId=1` → modules of student 1

**Errors**
- `404`: student or module does not exist
- `409`: student already registered to this module
- `409`: module full

```json
{ "type": "about:blank", "title": "Conflict", "status": 409,
  "detail": "Le module MS101 est complet (capacité 30)", "instance": "/api/registrations" }
```

---

## 4. Exams — `/api/exams`

| Method | URL | Description | Success |
|---|---|---|---|
| POST | `/api/exams` | Schedule an exam for a module | `201` |
| GET | `/api/exams/{id}` | Get one exam | `200` |
| GET | `/api/exams?courseId=` | List exams (optional filter, sorted by date) | `200` |

**Payload (POST)**

| Field | Type | Rules |
|---|---|---|
| `courseId` | long | required |
| `title` | string | required, max 150 |
| `examDate` | datetime `yyyy-MM-ddTHH:mm:ss` | required |
| `room` | string | optional, max 100 |

```json
POST /api/exams
{
  "courseId": 1,
  "title": "Examen final",
  "examDate": "2026-12-20T09:00:00",
  "room": "Amphi A"
}
```

**Response `201`**
```json
{
  "id": 1,
  "courseId": 1,
  "courseCode": "MS101",
  "title": "Examen final",
  "examDate": "2026-12-20T09:00:00",
  "room": "Amphi A"
}
```

**Errors**: `404` if the module does not exist · `400` if the data is invalid.

---

## 5. Grades — `/api/grades`

| Method | URL | Description | Success |
|---|---|---|---|
| POST | `/api/grades` | Record a grade | `201` |
| PUT | `/api/grades/{id}` | Correct a grade | `200` |
| GET | `/api/grades/{id}` | Get one grade | `200` |
| GET | `/api/grades?examId=&studentId=` | List grades (optional filters) | `200` |

**Payload (POST)**

| Field | Type | Rules |
|---|---|---|
| `examId` | long | required |
| `studentId` | long | required |
| `score` | decimal | required, 0 to 20, max 2 decimals |

```json
POST /api/grades
{
  "examId": 1,
  "studentId": 1,
  "score": 16.5
}
```

**Response `201`**
```json
{
  "id": 1,
  "examId": 1,
  "examTitle": "Examen final",
  "courseCode": "MS101",
  "studentId": 1,
  "studentName": "Sara Bennani",
  "score": 16.5,
  "recordedAt": "2026-11-17T14:57:47.022707"
}
```

**Payload (PUT `/api/grades/{id}`)**
```json
{ "score": 17 }
```
**Response `200`**: same object, with the new `score` and `recordedAt`.

**Filter examples**
- `GET /api/grades?examId=1` → all grades for an exam
- `GET /api/grades?studentId=1` → all grades for a student

**Errors**
- `404`: exam, student or grade does not exist
- `409`: student is not registered to the exam's module
- `409`: a grade already exists for this student and exam (use PUT to change it)
- `400`: score outside 0 to 20

---

## Typical test scenario (Postman / curl)

1. `POST /api/students` → `id = 1`
2. `POST /api/courses` → `id = 1`
3. `POST /api/registrations` `{ "studentId": 1, "courseId": 1 }`
4. `POST /api/exams` `{ "courseId": 1, ... }` → `id = 1`
5. `POST /api/grades` `{ "examId": 1, "studentId": 1, "score": 16.5 }`
6. `GET /api/courses/1` → `enrolled: 1`
7. `GET /api/grades?studentId=1`

```bash
curl -X POST localhost:8080/api/students -H 'Content-Type: application/json' \
  -d '{"firstName":"Sara","lastName":"Bennani","email":"sara@ecole.ma","birthDate":"2002-03-01"}'
```
