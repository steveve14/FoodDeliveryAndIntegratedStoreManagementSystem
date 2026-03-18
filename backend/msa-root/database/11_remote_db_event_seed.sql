-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_event

DELETE FROM events WHERE id IN (
  'event-001', 'event-002', 'event-003', 'event-004', 'event-005',
  'event-006', 'event-007', 'event-008', 'event-009', 'event-010',
  'event-011', 'event-012', 'event-013', 'event-014', 'event-015',
  'event-016', 'event-017', 'event-018', 'event-019', 'event-020'
);

INSERT INTO events (id, type, payload, created_at) VALUES
  ('event-001', 'ORDER_CREATED', '{"orderId":"order-001","userId":"user-minsu","storeId":"store-seoul-1"}', NOW() - INTERVAL '3 hour'),
  ('event-002', 'ORDER_CONFIRMED', '{"orderId":"order-002","storeId":"store-seoul-2","status":"CONFIRMED"}', NOW() - INTERVAL '2 hour 10 minute'),
  ('event-003', 'DELIVERY_STARTED', '{"orderId":"order-003","deliveryId":"delivery-002"}', NOW() - INTERVAL '85 minute'),
  ('event-004', 'ORDER_CANCELLED', '{"orderId":"order-005","reason":"USER_REQUEST"}', NOW() - INTERVAL '2 day'),
  ('event-005', 'USER_REGISTERED', '{"userId":"user-subin","email":"subin.kang@fdms.local"}', NOW() - INTERVAL '10 day'),
  -- 추가 이벤트 10건
  ('event-006', 'ORDER_CREATED',      '{"orderId":"order-006","userId":"user-taehyun","storeId":"store-busan-1"}',                       NOW() - INTERVAL '1 day 6 hour'),
  ('event-007', 'DELIVERY_ASSIGNED',  '{"orderId":"order-006","deliveryId":"delivery-004","courierId":"라이더 오준혁"}',                   NOW() - INTERVAL '1 day 5 hour 50 minute'),
  ('event-008', 'DELIVERY_COMPLETED', '{"orderId":"order-006","deliveryId":"delivery-004"}',                                               NOW() - INTERVAL '1 day 5 hour'),
  ('event-009', 'ORDER_CREATED',      '{"orderId":"order-007","userId":"user-jisoo","storeId":"store-daejeon-1"}',                         NOW() - INTERVAL '50 minute'),
  ('event-010', 'DELIVERY_STARTED',   '{"orderId":"order-007","deliveryId":"delivery-005"}',                                               NOW() - INTERVAL '35 minute'),
  ('event-011', 'ORDER_CONFIRMED',    '{"orderId":"order-008","storeId":"store-suwon-1","status":"CONFIRMED"}',                            NOW() - INTERVAL '1 hour 20 minute'),
  ('event-012', 'ORDER_CANCELLED',    '{"orderId":"order-013","userId":"user-chaeyeon","reason":"STORE_CLOSED"}',                          NOW() - INTERVAL '5 day 3 hour'),
  ('event-013', 'USER_REGISTERED',    '{"userId":"user-taehyun","email":"taehyun.kim@fdms.local"}',                                        NOW() - INTERVAL '7 day'),
  ('event-014', 'USER_REGISTERED',    '{"userId":"user-hyunwoo","email":"hyunwoo.jang@fdms.local","provider":"kakao"}',                    NOW() - INTERVAL '2 day'),
  ('event-015', 'STORE_REGISTERED',   '{"storeId":"store-busan-1","ownerId":"store-owner-4","name":"해운대 화덕피자","category":"PIZZA"}', NOW() - INTERVAL '12 day'),
  ('event-016', 'ORDER_CREATED',      '{"orderId":"order-016","userId":"user-seed-001","storeId":"store-seed-001"}', NOW() - INTERVAL '24 minute'),
  ('event-017', 'ORDER_CONFIRMED',    '{"orderId":"order-017","storeId":"store-seed-002","status":"CONFIRMED"}', NOW() - INTERVAL '20 minute'),
  ('event-018', 'DELIVERY_STARTED',   '{"orderId":"order-018","deliveryId":"delivery-013"}', NOW() - INTERVAL '11 minute'),
  ('event-019', 'DELIVERY_COMPLETED', '{"orderId":"order-019","deliveryId":"delivery-014"}', NOW() - INTERVAL '2 hour 35 minute'),
  ('event-020', 'ORDER_CANCELLED',    '{"orderId":"order-020","reason":"USER_REQUEST"}', NOW() - INTERVAL '3 hour 30 minute');