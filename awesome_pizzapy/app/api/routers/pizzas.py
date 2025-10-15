from __future__ import annotations

from fastapi import APIRouter, Depends, Response, status

from app.api.dependencies import get_pizza_service
from app.schemas.pizzas import PizzaCreate, PizzaResponse, PizzaUpdate, serialize_pizza
from app.services.pizzas import PizzaService

router = APIRouter(prefix="/pizzas", tags=["Pizzas"])


@router.get("", response_model=list[PizzaResponse])
def list_pizzas(service: PizzaService = Depends(get_pizza_service)):
    pizzas = service.list_pizzas()
    return [serialize_pizza(pizza) for pizza in pizzas]


@router.get("/{pizza_id}", response_model=PizzaResponse)
def get_pizza(pizza_id: int, service: PizzaService = Depends(get_pizza_service)):
    pizza = service.get_pizza(pizza_id)
    return serialize_pizza(pizza)


@router.post("", response_model=PizzaResponse, status_code=status.HTTP_201_CREATED)
def create_pizza(
    payload: PizzaCreate,
    response: Response,
    service: PizzaService = Depends(get_pizza_service),
):
    pizza = service.create_pizza(payload)
    response.headers["Location"] = f"/pizzas/{pizza.pizza_id}"
    return serialize_pizza(pizza)


@router.put("/{pizza_id}", response_model=PizzaResponse)
def update_pizza(
    pizza_id: int,
    payload: PizzaUpdate,
    service: PizzaService = Depends(get_pizza_service),
):
    pizza = service.update_pizza(pizza_id, payload)
    return serialize_pizza(pizza)


@router.delete("/{pizza_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_pizza(pizza_id: int, service: PizzaService = Depends(get_pizza_service)):
    service.delete_pizza(pizza_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
