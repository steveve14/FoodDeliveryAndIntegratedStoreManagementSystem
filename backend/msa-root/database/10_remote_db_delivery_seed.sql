-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_delivery

DELETE FROM delivery WHERE id IN (
  'delivery-001', 'delivery-002', 'delivery-003',
  'delivery-004', 'delivery-005', 'delivery-006', 'delivery-007', 'delivery-008', 'delivery-009', 'delivery-010'
);

INSERT INTO delivery (id, order_id, courier, status, delivery_fee, scheduled_at) VALUES
  ('delivery-001', 'order-002', '라이더 김현수', 'ASSIGNED', 3000, NOW() - INTERVAL '2 hour'),
  ('delivery-002', 'order-003', '라이더 박소연', 'PICKED_UP', 2500, NOW() - INTERVAL '80 minute'),
  ('delivery-003', 'order-004', '라이더 정우성', 'COMPLETED', 3000, NOW() - INTERVAL '1 day 3 hour'),
  -- 추가 배달 7건
  ('delivery-004', 'order-006', '라이더 오준혁', 'COMPLETED', 3500, NOW() - INTERVAL '1 day 5 hour'),
  ('delivery-005', 'order-007', '라이더 김재원', 'PICKED_UP', 2500, NOW() - INTERVAL '35 minute'),
  ('delivery-006', 'order-008', '라이더 이수진', 'ASSIGNED', 4000, NOW() - INTERVAL '1 hour 20 minute'),
  ('delivery-007', 'order-009', '라이더 박선호', 'COMPLETED', 3000, NOW() - INTERVAL '3 day 1 hour'),
  ('delivery-008', 'order-011', '라이더 최시원', 'ASSIGNED', 3500, NOW() - INTERVAL '2 hour'),
  ('delivery-009', 'order-014', '라이더 익명화', 'PICKED_UP', 2000, NOW() - INTERVAL '20 minute'),
  ('delivery-010', 'order-015', '라이더 조민준', 'COMPLETED', 2500, NOW() - INTERVAL '2 day 7 hour');