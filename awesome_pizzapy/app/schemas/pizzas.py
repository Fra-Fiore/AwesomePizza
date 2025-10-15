from __future__ import annotations

from typing import List, Optional

from pydantic import BaseModel, Field, PositiveFloat, conint


class PizzaBase(BaseModel):
    name: str = Field(..., min_length=1, max_length=100)
    price: PositiveFloat
    description: Optional[str] = Field(None, max_length=255)
    image_url: Optional[str] = Field(None)
    ingredient_ids: List[conint(ge=1)] = Field(..., min_items=1)
    vegan_ok: bool = False
    vegetarian_ok: bool = False
    gluten_free: bool = False


class PizzaCreate(PizzaBase):
    pass


class PizzaUpdate(PizzaBase):
    pass


class PizzaResponse(BaseModel):
    id: int
    name: str
    price: float
    description: Optional[str]
    image_url: Optional[str]
    ingredients: list[str]
    vegan_ok: bool
    vegetarian_ok: bool
    gluten_free: bool

    model_config = dict(from_attributes=True)


def serialize_pizza(pizza) -> PizzaResponse:
    return PizzaResponse(
        id=pizza.pizza_id,
        name=pizza.name,
        price=float(pizza.price),
        description=pizza.description,
        image_url=pizza.image_url,
        ingredients=[ingredient.name for ingredient in pizza.ingredients],
        vegan_ok=pizza.vegan_ok,
        vegetarian_ok=pizza.vegetarian_ok,
        gluten_free=pizza.gluten_free,
    )
