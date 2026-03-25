# TechW E-Commerce System

A comprehensive e-commerce backend system built with Spring Boot, providing both admin and public APIs for managing products, categories, brands, orders, carts, and users.

## Features

- **Product Management**: CRUD operations with validation, filtering by name/category, and pagination.
- **Category & Brand Management**: Organize products with full CRUD.
- **Order Management**: Create orders via checkout, cancel/edit PENDING orders, admin status updates.
- **Cart Management**: Add/remove items, update quantities, checkout to create orders.
- **User Profiles**: View and update profile (name, avatar).
- **Authentication & Authorization**: JWT-based auth with BCrypt password hashing.
- **Role-based Access Control**: `USER` and `ADMIN` roles with endpoint-level security.
- **Input Validation**: `@NotBlank`, `@Size`, `@Min`, `@DecimalMin` on all request DTOs.
- **Custom Exceptions**: `ResourceNotFoundException` (404), `BusinessRuleException` (400), `DuplicateResourceException` (409).
- **Standardized API Response**: All endpoints return `ApiResponse<T>` with `{ code, message, data }`.
- **Swagger/OpenAPI**: Interactive API documentation at `/swagger-ui.html`.
- **Spring Profiles**: Separate `dev` and `prod` configurations.
- **Unit Tests**: Mockito-based tests for services (Cart, User, Product, Auth).

## Technology Stack

- **Backend**: Spring Boot 3.2.3 (Java 17)
- **Database**: PostgreSQL 15
- **Authentication**: JWT (jjwt) + Spring Security + BCrypt
- **Validation**: Jakarta Bean Validation
- **API Docs**: springdoc-openapi (Swagger UI)
- **Testing**: JUnit 5 + Mockito
- **Build Tool**: Gradle
- **Containerization**: Docker + Docker Compose

## Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose (recommended)
- OR PostgreSQL 15+ (for local development without Docker)

### Quick Start with Docker

```bash
# Clone and start all services (DB + Backend + Frontend)
docker compose up --build
```

- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Frontend**: http://localhost:5173

### Local Development (without Docker)

1. Install and start PostgreSQL, create database `TechW`
2. Update `application-dev.properties` with your local DB connection
3. Run the backend:

```bash
cd backend
.\gradlew.bat bootRun
```

4. Run the frontend:

```bash
cd frontend
npm install
npm run dev
```

### Spring Profiles

| Profile | DB Config | SQL Logging | DDL Mode | Data Seed |
|---------|-----------|-------------|----------|-----------|
| `dev` (default) | Local/Docker | ✅ Enabled | `update` | ✅ Enabled |
| `prod` | Env vars (`DB_URL`, `DB_USER`, `DB_PASS`) | ❌ Disabled | `validate` | ❌ Disabled |

Switch profiles:
```bash
# Run with prod profile
.\gradlew.bat bootRun --args='--spring.profiles.active=prod'

# Or via environment variable
set SPRING_PROFILES_ACTIVE=prod
```

## API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Products (Public GET, Admin POST/PUT/DELETE)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List products (filter: `name`, `categoryId`) |
| GET | `/api/products/{id}` | Get product details |
| POST | `/api/products` | Create product (Admin) |
| PUT | `/api/products/{id}` | Update product (Admin) |
| DELETE | `/api/products/{id}` | Delete product (Admin) |

### Cart (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cart` | Get current user's cart |
| POST | `/api/cart/items` | Add item to cart |
| PUT | `/api/cart/items/{itemId}` | Update item quantity |
| DELETE | `/api/cart/items/{itemId}` | Remove item |
| DELETE | `/api/cart` | Clear cart |
| POST | `/api/cart/checkout` | Checkout → create order |

### Orders (Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | All orders (Admin) |
| GET | `/api/orders/my-orders` | Current user's orders |
| GET | `/api/orders/{id}` | Order details |
| PUT | `/api/orders/{id}/status` | Update status (Admin) |
| PUT | `/api/orders/{id}/cancel` | Cancel own PENDING order |
| PUT | `/api/orders/{id}/update` | Edit own PENDING order |

### Users (Admin)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List all users (Admin) |
| PUT | `/api/users/{id}/role` | Change user role (Admin) |
| GET | `/api/users/me` | Get own profile |
| PUT | `/api/users/me` | Update own profile |

> **Full interactive docs**: See Swagger UI at `/swagger-ui.html`

## Running Tests

```bash
cd backend
.\gradlew.bat test
```

Test classes:
- `CartServiceTest` — Cart operations & checkout
- `UserServiceTest` — User listing & role updates
- `ProductServiceTest` — CRUD, validation, not-found scenarios
- `AuthServiceTest` — Register, login, duplicate email, bad credentials

## Security Notes

- JWT tokens expire after 24 hours (configurable via `jwt.expiration`)
- Passwords stored with BCrypt hashing
- JWT secret should be set via `JWT_SECRET` environment variable in production
- Public APIs: products (GET), categories (GET), brands (GET), auth, Swagger UI
- Protected APIs require `Authorization: Bearer <token>` header
