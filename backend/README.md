# TechW E-Commerce System

A comprehensive e-commerce backend system built with Spring Boot, providing both admin and public APIs for managing products, categories, brands, orders, and users.

## Features

- **Product Management**: Create, update, delete, and view products with price, stock, and status management.
- **Category & Brand Management**: Organize products dynamically using standalone categories and brands.
- **Order Management**: Process and track customer orders, separating administrative views from specific user histories.
- **Cart & User Profiles** *(Planned)*: User-specific cart items and personalized profile management. 
- **Authentication & Authorization**: Secure access combining public browsing and JWT-based restricted operations for administrators.

## API Endpoints

### Admin APIs (Authentication Required)

#### Product Management
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update an existing product
- `DELETE /api/products/{id}` - Delete a product

#### Category Management
- `POST /api/categories` - Create a new category
- `PUT /api/categories/{id}` - Update an existing category
- `DELETE /api/categories/{id}` - Delete a category

#### Brand Management
- `POST /api/brands` - Create a new brand
- `PUT /api/brands/{id}` - Update an existing brand
- `DELETE /api/brands/{id}` - Delete a brand

#### Order Management
- `GET /api/orders` - Get all orders in the system
- `GET /api/orders/user/{userId}` - Get all orders for a specific user (Authenticated User/Admin)
- `GET /api/orders/{id}` - Get details of a specific order (Authenticated User/Admin)
- `PUT /api/orders/{id}/status` - Update the status of an order (Admin)

#### User Management (Possible/Planned)
- `GET /api/users` - Get a list of all registered users
- `PUT /api/users/{id}/role` - Update a specific user's system role

### Public APIs (No Authentication Required)

#### Authentication
- `POST /api/auth/register` - Register a new user account
- `POST /api/auth/login` - Authenticate a user and receive a JWT token

#### Catalog Information
- `GET /api/products` - Get a paginated list of all active products (Supports `name`, `categoryId` filters)
- `GET /api/products/{id}` - Get product details by ID
- `GET /api/categories` - Get all available categories
- `GET /api/brands` - Get all available brands

#### Cart Management (Possible/Planned)
*(Currently missing controllers, but supported by existing data models)*
- `GET /api/cart` - Retrieve the current logged-in user's shopping cart.
- `POST /api/cart/items` - Add a product to the cart.
- `PUT /api/cart/items/{itemId}` - Update the quantity of a specific item in the cart.
- `DELETE /api/cart/items/{itemId}` - Remove a specific item from the cart.
- `DELETE /api/cart` - Clear the entire user cart.
- `POST /api/checkout` - Convert the current `Cart` to an `Order`.

## Data Models

### Catalog Models
- `Product`: Core sales item tracking stock, price, activation status, images, and relationships.
- `Category`: Represents groupings of products for better navigation.
- `Brand`: Represents manufacturers or labels associated with products.

### Commerce Models
- `Cart`: Active shopping sessions linked to users.
- `CartItem`: Specific products and quantities within a Cart.
- `Order`: Completed transactions tracking final pricing, dates, and status.
- `OrderItem`: Specific products locked at a purchased price for historical tracking.

### User & Security Models
- `User`: Identity details including ID (UUID), Email, Name, and Role.
- `Role`: Enum values (`USER`, `ADMIN`) governing access permissions.

## Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: SQL Server with JPA/Hibernate
- **Authentication**: JWT with Spring Security
- **Data Validation**: Jakarta Validations
- **Build Tool**: Gradle

## Getting Started

### Prerequisites
- Java 17 or higher
- SQL Server
- Gradle 8.x

### Installation

1. Clone the repository
2. Configure database connection in `src/main/resources/application.properties`
3. Optional: Set `spring.jpa.hibernate.ddl-auto=create` on the first run to build schema. Revert to `update` afterward.
4. Start the application: `.\gradlew.bat bootRun`

### Configuration

Key configuration properties:
- `spring.datasource.url` - Database connection URL
- `spring.jpa.hibernate.ddl-auto` - Database schema generation mode
- JWT keys and secrets (Configured in custom Security classes)

## Security

- JWT-based authentication for administrative and user-specific APIs.
- Public APIs (Products, Categories, Brands, Auth) are accessible without authentication.
- Role-based access control (`@PreAuthorize("hasRole('ADMIN')")`).
- Passwords stored securely using BCrypt hashing.

## Contributing

1. Follow the established coding conventions
2. Use the DTO → Model → Entity pattern
3. Ensure to avoid lazy-loading serialization bugs (e.g., using `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})` on `FetchType.LAZY` relations).
4. Write unit tests for new functionality.
