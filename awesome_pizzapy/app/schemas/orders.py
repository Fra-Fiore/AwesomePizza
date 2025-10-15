from __future__ import annotations

from datetime import datetime
from typing import List, Optional

from pydantic import BaseModel, Field, conint

from app.core.status import OrderStatus


class OrderPizzaPayload(BaseModel):
    pizza_id: conint(ge=1)
    quantity: conint(ge=1)
    notes: Optional[str] = Field(default=None, max_length=255)


class OrderCreate(BaseModel):
    pizzas: List[OrderPizzaPayload] = Field(..., min_items=1)


class OrderPizzaResponse(BaseModel):
    pizza_name: str
    quantity: int
    notes: Optional[str]


class OrderResponse(BaseModel):
    id: int
    order_code: str
    timestamp: datetime
    status: OrderStatus
    total_price: float
    pizzas: List[OrderPizzaResponse]

    model_config = dict(from_attributes=True)


class PaginatedOrders(BaseModel):
    items: List[OrderResponse]
    total: int
    page: int
    size: int


def serialize_order(order) -> OrderResponse:
    return OrderResponse(
        id=order.order_id,
        order_code=order.order_code,
        timestamp=order.timestamp,
        status=order.status,
        total_price=float(order.total_price),
        pizzas=[
            OrderPizzaResponse(
                pizza_name=order_pizza.pizza.name,
                quantity=order_pizza.quantity,
                notes=order_pizza.notes,
            )
            for order_pizza in order.pizzas
        ],
    )
