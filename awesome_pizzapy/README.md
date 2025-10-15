# AwesomePizzaPy

AwesomePizzaPy is a FastAPI reimplementation of the AwesomePizza backend. It mirrors the REST API surface, data model and business rules of the original Spring Boot service while adopting a Pythonic stack (FastAPI, SQLAlchemy, Alembic).

## Main features

- Full CRUD for pizzas, including dietary flags and ingredient composition.
- Order management workflow with guarded status transitions (`CREATED → IN_PREPARATION → READY → DELIVERED`).
- Conflict detection when multiple orders attempt to enter preparation simultaneously.
- PostgreSQL persistence with Alembic migrations and seed data mirroring the original schema and bootstrap scripts.
- Configurable via environment variables compatible with the Spring Boot setup.

## Project layout

```
awesome_pizzapy/
├── app/
│   ├── api/               # FastAPI routers and dependency wiring
│   ├── core/              # Settings, enums and exception hierarchy
│   ├── db/                # SQLAlchemy engine and session helpers
│   ├── models/            # ORM models (Pizza, Ingredient, Order, OrderPizza)
│   ├── repositories/      # Data access helpers used by the services
│   ├── schemas/           # Pydantic request/response models
│   └── services/          # Business logic replicating the Java services
├── alembic/               # Database migrations and seed data
├── pyproject.toml         # Project metadata and dependencies
└── README.md              # This document
```

## Running the application

1. Create and activate a Python 3.11 virtual environment.
2. Install dependencies: `pip install -e .` from the `awesome_pizzapy` directory.
3. Configure the database connection by setting one of the following:
   - `DATABASE_URL` (SQLAlchemy format, e.g. `postgresql+psycopg://user:pass@localhost:5432/awesomepizza`), or
   - `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` (same values used by the Java service).
4. Run database migrations: `alembic upgrade head`.
5. Start the API with `uvicorn app.main:app --reload`.

The OpenAPI documentation is available at `http://localhost:8000/docs` once the server is running.

## Testing

Install the optional `dev` dependencies (`pip install -e .[dev]`) and run `pytest`.
