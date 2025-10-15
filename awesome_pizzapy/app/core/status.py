from __future__ import annotations

from enum import Enum


class OrderStatus(str, Enum):
    CREATED = "CREATED"
    IN_PREPARATION = "IN_PREPARATION"
    READY = "READY"
    DELIVERED = "DELIVERED"


_ALLOWED_TRANSITIONS: dict[OrderStatus, tuple[OrderStatus, ...]] = {
    OrderStatus.CREATED: (OrderStatus.IN_PREPARATION,),
    OrderStatus.IN_PREPARATION: (OrderStatus.READY,),
    OrderStatus.READY: (OrderStatus.DELIVERED,),
}


def is_valid_transition(current: OrderStatus, new_status: OrderStatus) -> bool:
    return new_status in _ALLOWED_TRANSITIONS.get(current, tuple())
