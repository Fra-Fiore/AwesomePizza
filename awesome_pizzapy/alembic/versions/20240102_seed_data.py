"""Seed data mirroring the original AwesomePizza bootstrap."""

from __future__ import annotations

from decimal import Decimal

from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = "20240102"
down_revision = "20240101"
branch_labels = None
depends_on = None

pizza_table = sa.table(
    "pizza",
    sa.column("name", sa.String),
    sa.column("price", sa.Numeric(10, 2)),
    sa.column("description", sa.Text),
    sa.column("image_url", sa.String),
)

ingredient_table = sa.table(
    "ingredient",
    sa.column("name", sa.String),
    sa.column("allergene", sa.Boolean),
)

pizza_ingredient_table = sa.table(
    "pizza_ingredient",
    sa.column("pizza_id", sa.Integer),
    sa.column("ingredient_id", sa.Integer),
)


def upgrade() -> None:
    op.bulk_insert(
        pizza_table,
        [
            {
                "name": "Margherita",
                "price": Decimal("8.50"),
                "description": "Classic pizza with tomato and mozzarella",
                "image_url": "https://example.com/margherita.jpg",
            },
            {
                "name": "Diavola",
                "price": Decimal("10.00"),
                "description": "Spicy salami and mozzarella",
                "image_url": "https://example.com/diavola.jpg",
            },
            {
                "name": "Quattro Formaggi",
                "price": Decimal("12.00"),
                "description": "Four-cheese blend",
                "image_url": "https://example.com/quattroformaggi.jpg",
            },
        ],
    )

    op.bulk_insert(
        ingredient_table,
        [
            {"name": "Pomodoro", "allergene": False},
            {"name": "Mozzarella", "allergene": True},
            {"name": "Salame Piccante", "allergene": True},
            {"name": "Gorgonzola", "allergene": True},
            {"name": "Fontina", "allergene": True},
            {"name": "Parmigiano", "allergene": True},
            {"name": "Farina di Grano", "allergene": True},
            {"name": "Farina Integrale", "allergene": True},
            {"name": "Farina di Riso", "allergene": False},
            {"name": "Mozzarella di Riso", "allergene": False},
            {"name": "Seitan", "allergene": False},
            {"name": "Tofu", "allergene": False},
            {"name": "Verdure Grigliate", "allergene": False},
            {"name": "Olive", "allergene": False},
            {"name": "Basilico", "allergene": False},
            {"name": "Olio EVO", "allergene": False},
        ],
    )

    op.bulk_insert(
        pizza_ingredient_table,
        [
            {"pizza_id": 1, "ingredient_id": 1},
            {"pizza_id": 1, "ingredient_id": 2},
            {"pizza_id": 1, "ingredient_id": 15},
            {"pizza_id": 1, "ingredient_id": 16},
            {"pizza_id": 2, "ingredient_id": 1},
            {"pizza_id": 2, "ingredient_id": 2},
            {"pizza_id": 2, "ingredient_id": 3},
            {"pizza_id": 2, "ingredient_id": 16},
            {"pizza_id": 3, "ingredient_id": 2},
            {"pizza_id": 3, "ingredient_id": 4},
            {"pizza_id": 3, "ingredient_id": 5},
            {"pizza_id": 3, "ingredient_id": 6},
            {"pizza_id": 3, "ingredient_id": 16},
        ],
    )


def downgrade() -> None:
    op.execute("DELETE FROM pizza_ingredient")
    op.execute("DELETE FROM ingredient")
    op.execute("DELETE FROM pizza")
