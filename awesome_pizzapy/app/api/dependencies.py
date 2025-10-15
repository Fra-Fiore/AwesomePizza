from __future__ import annotations

from fastapi import Depends
from sqlalchemy.orm import Session

from app.db.session import get_session
from app.services.orders import OrderService
from app.services.pizzas import PizzaService


def get_db_session() -> Session:
    yield from get_session()


def get_pizza_service(session: Session = Depends(get_db_session)) -> PizzaService:
    return PizzaService(session)


def get_order_service(session: Session = Depends(get_db_session)) -> OrderService:
    return OrderService(session)
