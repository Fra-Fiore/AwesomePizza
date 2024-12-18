-- Creazione delle tabelle principali
CREATE TABLE pizza
(
    pizza_id      BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100)   NOT NULL,
    description   TEXT           NOT NULL,
    price         NUMERIC(10, 2) NOT NULL,
    vegan_ok      BOOLEAN DEFAULT FALSE,
    vegetarian_ok BOOLEAN DEFAULT FALSE,
    gluten_free   BOOLEAN DEFAULT FALSE,
    image_url     VARCHAR(255)
);

CREATE TABLE ingredient
(
    ingredient_id BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    allergene     BOOLEAN DEFAULT FALSE
);

CREATE TABLE "order"
(
    order_id  BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_code VARCHAR(50) NOT NULL,
    status      VARCHAR(50)    NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL
);
CREATE INDEX order_status_idx ON "order" (status);
CREATE INDEX order_order_code_idx ON "order" (order_code);

CREATE TABLE order_pizza
(
    order_pizza_id BIGSERIAL PRIMARY KEY,
    order_id       BIGINT  NOT NULL REFERENCES "order" (order_id) ON DELETE CASCADE,
    pizza_id       BIGINT  NOT NULL REFERENCES pizza (pizza_id) ON DELETE CASCADE,
    quantity       INTEGER NOT NULL CHECK (quantity > 0),
    notes          TEXT
);

CREATE TABLE pizza_ingredient
(
    pizza_ingredient_id BIGSERIAL PRIMARY KEY,
    pizza_id            BIGINT NOT NULL REFERENCES pizza (pizza_id) ON DELETE CASCADE,
    ingredient_id       BIGINT NOT NULL REFERENCES ingredient (ingredient_id) ON DELETE CASCADE
);
