# Clinic Management API

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0.2-green)
![Maven](https://img.shields.io/badge/build-Maven-red)
![Database](https://img.shields.io/badge/Database-MySQL-orange)
![Flyway](https://img.shields.io/badge/Migrations-Flyway-red)

REST API for managing patients, doctors, and medical appointments built with Spring Boot.

> **Note:** The codebase and API fields use Spanish naming conventions.

---

## 📌 Features

- Full CRUD for patients and doctors
- Appointment scheduling and cancellation
- Input validation using Bean Validation
- Global exception handling with `@RestControllerAdvice`
- Structured error responses
- DTO pattern (separation between domain model and API layer)
- Logging with SLF4J
- Transaction management with `@Transactional`
- Soft delete (logical deactivation instead of physical deletion)
- Database versioning with Flyway migrations
- Data persistence using JPA + MySQL
- H2 in-memory database for testing
- API documentation with Swagger / OpenAPI
- Stateless authentication with Spring Security + JWT

---

## 🏗 Architecture

Layered architecture:

```
Controller → Service → Repository → Database
```

Clear separation of responsibilities:

- `controller` → REST API endpoints
- `service` → business logic
- `repository` → data access layer
- `domain` → entities, DTOs, and validations
- `infra/errores` → centralized exception handling
- `infra/security` → JWT authentication and filters
- `infra/springdoc` → Swagger/OpenAPI configuration
- `resources/db/migration` → Flyway SQL migration scripts

---

## 🛠 Technologies

- Java 17
- Spring Boot 3.0.2
- Spring Data JPA / Hibernate
- Spring Security
- JWT (Auth0 java-jwt 4.2.1)
- MySQL 8
- Flyway
- H2 (testing)
- Jakarta Bean Validation
- Lombok 1.18.30
- Maven
- Swagger / OpenAPI (SpringDoc 2.1.0)
- SLF4J

---

## ⚙ Requirements

- Java 17+
- Maven 3.9+
- MySQL 8+

---

## 🗄 Database Configuration

Create the database:

```sql
CREATE DATABASE vollmed_api;
```

Configure `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vollmed_api
    username: root
    password: your_password
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

api:
  security:
    secret: your_jwt_secret
    token:
      expiracion-horas: 2
      zona-horaria: "-03:00"
```

Flyway will automatically run the migration scripts on startup. No manual schema creation needed beyond the database itself.

---

## 📦 Database Migrations (Flyway)

| Version | Description |
|---------|-------------|
| V1 | Create `medicos` table |
| V2 | Add `telefono` column to `medicos` |
| V3 | Add `activo` column to `medicos` |
| V4 | Create `usuarios` table |
| V5 | Create `pacientes` table |
| V6 | Create `consultas` table |
| V7 | Seed data for `pacientes` |
| V8 | Seed data for `medicos` |
| V9 | Seed data for `usuarios` |

---

## 🚀 Running the Project

Clone the repository:

```bash
git clone https://github.com/cfaviero/clinic-management-api.git
cd clinic-management-api
```

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

The API will start at:

```
http://localhost:8080
```

Swagger documentation:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Authentication

All endpoints except `/login` require a Bearer JWT token.

**Login:**

```
POST /login
```

Request body:

```json
{
  "login": "user@email.com",
  "clave": "yourpassword"
}
```

Response:

```json
{
  "jwTtoken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Use the token in the `Authorization` header for all subsequent requests:

```
Authorization: Bearer <token>
```

---

## 📡 API Endpoints

### 👤 Patients — `/pacientes`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/pacientes` | Register a new patient |
| GET | `/pacientes` | List all active patients (paginated) |
| GET | `/pacientes/{id}` | Get patient details by ID |
| PUT | `/pacientes` | Update patient data |
| DELETE | `/pacientes/{id}` | Deactivate a patient |

**Register patient — request body:**

```json
{
  "nombre": "John Doe",
  "email": "john.doe@email.com",
  "telefono": "37612345678",
  "documento": "12345678",
  "direccion": {
    "calle": "Main Street",
    "numero": "123",
    "complemento": "Apt 4B",
    "distrito": "Downtown",
    "ciudad": "Buenos Aires"
  }
}
```

---

### 🩺 Doctors — `/medicos`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/medicos` | Register a new doctor |
| GET | `/medicos` | List all active doctors (paginated) |
| GET | `/medicos/{id}` | Get doctor details by ID |
| PUT | `/medicos` | Update doctor data |
| DELETE | `/medicos/{id}` | Deactivate a doctor |

**Register doctor — request body:**

```json
{
  "nombre": "Dr. Jane Smith",
  "email": "jane.smith@clinic.com",
  "telefono": "37698765432",
  "documento": "987654",
  "especialidad": "CARDIOLOGIA",
  "direccion": {
    "calle": "Medical Ave",
    "numero": "456",
    "complemento": "",
    "distrito": "Health District",
    "ciudad": "Buenos Aires"
  }
}
```

Available specialties: `ORTOPEDIA`, `CARDIOLOGIA`, `GINECOLOGIA`, `PEDIATRIA`

---

### 📅 Appointments — `/consultas`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/consultas` | List all appointments (paginated) |
| POST | `/consultas` | Schedule a new appointment |
| DELETE | `/consultas/{id}` | Cancel an appointment |

**Schedule appointment — request body:**

```json
{
  "idPaciente": 1,
  "idMedico": 2,
  "fecha": "2026-04-15T10:00:00",
  "especialidad": "CARDIOLOGIA"
}
```

> `idMedico` is optional. If not provided, a doctor will be selected automatically based on `especialidad`.

**Cancel appointment — request body:**

```json
{
  "motivo": "PACIENTE_DESISTIO"
}
```

Available cancellation reasons: `PACIENTE_DESISTIO`, `MEDICO_CANCELO`, `OTROS`

---

## 🧠 Business Rules

- Appointments can only be scheduled Monday to Saturday, from 07:00 to 19:00.
- Appointments must be scheduled at least 30 minutes in advance.
- A patient can only have one appointment per day.
- A doctor cannot have two appointments at the same time.
- Appointments can only be cancelled with at least 24 hours notice.
- Inactive doctors and patients cannot be assigned to appointments.

---

## ❌ Error Handling

**404 — Resource not found:**

```json
{}
```

**400 — Validation error:**

```json
[
  {
    "campo": "nombre",
    "error": "must not be blank"
  }
]
```

**400 — Business rule violation:**

```
"el paciente ya tiene una consulta para ese dia"
```

---

## 🧠 Technical Decisions

- DTOs used to prevent exposing JPA entities directly in API responses
- Strategy pattern for appointment validations — easy to add new rules without modifying existing code
- Global exception handling via `@RestControllerAdvice`
- Enums persisted as `STRING` to avoid issues when reordering values
- Soft delete with `activo` flag instead of physical deletion
- `@Transactional(readOnly = true)` on read-only operations for performance
- JWT token expiration and timezone externalized to `application.yml`
- Flyway for database version control — schema changes are tracked and reproducible
