from __future__ import annotations

from fastapi import APIRouter, Depends, Query, Response, status

from app.api.dependencies import get_order_service
from app.core.status import OrderStatus
from app.schemas.orders import OrderCreate, OrderResponse, PaginatedOrders, serialize_order
from app.services.orders import OrderService

router = APIRouter(prefix="/orders", tags=["Orders"])


@router.get("", response_model=list[OrderResponse])
def list_orders(service: OrderService = Depends(get_order_service)):
    orders = service.list_orders()
    return [serialize_order(order) for order in orders]


@router.get("/search", response_model=PaginatedOrders)
def search_orders(
    service: OrderService = Depends(get_order_service),
    status: list[OrderStatus] | None = Query(default=None),
    page: int = Query(default=0, ge=0),
    size: int = Query(default=20, ge=1),
):
    statuses = status or []
    items, total = service.list_filtered(statuses, page=page, size=size)
    return PaginatedOrders(
        items=[serialize_order(order) for order in items],
        total=total,
        page=page,
        size=size,
    )


@router.get("/{order_id}", response_model=OrderResponse)
def get_order(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.get_order(order_id)
    return serialize_order(order)


@router.get("/{order_code}/status", response_model=str)
def get_order_status(order_code: str, service: OrderService = Depends(get_order_service)):
    status_value = service.get_order_status_by_code(order_code)
    return status_value.value


@router.post("", response_model=OrderResponse, status_code=status.HTTP_201_CREATED)
def create_order(
    payload: OrderCreate,
    response: Response,
    service: OrderService = Depends(get_order_service),
):
    order = service.create_order(payload)
    response.headers["Location"] = f"/orders/{order.order_id}"
    return serialize_order(order)


@router.post("/{order_id}/start-preparation", response_model=OrderResponse)
def start_preparation(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.start_order_preparation(order_id)
    return serialize_order(order)


@router.post("/{order_id}/mark-ready", response_model=OrderResponse)
def mark_ready(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.mark_ready(order_id)
    return serialize_order(order)


@router.post("/{order_id}/mark-delivered", response_model=OrderResponse)
def mark_delivered(order_id: int, service: OrderService = Depends(get_order_service)):
    order = service.mark_delivered(order_id)
    return serialize_order(order)
