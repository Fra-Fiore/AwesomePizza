from __future__ import annotations

from datetime import datetime, timezone
from http import HTTPStatus
from typing import Callable

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse


class AwesomePizzaException(RuntimeError):
    status_code: int = HTTPStatus.INTERNAL_SERVER_ERROR

    def __init__(self, message: str):
        super().__init__(message)
        self.message = message


class PizzaNotFoundException(AwesomePizzaException):
    status_code = HTTPStatus.NOT_FOUND

    def __init__(self, pizza_id: int):
        super().__init__(f"Pizza with id {pizza_id} not found")


class IngredientNotFoundException(AwesomePizzaException):
    status_code = HTTPStatus.NOT_FOUND

    def __init__(self, ingredient_id: int):
        super().__init__(f"Ingredient with id {ingredient_id} not found")


class OrderNotFoundException(AwesomePizzaException):
    status_code = HTTPStatus.NOT_FOUND

    def __init__(self, order_identifier: int | str):
        super().__init__(f"Order with identifier {order_identifier} not found")


class InvalidOrderStateException(AwesomePizzaException):
    status_code = HTTPStatus.CONFLICT

    def __init__(self, order_id: int, current: str, attempted: str):
        super().__init__(
            f"Invalid status transition for order {order_id}: {current} -> {attempted}"
        )


class OrderAlreadyInPreparationException(AwesomePizzaException):
    status_code = HTTPStatus.CONFLICT

    def __init__(self):
        super().__init__("There is already an order in IN_PREPARATION status")


def _build_error_response(status: HTTPStatus, message: str) -> JSONResponse:
    return JSONResponse(
        status_code=status.value,
        content={
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "status": status.value,
            "error": status.phrase,
            "message": message,
        },
    )


def register_exception_handlers(app: FastAPI) -> None:
    @app.exception_handler(AwesomePizzaException)
    async def handle_domain_exception(request: Request, exc: AwesomePizzaException):
        status = HTTPStatus(exc.status_code)
        return _build_error_response(status, exc.message)

    @app.exception_handler(Exception)
    async def handle_generic_exception(request: Request, exc: Exception):
        return _build_error_response(HTTPStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")


__all__ = [
    "AwesomePizzaException",
    "PizzaNotFoundException",
    "IngredientNotFoundException",
    "OrderNotFoundException",
    "InvalidOrderStateException",
    "OrderAlreadyInPreparationException",
    "register_exception_handlers",
]
