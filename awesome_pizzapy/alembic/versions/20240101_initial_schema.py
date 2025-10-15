"""Initial schema for AwesomePizzaPy"""

from __future__ import annotations

from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = "20240101"
down_revision = None
branch_labels = None
depends_on = None

order_status_enum = sa.Enum(
    "CREATED",
    "IN_PREPARATION",
    "READY",
    "DELIVERED",
    name="order_status",
)


def upgrade() -> None:
    op.create_table(
        "pizza",
        sa.Column("pizza_id", sa.Integer(), primary_key=True, autoincrement=True),
        sa.Column("name", sa.String(length=100), nullable=False),
        sa.Column("description", sa.Text(), nullable=False),
        sa.Column("price", sa.Numeric(10, 2), nullable=False),
        sa.Column("vegan_ok", sa.Boolean(), nullable=False, server_default=sa.false()),
        sa.Column("vegetarian_ok", sa.Boolean(), nullable=False, server_default=sa.false()),
        sa.Column("gluten_free", sa.Boolean(), nullable=False, server_default=sa.false()),
        sa.Column("image_url", sa.String(length=255), nullable=True),
    )

    op.create_table(
        "ingredient",
        sa.Column("ingredient_id", sa.Integer(), primary_key=True, autoincrement=True),
        sa.Column("name", sa.String(length=100), nullable=False),
        sa.Column("allergene", sa.Boolean(), nullable=False, server_default=sa.false()),
    )

    op.create_table(
        "order",
        sa.Column("order_id", sa.Integer(), primary_key=True, autoincrement=True),
        sa.Column("timestamp", sa.DateTime(), nullable=False, server_default=sa.func.now()),
        sa.Column("status", order_status_enum, nullable=False),
        sa.Column("order_code", sa.String(length=50), nullable=False),
        sa.Column("total_price", sa.Numeric(10, 2), nullable=False),
    )
    op.create_index("order_status_idx", "order", ["status"])
    op.create_index("order_order_code_idx", "order", ["order_code"], unique=False)

    op.create_table(
        "order_pizza",
        sa.Column("order_pizza_id", sa.Integer(), primary_key=True, autoincrement=True),
        sa.Column("order_id", sa.Integer(), sa.ForeignKey("order.order_id", ondelete="CASCADE"), nullable=False),
        sa.Column("pizza_id", sa.Integer(), sa.ForeignKey("pizza.pizza_id", ondelete="CASCADE"), nullable=False),
        sa.Column("quantity", sa.Integer(), nullable=False),
        sa.Column("notes", sa.Text(), nullable=True),
        sa.CheckConstraint("quantity > 0", name="ck_order_pizza_quantity_positive"),
    )

    op.create_table(
        "pizza_ingredient",
        sa.Column("pizza_ingredient_id", sa.Integer(), primary_key=True, autoincrement=True),
        sa.Column("pizza_id", sa.Integer(), sa.ForeignKey("pizza.pizza_id", ondelete="CASCADE"), nullable=False),
        sa.Column("ingredient_id", sa.Integer(), sa.ForeignKey("ingredient.ingredient_id", ondelete="CASCADE"), nullable=False),
    )


def downgrade() -> None:
    op.drop_table("pizza_ingredient")
    op.drop_table("order_pizza")
    op.drop_index("order_order_code_idx", table_name="order")
    op.drop_index("order_status_idx", table_name="order")
    op.drop_table("order")
    order_status_enum.drop(op.get_bind(), checkfirst=False)
    op.drop_table("ingredient")
    op.drop_table("pizza")
