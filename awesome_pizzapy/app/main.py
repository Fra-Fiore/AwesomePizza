from __future__ import annotations

from fastapi import FastAPI

from app.api.routers import orders, pizzas
from app.core.config import settings
from app.core.exceptions import register_exception_handlers
from app.db.base import Base
from app.db.session import engine


def create_app() -> FastAPI:
    app = FastAPI(
        title=settings.app_name,
        docs_url=settings.docs_url,
        redoc_url=settings.redoc_url,
        openapi_url=settings.openapi_url,
    )

    register_exception_handlers(app)

    app.include_router(pizzas.router)
    app.include_router(orders.router)

    return app


def _ensure_schema() -> None:
    Base.metadata.create_all(bind=engine)


app = create_app()


@app.on_event("startup")
async def on_startup():
    _ensure_schema()
