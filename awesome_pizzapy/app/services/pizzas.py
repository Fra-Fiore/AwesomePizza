from __future__ import annotations

from decimal import Decimal

from sqlalchemy.orm import Session

from app.core.exceptions import IngredientNotFoundException, PizzaNotFoundException
from app.models.domain import Pizza
from app.repositories.ingredients import IngredientRepository
from app.repositories.pizzas import PizzaRepository
from app.schemas.pizzas import PizzaCreate, PizzaUpdate


class PizzaService:
    def __init__(
        self,
        session: Session,
        pizza_repository: PizzaRepository | None = None,
        ingredient_repository: IngredientRepository | None = None,
    ) -> None:
        self._session = session
        self._pizzas = pizza_repository or PizzaRepository(session)
        self._ingredients = ingredient_repository or IngredientRepository(session)

    def list_pizzas(self) -> list[Pizza]:
        return self._pizzas.list()

    def get_pizza(self, pizza_id: int) -> Pizza:
        pizza = self._pizzas.get(pizza_id)
        if not pizza:
            raise PizzaNotFoundException(pizza_id)
        return pizza

    def create_pizza(self, payload: PizzaCreate) -> Pizza:
        ingredients = self._ingredients.get_by_ids(payload.ingredient_ids)
        ordered_ingredients = self._resolve_ingredients(payload.ingredient_ids, ingredients)

        pizza = Pizza(
            name=payload.name,
            price=Decimal(str(payload.price)),
            description=payload.description or "",
            image_url=payload.image_url,
            vegan_ok=payload.vegan_ok,
            vegetarian_ok=payload.vegetarian_ok,
            gluten_free=payload.gluten_free,
            ingredients=ordered_ingredients,
        )

        self._pizzas.add(pizza)
        self._session.commit()
        self._session.refresh(pizza)
        return self.get_pizza(pizza.pizza_id)

    def update_pizza(self, pizza_id: int, payload: PizzaUpdate) -> Pizza:
        pizza = self.get_pizza(pizza_id)
        ingredients = self._ingredients.get_by_ids(payload.ingredient_ids)
        ordered_ingredients = self._resolve_ingredients(payload.ingredient_ids, ingredients)

        pizza.name = payload.name
        pizza.price = Decimal(str(payload.price))
        pizza.description = payload.description or ""
        pizza.image_url = payload.image_url
        pizza.vegan_ok = payload.vegan_ok
        pizza.vegetarian_ok = payload.vegetarian_ok
        pizza.gluten_free = payload.gluten_free
        pizza.ingredients = ordered_ingredients

        self._session.commit()
        self._session.refresh(pizza)
        return self.get_pizza(pizza_id)

    def delete_pizza(self, pizza_id: int) -> None:
        pizza = self.get_pizza(pizza_id)
        self._pizzas.remove(pizza)
        self._session.commit()

    def _resolve_ingredients(self, requested_ids, found):
        missing = set(requested_ids) - set(found.keys())
        if missing:
            missing_id = next(iter(missing))
            raise IngredientNotFoundException(int(missing_id))
        return [found[int(i)] for i in requested_ids]
