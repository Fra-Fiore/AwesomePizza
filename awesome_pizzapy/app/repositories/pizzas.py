from __future__ import annotations

from sqlalchemy import select
from sqlalchemy.orm import Session, selectinload

from app.models.domain import Pizza


class PizzaRepository:
    def __init__(self, session: Session):
        self._session = session

    def list(self) -> list[Pizza]:
        stmt = (
            select(Pizza)
            .options(selectinload(Pizza.ingredients))
            .order_by(Pizza.pizza_id)
        )
        return self._session.execute(stmt).unique().scalars().all()

    def get(self, pizza_id: int) -> Pizza | None:
        stmt = (
            select(Pizza)
            .options(selectinload(Pizza.ingredients))
            .where(Pizza.pizza_id == pizza_id)
        )
        return self._session.execute(stmt).unique().scalar_one_or_none()

    def get_many(self, pizza_ids: list[int]) -> dict[int, Pizza]:
        if not pizza_ids:
            return {}
        stmt = (
            select(Pizza)
            .options(selectinload(Pizza.ingredients))
            .where(Pizza.pizza_id.in_(pizza_ids))
        )
        pizzas = self._session.execute(stmt).unique().scalars().all()
        return {pizza.pizza_id: pizza for pizza in pizzas}

    def add(self, pizza: Pizza) -> Pizza:
        self._session.add(pizza)
        return pizza

    def remove(self, pizza: Pizza) -> None:
        self._session.delete(pizza)
