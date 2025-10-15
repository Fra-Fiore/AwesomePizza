from __future__ import annotations

from datetime import datetime
from decimal import Decimal
from time import time
from typing import Sequence

from sqlalchemy.orm import Session

from app.core.exceptions import (
    InvalidOrderStateException,
    OrderAlreadyInPreparationException,
    OrderNotFoundException,
    PizzaNotFoundException,
)
from app.core.status import OrderStatus, is_valid_transition
from app.models.domain import Order, OrderPizza
from app.repositories.orders import OrderRepository
from app.repositories.pizzas import PizzaRepository
from app.schemas.orders import OrderCreate


class OrderService:
    def __init__(
        self,
        session: Session,
        order_repository: OrderRepository | None = None,
        pizza_repository: PizzaRepository | None = None,
    ) -> None:
        self._session = session
        self._orders = order_repository or OrderRepository(session)
        self._pizzas = pizza_repository or PizzaRepository(session)

    def list_orders(self) -> list[Order]:
        return self._orders.list()

    def list_filtered(self, statuses: Sequence[OrderStatus], page: int, size: int) -> tuple[list[Order], int]:
        size = max(size, 1)
        offset = max(page, 0) * size
        items, total = self._orders.list_filtered(statuses, offset=offset, limit=size)
        return items, total

    def get_order(self, order_id: int) -> Order:
        order = self._orders.get(order_id)
        if not order:
            raise OrderNotFoundException(order_id)
        return order

    def get_order_status_by_code(self, order_code: str) -> OrderStatus:
        order = self._orders.get_by_code(order_code)
        if not order:
            raise OrderNotFoundException(order_code)
        return order.status

    def create_order(self, payload: OrderCreate) -> Order:
        pizza_ids = [item.pizza_id for item in payload.pizzas]
        pizzas = self._pizzas.get_many(pizza_ids)
        missing = set(pizza_ids) - set(pizzas.keys())
        if missing:
            raise PizzaNotFoundException(int(next(iter(missing))))

        order = Order(
            status=OrderStatus.CREATED,
            order_code=self._generate_order_code(),
            timestamp=datetime.utcnow(),
            total_price=Decimal("0.00"),
        )

        total = Decimal("0.00")
        order_items: list[OrderPizza] = []

        for item in payload.pizzas:
            pizza = pizzas[item.pizza_id]
            order_pizza = OrderPizza(
                pizza=pizza,
                quantity=item.quantity,
                notes=item.notes,
            )
            order_pizza.order = order
            order_items.append(order_pizza)
            total += pizza.price * item.quantity

        order.pizzas = order_items
        order.total_price = total.quantize(Decimal("0.01"))

        self._orders.add(order)
        self._session.commit()
        self._session.refresh(order)
        return self.get_order(order.order_id)

    def start_order_preparation(self, order_id: int) -> Order:
        if self._orders.exists_in_status(OrderStatus.IN_PREPARATION):
            raise OrderAlreadyInPreparationException()
        return self._update_status(order_id, OrderStatus.IN_PREPARATION)

    def mark_ready(self, order_id: int) -> Order:
        return self._update_status(order_id, OrderStatus.READY)

    def mark_delivered(self, order_id: int) -> Order:
        return self._update_status(order_id, OrderStatus.DELIVERED)

    def _update_status(self, order_id: int, new_status: OrderStatus) -> Order:
        order = self.get_order(order_id)
        if not is_valid_transition(order.status, new_status):
            raise InvalidOrderStateException(order_id, order.status.value, new_status.value)
        order.status = new_status
        self._session.commit()
        self._session.refresh(order)
        return order

    @staticmethod
    def _generate_order_code() -> str:
        return f"ORD-{int(time() * 1000)}"
