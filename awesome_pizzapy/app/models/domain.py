from __future__ import annotations

from datetime import datetime
from decimal import Decimal

from sqlalchemy import Boolean, CheckConstraint, Column, DateTime, Enum, ForeignKey, Integer, Numeric, String, Table, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.status import OrderStatus
from app.db.base import Base


pizza_ingredient_table = Table(
    "pizza_ingredient",
    Base.metadata,
    Column("pizza_ingredient_id", Integer, primary_key=True, autoincrement=True),
    Column("pizza_id", ForeignKey("pizza.pizza_id", ondelete="CASCADE"), nullable=False),
    Column("ingredient_id", ForeignKey("ingredient.ingredient_id", ondelete="CASCADE"), nullable=False),
)


class Pizza(Base):
    __tablename__ = "pizza"

    pizza_id: Mapped[int] = mapped_column("pizza_id", Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=False)
    price: Mapped[Decimal] = mapped_column(Numeric(10, 2), nullable=False)
    vegan_ok: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False, server_default="false")
    vegetarian_ok: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False, server_default="false")
    gluten_free: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False, server_default="false")
    image_url: Mapped[str | None] = mapped_column(String(255), nullable=True)

    orders: Mapped[list["OrderPizza"]] = relationship(
        back_populates="pizza", cascade="all, delete-orphan"
    )
    ingredients: Mapped[list["Ingredient"]] = relationship(
        secondary=pizza_ingredient_table,
        back_populates="pizzas",
        lazy="selectin",
    )


class Ingredient(Base):
    __tablename__ = "ingredient"

    ingredient_id: Mapped[int] = mapped_column("ingredient_id", Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    allergene: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False, server_default="false")

    pizzas: Mapped[list[Pizza]] = relationship(
        secondary=pizza_ingredient_table,
        back_populates="ingredients",
        lazy="selectin",
    )


class Order(Base):
    __tablename__ = "order"

    order_id: Mapped[int] = mapped_column("order_id", Integer, primary_key=True, autoincrement=True)
    timestamp: Mapped[datetime] = mapped_column(DateTime, nullable=False, default=datetime.utcnow)
    status: Mapped[OrderStatus] = mapped_column(Enum(OrderStatus, name="order_status"), nullable=False)
    order_code: Mapped[str] = mapped_column(String(50), nullable=False, index=True)
    total_price: Mapped[Decimal] = mapped_column(Numeric(10, 2), nullable=False)

    pizzas: Mapped[list["OrderPizza"]] = relationship(
        back_populates="order", cascade="all, delete-orphan",
        lazy="selectin",
    )


class OrderPizza(Base):
    __tablename__ = "order_pizza"
    __table_args__ = (CheckConstraint("quantity > 0", name="ck_order_pizza_quantity_positive"),)

    order_pizza_id: Mapped[int] = mapped_column("order_pizza_id", Integer, primary_key=True, autoincrement=True)
    order_id: Mapped[int] = mapped_column(ForeignKey("order.order_id", ondelete="CASCADE"))
    pizza_id: Mapped[int] = mapped_column(ForeignKey("pizza.pizza_id", ondelete="CASCADE"))
    quantity: Mapped[int] = mapped_column(Integer, nullable=False)
    notes: Mapped[str | None] = mapped_column(Text, nullable=True)

    order: Mapped[Order] = relationship(back_populates="pizzas")
    pizza: Mapped[Pizza] = relationship(back_populates="orders")
