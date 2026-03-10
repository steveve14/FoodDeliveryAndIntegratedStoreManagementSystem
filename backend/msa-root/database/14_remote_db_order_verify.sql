-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_order

SELECT COUNT(*) AS orders_count FROM orders;
SELECT COUNT(*) AS order_item_count FROM order_item;

SELECT user_id, COUNT(*) AS orders_count, MAX(created_at) AS last_order_at
FROM orders
WHERE user_id IS NOT NULL
GROUP BY user_id
ORDER BY last_order_at DESC;