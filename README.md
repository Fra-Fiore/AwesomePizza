# AwesomePizza

AwesomePizza is a Spring Boot (3.4) application that exposes a REST API for managing a pizza catalog and the related order workflow. It was originally developed as part of a technical interview exercise and is now being prepared for public sharing.

## High-level architecture

| Layer | Responsibilities | Key classes |
|-------|------------------|-------------|
| Web / API | REST controllers, OpenAPI metadata, validation | `PizzaController`, `OrderController` |
| Service | Orchestrates repository access, business rules, logging | `PizzaServiceImpl`, `OrderServiceImpl` |
| Persistence | Spring Data JPA repositories mapped to PostgreSQL tables | `PizzaRepository`, `OrderRepository`, `IngredientRepository` |
| Domain | JPA entities and enums modelling pizzas, ingredients, orders | `Pizza`, `Ingredient`, `Order`, `OrderPizza`, `OrderStatus` |
| DTO / Mapper | Request/response payloads and mapper components | `PizzaRequestDTO`, `OrderRequestDTO`, `PizzaMapper`, `OrderMapper` |

Supporting modules include:

- **Validation and error handling** – Bean Validation annotations on DTOs plus a `GlobalExceptionHandler` that turns domain exceptions into HTTP responses.
- **OpenAPI documentation** – Powered by `springdoc-openapi-starter-webmvc-ui`. Swagger UI is available at [`/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html).
- **Database bootstrapping** – SQL scripts under `config/database/init` populate a PostgreSQL schema with sample pizzas and ingredients.

## Running the application

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker (optional, for containerised setup)

### Run with Maven (local database)

```bash
# Configure environment variables (example values)
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/awesomepizza
export SPRING_DATASOURCE_USER=postgres
export SPRING_DATASOURCE_PASSWORD=postgres

mvn spring-boot:run
```

The API will be available at `http://localhost:8080` and logs are configured at DEBUG level for the application packages.

### Run with Docker Compose (application + PostgreSQL)

```bash
# Optionally create a .env file with the following variables:
# POSTGRES_USER=postgres
# POSTGRES_PASSWORD=postgres
# POSTGRES_DB=awesomepizza

docker compose up --build
```

- PostgreSQL is exposed on `localhost:5432` and seeded with demo data.
- The Spring Boot service is exposed on `localhost:8080`.

### Useful endpoints

- `GET /pizzas` – list all pizzas.
- `POST /pizzas` – create a pizza. Returns `201 Created` and sets the `Location` header.
- `GET /orders` – list every order.
- `GET /orders/search?status=CREATED&status=READY` – filter orders by status with pagination support.
- `POST /orders` – create an order from one or more pizzas. Returns `201 Created`.
- `POST /orders/{id}/start-preparation` – begin preparing an order (ensures only one order is in preparation at a time).
- `GET /orders/{orderCode}/status` – check the status of an order using its public code.

Refer to Swagger UI for the complete contract and sample payloads.

## Database schema

The SQL DDL in `config/database/init/01-DDL.sql` creates the following tables:

- `pizza`, `ingredient` and the join table `pizza_ingredient` modelling the catalog.
- `order`, `order_pizza` storing orders and the pizzas included in each order.

`02-DML.sql` inserts sample pizzas, ingredients and relationships. You can modify or extend these scripts to customise the demo dataset.

## Testing

Unit and web layer tests are located under `src/test/java`.

```bash
mvn test
```

The suite uses Mockito for service isolation and Spring MVC slice tests for the controllers.

## Project structure

```
src/
├── main
│   ├── java/com/francescofiore/awesomepizza
│   │   ├── controller        # REST controllers
│   │   ├── dto               # Request/response DTOs
│   │   ├── entity            # JPA entities
│   │   ├── exception         # Domain-specific exceptions + handler
│   │   ├── mapper            # DTO ↔ entity mapping utilities
│   │   ├── repository        # Spring Data repositories
│   │   ├── service           # Business logic interfaces & implementations
│   │   └── util              # Order status helpers
│   └── resources             # Application configuration
└── test                      # Service and controller tests
```

## Known issues & improvement opportunities

The review uncovered a few areas worth addressing before using the project in production:

1. **Incomplete entity mapping** – The `ingredient` table includes an `allergene` column that is not mapped in the `Ingredient` entity. Exposing this information (and aligning the entity with the schema) would enable allergen-aware features.
2. **Order update workflow** – `OrderServiceImpl.updateOrder` replaces the `OrderPizza` collection without re-attaching the parent order, which may lead to detached entities or missing cascade updates. The method should iterate through the incoming items, set `order` on each `OrderPizza` and consider merging vs. replacing existing entries.
3. **Pizza updates** – `PizzaServiceImpl.updatePizza` currently updates only the name, price and ingredients. Description, dietary flags and image URL remain stale. Extending the update logic (and adding validation around ingredient IDs) would make the endpoint more consistent.
4. **Validation coverage** – Additional constraints (e.g., enforcing non-empty pizza descriptions, checking that requested pizzas exist when creating orders) could be added at the DTO level to fail fast before hitting the database.
5. **Test coverage** – The project currently includes unit tests and controller slice tests. Adding integration tests (using Testcontainers for PostgreSQL) would increase confidence in the JPA mappings and database scripts.

## Contributing

Feel free to fork the repository, open issues for bugs or improvements, and submit pull requests. Suggestions around expanding the domain (e.g., promotions, customer profiles) or hardening the API (authentication, rate limiting) are especially welcome.
