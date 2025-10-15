from __future__ import annotations

from collections.abc import Sequence

from sqlalchemy import Select, func, select
from sqlalchemy.orm import Session, selectinload

from app.core.status import OrderStatus
from app.models.domain import Order, OrderPizza


class OrderRepository:
    def __init__(self, session: Session):
        self._session = session

    def _base_query(self) -> Select[tuple[Order]]:
        return (
            select(Order)
            .options(
                selectinload(Order.pizzas).selectinload(OrderPizza.pizza),
            )
            .order_by(Order.order_id)
        )

    def list(self) -> list[Order]:
        stmt = self._base_query()
        return self._session.execute(stmt).unique().scalars().all()

    def list_filtered(self, statuses: Sequence[OrderStatus], offset: int, limit: int) -> tuple[list[Order], int]:
        stmt = self._base_query()
        count_stmt = select(func.count(Order.order_id))

        if statuses:
            stmt = stmt.where(Order.status.in_(statuses))
            count_stmt = count_stmt.where(Order.status.in_(statuses))

        total = self._session.execute(count_stmt).scalar_one()
        items = (
            self._session.execute(stmt.offset(offset).limit(limit))
            .unique()
            .scalars()
            .all()
        )
        return items, total

    def get(self, order_id: int) -> Order | None:
        stmt = self._base_query().where(Order.order_id == order_id)
        return self._session.execute(stmt).unique().scalar_one_or_none()

    def get_by_code(self, order_code: str) -> Order | None:
        stmt = self._base_query().where(Order.order_code == order_code)
        return self._session.execute(stmt).unique().scalar_one_or_none()

    def exists_in_status(self, status: OrderStatus) -> bool:
        stmt = select(func.count(Order.order_id)).where(Order.status == status)
        return self._session.execute(stmt).scalar_one() > 0

    def add(self, order: Order) -> Order:
        self._session.add(order)
        return order

    def remove(self, order: Order) -> None:
        self._session.delete(order)
