INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at)
SELECT 1, 1, 'NEW', 120.00, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE id = 1);

INSERT INTO orders (id, user_id, status, total_amount, created_at, updated_at)
SELECT 2, 2, 'PAID', 75.50, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE id = 2);

INSERT INTO order_items (order_id, product_name, quantity, price)
SELECT 1, 'Keyboard', 1, 70.00
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = 1 AND product_name = 'Keyboard');

INSERT INTO order_items (order_id, product_name, quantity, price)
SELECT 1, 'Mouse', 1, 50.00
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = 1 AND product_name = 'Mouse');

INSERT INTO order_items (order_id, product_name, quantity, price)
SELECT 2, 'Headset', 1, 75.50
WHERE NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = 2 AND product_name = 'Headset');
