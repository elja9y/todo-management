# Todo Management API

A RESTful API for managing todos with JWT-based authentication and role-based access control, built with Spring Boot.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security + JWT (jjwt) |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL |
| Mapping | MapStruct |
| Boilerplate | Lombok |
| Build | Maven |

---

## Requirements

### Requirement 1 — Todo REST APIs

Full CRUD operations on todos:

- Add Todo
- Get Todo by ID
- Get All Todos
- Update Todo
- Delete Todo
- Complete Todo
- Incomplete Todo

### Requirement 2 — Role-Based Authorization

| Endpoint | ADMIN | USER |
|----------|-------|------|
| Add Todo | ✅ | ❌ |
| Get Todo | ✅ | ✅ |
| Get All Todos | ✅ | ✅ |
| Update Todo | ✅ | ❌ |
| Delete Todo | ✅ | ❌ |
| Complete Todo | ✅ | ✅ |
| Incomplete Todo | ✅ | ✅ |

### Requirement 3 — Auth REST APIs

- Register — creates a new user with `ROLE_USER` assigned automatically
- Login — authenticates and returns a JWT token

---

## Project Structure

```
src/main/java/com/github/elja9y/todo/
├── config/
│   └── SpringSecurityConfig.java       # Security filter chain, beans
├── controller/
│   ├── AuthController.java             # /api/auth/**
│   └── TodoController.java             # /api/todos/**
├── dto/
│   ├── auth/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── JwtAuthResponse.java
│   └── todo/
│       ├── CreateTodoRequest.java
│       ├── UpdateTodoRequest.java
│       └── TodoResponse.java
├── entity/
│   ├── Todo.java
│   ├── User.java
│   └── Role.java
├── exception/
│   ├── AppException.java               # Base exception class
│   ├── TodoException.java
│   ├── UserException.java
│   ├── RoleException.java
│   ├── ErrorDetails.java
│   └── GlobalExceptionHandler.java
├── mapper/
│   ├── TodoStructMapper.java           # MapStruct mapper
│   └── UserStructMapper.java
├── repository/
│   ├── TodoRepository.java
│   ├── UserRepository.java
│   └── RoleRepository.java
├── security/
│   ├── JwtTokenProvider.java           # Generate, validate, parse JWT
│   ├── JwtAuthenticationFilter.java    # Intercept and validate token per request
│   ├── JwtAuthenticationEntryPoint.java # Return 401 on unauthorized
│   └── CustomUserDetailsService.java   # Load user from DB for Spring Security
└── service/
    ├── AuthService.java
    ├── TodoService.java
    └── impl/
        ├── AuthServiceImpl.java
        └── TodoServiceImpl.java
```

---

## API Endpoints

### Auth

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and receive JWT token |

### Todos

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/todos` | ADMIN | Create a new todo |
| GET | `/api/todos/{id}` | ADMIN, USER | Get todo by ID |
| GET | `/api/todos` | ADMIN, USER | Get all todos |
| PUT | `/api/todos/{id}` | ADMIN | Update todo |
| DELETE | `/api/todos/{id}` | ADMIN | Delete todo |
| PATCH | `/api/todos/{id}/complete` | ADMIN, USER | Mark todo as complete |
| PATCH | `/api/todos/{id}/incomplete` | ADMIN, USER | Mark todo as incomplete |

---

## Authentication Flow

```
POST /api/auth/login  { usernameOrEmail, password }
        ↓
AuthenticationManager.authenticate()
        ↓
CustomUserDetailsService.loadUserByUsername() → DB lookup
        ↓
BCrypt password comparison
        ↓
JwtTokenProvider.generateToken() → signed JWT
        ↓
Response: { accessToken: "eyJ...", tokenType: "Bearer" }

— Subsequent Requests —

GET /api/todos  +  Authorization: Bearer eyJ...
        ↓
JwtAuthenticationFilter
  → validates token signature and expiry
  → extracts username
  → loads UserDetails (DB)
  → sets authentication in SecurityContextHolder
        ↓
@PreAuthorize checks role
        ↓
Controller → Service → Repository → Response
```

---

## Exception Handling

All exceptions extend `AppException` which carries `message`, `errorCode`, and `HttpStatus`. A single `@ExceptionHandler(AppException.class)` in `GlobalExceptionHandler` catches all module exceptions and returns a structured `ErrorDetails` response.

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "message": "Todo does not exist",
  "details": "uri=/api/todos/99",
  "errorCode": "TODO_NOT_FOUND"
}
```

| Exception Class | Error Codes |
|----------------|-------------|
| `TodoException` | `TODO_NOT_FOUND` |
| `UserException` | `USER_NOT_FOUND`, `DUPLICATED_USERNAME`, `DUPLICATED_EMAIL` |
| `RoleException` | `ROLE_NOT_FOUND` |

---

## Setup

### Prerequisites

- Java 25
- MySQL
- Maven

### Database

```sql
CREATE DATABASE todo_management;

INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

### Environment Variables

| Variable | Description |
|----------|-------------|
| `DB_PASSWORD` | MySQL root password |
| `JWT_SECRET` | Base64-encoded secret key (min 32 chars before encoding) |

### application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todo_management
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update

app.jwt-secret=${JWT_SECRET}
app.jwt-expiration-milliseconds=604800000
```

### Run

```bash
mvn spring-boot:run
```

---

## Request Examples

### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "Ahmed",
  "username": "ahmed",
  "email": "ahmed@mail.com",
  "password": "password123"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "ahmed",
  "password": "password123"
}
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

### Authenticated Request
```http
GET /api/todos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Notes

- Roles must be seeded manually in the DB before registering users — registration auto-assigns `ROLE_USER`
- `completed` field is server-controlled and excluded from `UpdateTodoRequest` — use the dedicated complete/incomplete endpoints
- JWT expiry is set to 7 days (604800000ms) by default
