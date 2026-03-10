-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_user

SELECT COUNT(*) AS users_count FROM users;
SELECT COUNT(*) AS addresses_count FROM addresses;
SELECT COUNT(*) AS favorite_stores_count FROM favorite_stores;
SELECT COUNT(*) AS cart_items_count FROM cart_items;
SELECT COUNT(*) AS mail_messages_count FROM mail_messages;
SELECT COUNT(*) AS user_notifications_count FROM user_notifications;