from __future__ import annotations

from collections.abc import Iterable

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.domain import Ingredient


class IngredientRepository:
    def __init__(self, session: Session):
        self._session = session

    def get_by_ids(self, ingredient_ids: Iterable[int]) -> dict[int, Ingredient]:
        ids = list(set(int(i) for i in ingredient_ids))
        if not ids:
            return {}

        stmt = select(Ingredient).where(Ingredient.ingredient_id.in_(ids))
        result = self._session.execute(stmt).scalars().all()
        return {ingredient.ingredient_id: ingredient for ingredient in result}
