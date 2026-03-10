-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_order

DELETE FROM order_item WHERE id IN (
  'order-item-001', 'order-item-002', 'order-item-003', 'order-item-004', 'order-item-005', 'order-item-006', 'order-item-007', 'order-item-008',
  'order-item-009', 'order-item-010', 'order-item-011', 'order-item-012', 'order-item-013', 'order-item-014', 'order-item-015',
  'order-item-016', 'order-item-017', 'order-item-018', 'order-item-019', 'order-item-020', 'order-item-021', 'order-item-022', 'order-item-023'
);
DELETE FROM orders WHERE id IN (
  'order-001', 'order-002', 'order-003', 'order-004', 'order-005',
  'order-006', 'order-007', 'order-008', 'order-009', 'order-010',
  'order-011', 'order-012', 'order-013', 'order-014', 'order-015'
);

INSERT INTO orders (id, user_id, store_id, total_amount, status, created_at) VALUES
  ('order-001', 'user-minsu', 'store-seoul-1', 23000, 'CREATED', NOW() - INTERVAL '3 hour'),
  ('order-002', 'user-seoyeon', 'store-seoul-2', 29500, 'CONFIRMED', NOW() - INTERVAL '2 hour 20 minute'),
  ('order-003', 'user-yujin', 'store-incheon-1', 13400, 'DELIVERING', NOW() - INTERVAL '95 minute'),
  ('order-004', 'user-subin', 'store-seoul-2', 21000, 'DELIVERED', NOW() - INTERVAL '1 day 4 hour'),
  ('order-005', 'user-jihoon', 'store-seoul-1', 18500, 'CANCELLED', NOW() - INTERVAL '2 day 1 hour'),
  -- 추가 주문 10건
  ('order-006', 'user-taehyun', 'store-busan-1', 48000, 'DELIVERED', NOW() - INTERVAL '1 day 6 hour'),
  ('order-007', 'user-jisoo', 'store-daejeon-1', 27000, 'DELIVERING', NOW() - INTERVAL '45 minute'),
  ('order-008', 'user-minho', 'store-suwon-1', 46000, 'CONFIRMED', NOW() - INTERVAL '1 hour 30 minute'),
  ('order-009', 'user-junho', 'store-daegu-1', 34000, 'DELIVERED', NOW() - INTERVAL '3 day 2 hour'),
  ('order-010', 'user-sojung', 'store-seoul-3', 17000, 'CREATED', NOW() - INTERVAL '20 minute'),
  ('order-011', 'user-hyunwoo', 'store-busan-1', 25000, 'CONFIRMED', NOW() - INTERVAL '2 hour 10 minute'),
  ('order-012', 'user-naeun', 'store-daejeon-1', 31000, 'DELIVERED', NOW() - INTERVAL '4 day 1 hour'),
  ('order-013', 'user-chaeyeon', 'store-suwon-1', 28000, 'CANCELLED', NOW() - INTERVAL '5 day 3 hour'),
  ('order-014', 'user-sangjun', 'store-seoul-1', 22500, 'DELIVERING', NOW() - INTERVAL '30 minute'),
  ('order-015', 'user-yoona', 'store-seoul-3', 25500, 'DELIVERED', NOW() - INTERVAL '2 day 8 hour');

INSERT INTO order_item (id, order_id, menu_id, quantity, price_snapshot) VALUES
  ('order-item-001', 'order-001', 'menu-001', 1, 9500),
  ('order-item-002', 'order-001', 'menu-003', 1, 9000),
  ('order-item-003', 'order-002', 'menu-005', 1, 23000),
  ('order-item-004', 'order-002', 'menu-006', 1, 6500),
  ('order-item-005', 'order-003', 'menu-008', 1, 8900),
  ('order-item-006', 'order-003', 'menu-007', 1, 4500),
  ('order-item-007', 'order-004', 'menu-004', 1, 21000),
  ('order-item-008', 'order-005', 'menu-002', 1, 13500),
  -- 추가 주문 항목
  ('order-item-009', 'order-006', 'menu-010', 1, 22000),
  ('order-item-010', 'order-006', 'menu-012', 1, 26000),
  ('order-item-011', 'order-007', 'menu-013', 1, 12000),
  ('order-item-012', 'order-007', 'menu-015', 1, 16000),
  ('order-item-013', 'order-008', 'menu-016', 1, 28000),
  ('order-item-014', 'order-008', 'menu-018', 1, 18000),
  ('order-item-015', 'order-009', 'menu-019', 1, 10000),
  ('order-item-016', 'order-009', 'menu-021', 1, 24000),
  ('order-item-017', 'order-010', 'menu-022', 1, 8500),
  ('order-item-018', 'order-010', 'menu-023', 1, 7500),
  ('order-item-019', 'order-011', 'menu-011', 1, 25000),
  ('order-item-020', 'order-012', 'menu-014', 1, 15000),
  ('order-item-021', 'order-012', 'menu-013', 1, 12000),
  ('order-item-022', 'order-013', 'menu-017', 1, 32000),
  ('order-item-023', 'order-014', 'menu-001', 1, 9500);