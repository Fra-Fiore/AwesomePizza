-- Seed pizzas
INSERT INTO pizza (name, price, description, image_url)
VALUES ('Margherita', 8.50, 'Classic pizza with tomato and mozzarella', 'https://example.com/margherita.jpg'),
       ('Diavola', 10.00, 'Spicy salami and mozzarella', 'https://example.com/diavola.jpg'),
       ('Quattro Formaggi', 12.00, 'Four-cheese blend', 'https://example.com/quattroformaggi.jpg');

-- Core ingredients
INSERT INTO ingredient (name, allergene)
VALUES ('Pomodoro', FALSE),
       ('Mozzarella', TRUE),
       ('Salame Piccante', TRUE),
       ('Gorgonzola', TRUE),
       ('Fontina', TRUE),
       ('Parmigiano', TRUE);

-- Flours
INSERT INTO ingredient (name, allergene)
VALUES ('Farina di Grano', TRUE),
       ('Farina Integrale', TRUE),
       ('Farina di Riso', FALSE);

-- Vegan and gluten-free options
INSERT INTO ingredient (name, allergene)
VALUES ('Mozzarella di Riso', FALSE),
       ('Seitan', FALSE),
       ('Tofu', FALSE),
       ('Verdure Grigliate', FALSE),
       ('Olive', FALSE),
       ('Basilico', FALSE),
       ('Olio EVO', FALSE);


-- Pizza-to-ingredient relationships
INSERT INTO pizza_ingredient (pizza_id, ingredient_id)
VALUES (1, 1),  -- Margherita: Pomodoro
       (1, 2),  -- Margherita: Mozzarella
       (1, 15), -- Margherita: Basilico
       (1, 16), -- Margherita: Olio EVO
       (2, 1),  -- Diavola: Pomodoro
       (2, 2),  -- Diavola: Mozzarella
       (2, 3),  -- Diavola: Salame Piccante
       (2, 16), -- Margherita: Olio EVO
       (3, 2),  -- Quattro Formaggi: Mozzarella
       (3, 4),  -- Quattro Formaggi: Gorgonzola
       (3, 5),  -- Quattro Formaggi: Fontina
       (3, 6),  -- Quattro Formaggi: Parmigiano
       (3, 16); -- Margherita: Olio EVO
