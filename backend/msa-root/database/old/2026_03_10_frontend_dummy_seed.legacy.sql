-- 2026-03-10 프론트 더미 화면 연동용 시드 데이터
-- 목적: web-admin, web-shop, web-user에서 사용하던 더미 데이터를
--       현재 MSA 스키마 + 확장 사용자 프로필/메일/알림 테이블에 적재한다.

\c db_user

DELETE FROM refresh_tokens WHERE user_id IN (
  'admin-main', 'admin-cs', 'admin-finance', 'admin-store-ops', 'admin-audit',
  'store-owner-1', 'store-owner-2', 'store-owner-3',
  'user-minsu', 'user-seoyeon', 'user-jihoon', 'user-yujin', 'user-haneul', 'user-subin', 'user-dohyun', 'user-sohee'
);

DELETE FROM user_notifications WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8);
DELETE FROM mail_messages WHERE id IN (1, 2, 3, 4);
DELETE FROM favorite_stores WHERE id IN ('fav-001', 'fav-002', 'fav-003', 'fav-004');
DELETE FROM cart_items WHERE id IN ('cart-001', 'cart-002', 'cart-003', 'cart-004');
DELETE FROM addresses WHERE id IN ('addr-001', 'addr-002', 'addr-003', 'addr-004', 'addr-005', 'addr-006');
DELETE FROM users WHERE id IN (
  'admin-main', 'admin-cs', 'admin-finance', 'admin-store-ops', 'admin-audit',
  'store-owner-1', 'store-owner-2', 'store-owner-3',
  'user-minsu', 'user-seoyeon', 'user-jihoon', 'user-yujin', 'user-haneul', 'user-subin', 'user-dohyun', 'user-sohee'
);

INSERT INTO users (
  id, email, password_hash, name, phone, roles, provider, provider_id,
  username, avatar_url, marketing_status, location, team_role, created_at
) VALUES
  ('admin-main', 'admin@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '플랫폼 운영 관리자', '010-1000-0001', 'ADMIN', 'local', NULL, 'admin_ops', 'https://i.pravatar.cc/128?u=fdms-team-1', 'internal', '서울특별시 중구', 'owner', NOW() - INTERVAL '20 day'),
  ('admin-cs', 'support@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '고객센터 매니저', '010-1000-0002', 'ADMIN', 'local', NULL, 'cs_manager', 'https://i.pravatar.cc/128?u=fdms-team-2', 'internal', '서울특별시 영등포구', 'member', NOW() - INTERVAL '18 day'),
  ('admin-finance', 'finance@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '정산 담당자', '010-1000-0003', 'ADMIN', 'local', NULL, 'finance_admin', 'https://i.pravatar.cc/128?u=fdms-team-3', 'internal', '서울특별시 강서구', 'member', NOW() - INTERVAL '17 day'),
  ('admin-store-ops', 'storeops@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '매장 운영 담당', '010-1000-0004', 'ADMIN', 'local', NULL, 'store_ops', 'https://i.pravatar.cc/128?u=fdms-team-4', 'internal', '경기도 성남시', 'member', NOW() - INTERVAL '17 day'),
  ('admin-audit', 'audit@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '보안 감사 관리자', '010-1000-0005', 'ADMIN', 'local', NULL, 'audit_owner', 'https://i.pravatar.cc/128?u=fdms-team-5', 'internal', '서울특별시 서초구', 'owner', NOW() - INTERVAL '16 day'),
  ('store-owner-1', 'owner1@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '김도윤', '010-2000-0001', 'STORE', 'local', NULL, 'gangnam_owner', 'https://i.pravatar.cc/128?u=fdms-store-1', 'internal', '서울특별시 강남구', 'member', NOW() - INTERVAL '17 day'),
  ('store-owner-2', 'owner2@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '이수아', '010-2000-0002', 'STORE', 'local', NULL, 'songpa_owner', 'https://i.pravatar.cc/128?u=fdms-store-2', 'internal', '서울특별시 송파구', 'member', NOW() - INTERVAL '17 day'),
  ('store-owner-3', 'owner3@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '박현우', '010-2000-0003', 'STORE', 'local', NULL, 'yeonsu_owner', 'https://i.pravatar.cc/128?u=fdms-store-3', 'internal', '인천광역시 연수구', 'member', NOW() - INTERVAL '16 day'),
  ('user-minsu', 'minsu.kim@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '김민수', '010-3000-0001', 'USER', 'local', NULL, 'minsu_kim', 'https://i.pravatar.cc/128?u=fdms-user-1', 'subscribed', '서울특별시 강남구', NULL, NOW() - INTERVAL '15 day'),
  ('user-seoyeon', 'seoyeon.lee@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '이서연', '010-3000-0002', 'USER', 'local', NULL, 'seoyeon_lee', 'https://i.pravatar.cc/128?u=fdms-user-2', 'subscribed', '서울특별시 송파구', NULL, NOW() - INTERVAL '14 day'),
  ('user-jihoon', 'jihoon.park@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '박지훈', '010-3000-0003', 'USER', 'local', NULL, 'jihoon_park', 'https://i.pravatar.cc/128?u=fdms-user-3', 'unsubscribed', '경기도 성남시', NULL, NOW() - INTERVAL '13 day'),
  ('user-yujin', 'yujin.choi@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '최유진', '010-3000-0004', 'USER', 'local', NULL, 'yujin_choi', 'https://i.pravatar.cc/128?u=fdms-user-4', 'subscribed', '인천광역시 연수구', NULL, NOW() - INTERVAL '12 day'),
  ('user-haneul', 'haneul.jung@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '정하늘', '010-3000-0005', 'USER', 'local', NULL, 'haneul_jung', 'https://i.pravatar.cc/128?u=fdms-user-5', 'bounced', '대전광역시 유성구', NULL, NOW() - INTERVAL '11 day'),
  ('user-subin', 'subin.kang@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '강수빈', '010-3000-0006', 'USER', 'local', NULL, 'subin_kang', 'https://i.pravatar.cc/128?u=fdms-user-6', 'subscribed', '부산광역시 해운대구', NULL, NOW() - INTERVAL '10 day'),
  ('user-dohyun', 'dohyun.yoon@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '윤도현', '010-3000-0007', 'USER', 'local', NULL, 'dohyun_yoon', 'https://i.pravatar.cc/128?u=fdms-user-7', 'subscribed', '광주광역시 서구', NULL, NOW() - INTERVAL '9 day'),
  ('user-sohee', 'sohee.han@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '한소희', '010-3000-0008', 'USER', 'local', NULL, 'sohee_han', 'https://i.pravatar.cc/128?u=fdms-user-8', 'subscribed', '제주특별자치도 제주시', NULL, NOW() - INTERVAL '8 day');

INSERT INTO addresses (id, user_id, label, line1, line2, city, state, postal_code, country, primary_address, created_at) VALUES
  ('addr-001', 'user-minsu', '집', '강남대로 123', '101동 1201호', '서울특별시', '강남구', '06236', '대한민국', TRUE, NOW() - INTERVAL '10 day'),
  ('addr-002', 'user-minsu', '회사', '테헤란로 51', '8층', '서울특별시', '강남구', '06142', '대한민국', FALSE, NOW() - INTERVAL '9 day'),
  ('addr-003', 'user-seoyeon', '집', '올림픽로 300', '2202호', '서울특별시', '송파구', '05551', '대한민국', TRUE, NOW() - INTERVAL '8 day'),
  ('addr-004', 'user-jihoon', '집', '판교역로 166', NULL, '경기도', '성남시', '13494', '대한민국', TRUE, NOW() - INTERVAL '7 day'),
  ('addr-005', 'user-yujin', '집', '컨벤시아대로 165', '1205호', '인천광역시', '연수구', '22004', '대한민국', TRUE, NOW() - INTERVAL '6 day'),
  ('addr-006', 'user-subin', '집', '센텀중앙로 90', '1803호', '부산광역시', '해운대구', '48058', '대한민국', TRUE, NOW() - INTERVAL '5 day');

INSERT INTO favorite_stores (id, user_id, store_id, name, category, rating, delivery_time, min_order, image_icon, created_at) VALUES
  ('fav-001', 'user-minsu', 'store-seoul-1', '강남 한식당', 'KOREAN', 4.7, '32분', '15000원', 'i-lucide-store', NOW() - INTERVAL '4 day'),
  ('fav-002', 'user-minsu', 'store-seoul-2', '송파 치킨클럽', 'CHICKEN', 4.8, '28분', '18000원', 'i-lucide-store', NOW() - INTERVAL '3 day'),
  ('fav-003', 'user-seoyeon', 'store-incheon-1', '연수 브런치랩', 'CAFE', 4.6, '25분', '12000원', 'i-lucide-store', NOW() - INTERVAL '3 day'),
  ('fav-004', 'user-yujin', 'store-seoul-1', '강남 한식당', 'KOREAN', 4.7, '32분', '15000원', 'i-lucide-store', NOW() - INTERVAL '2 day');

INSERT INTO cart_items (id, user_id, store_id, store_name, menu_id, menu_name, quantity, price, created_at) VALUES
  ('cart-001', 'user-minsu', 'store-seoul-1', '강남 한식당', 'menu-001', '직화 제육덮밥', 1, 9500, NOW() - INTERVAL '2 hour'),
  ('cart-002', 'user-minsu', 'store-seoul-1', '강남 한식당', 'menu-002', '소불고기 정식', 2, 13500, NOW() - INTERVAL '110 minute'),
  ('cart-003', 'user-seoyeon', 'store-seoul-2', '송파 치킨클럽', 'menu-004', '후라이드 치킨', 1, 21000, NOW() - INTERVAL '90 minute'),
  ('cart-004', 'user-subin', 'store-incheon-1', '연수 브런치랩', 'menu-008', '에그 베이글 샌드위치', 1, 8900, NOW() - INTERVAL '70 minute');

INSERT INTO mail_messages (id, from_user_id, subject, body, unread, created_at) VALUES
  (1, 'user-seoyeon', '주문 지연 문의 대응 요청', '강남권 주문 지연 관련 문의가 증가하고 있습니다. 점심 피크 시간 주문 처리 현황을 확인해 주세요.', TRUE, NOW() - INTERVAL '1 hour 12 minute'),
  (2, 'user-yujin', '쿠폰 정책 검토 회의', '신규 가입 쿠폰과 재방문 쿠폰 정책을 3월 주간 캠페인 일정에 맞춰 재검토해야 합니다.', FALSE, NOW() - INTERVAL '1 day 2 hour'),
  (3, 'user-subin', '정산 오류 후보 건 공유', '정산 금액이 주문 총액과 차이 나는 후보 건 3건을 확인했습니다. 거래 로그 화면 연동 전 임시 검토용입니다.', TRUE, NOW() - INTERVAL '2 day 3 hour'),
  (4, 'user-dohyun', '이벤트 발행 실패 로그 확인', 'service-event 적재 실패 건이 있어 재시도 전 traceId 기준 필터 화면이 필요합니다.', FALSE, NOW() - INTERVAL '3 day 4 hour');

INSERT INTO user_notifications (id, user_id, body, unread, created_at) VALUES
  (1, 'user-seoyeon', '1:1 문의를 등록했습니다', TRUE, NOW() - INTERVAL '12 minute'),
  (2, 'user-minsu', '신규 주문이 접수되었습니다', FALSE, NOW() - INTERVAL '1 hour'),
  (3, 'user-subin', '환불 요청을 남겼습니다', TRUE, NOW() - INTERVAL '2 hour'),
  (4, 'user-yujin', '리뷰 답글 등록을 요청했습니다', FALSE, NOW() - INTERVAL '5 hour'),
  (5, 'user-sohee', '이벤트 페이지를 조회했습니다', FALSE, NOW() - INTERVAL '1 day'),
  (6, 'user-jihoon', '장바구니에 메뉴를 다시 담았습니다', FALSE, NOW() - INTERVAL '2 day'),
  (7, 'user-haneul', '이메일 수신 상태가 반송으로 변경되었습니다', TRUE, NOW() - INTERVAL '3 day'),
  (8, 'user-dohyun', '관리자 공지를 확인했습니다', FALSE, NOW() - INTERVAL '4 day');

\c db_store

DELETE FROM menu WHERE id IN ('menu-001', 'menu-002', 'menu-003', 'menu-004', 'menu-005', 'menu-006', 'menu-007', 'menu-008', 'menu-009');
DELETE FROM store WHERE id IN ('store-seoul-1', 'store-seoul-2', 'store-incheon-1');

INSERT INTO store (id, owner_id, name, address, phone, category, status, latitude, longitude, min_order_amount, rating_avg, description, opening_hours, created_at) VALUES
  ('store-seoul-1', 'store-owner-1', '강남 한식당', '서울특별시 강남구 강남대로 100', '02-555-1001', 'KOREAN', 'OPEN', 37.4979, 127.0276, 15000, 4.7, '직장인 점심 수요가 많은 한식 배달 전문점', '10:00-21:00', NOW() - INTERVAL '15 day'),
  ('store-seoul-2', 'store-owner-2', '송파 치킨클럽', '서울특별시 송파구 오금로 120', '02-555-1002', 'CHICKEN', 'OPEN', 37.5145, 127.1059, 18000, 4.8, '주문량이 많은 야식 전문 치킨 매장', '11:00-23:30', NOW() - INTERVAL '14 day'),
  ('store-incheon-1', 'store-owner-3', '연수 브런치랩', '인천광역시 연수구 센트럴로 240', '032-555-1003', 'CAFE', 'OPEN', 37.3925, 126.6378, 12000, 4.6, '샌드위치와 커피를 판매하는 브런치 전문점', '08:30-20:00', NOW() - INTERVAL '13 day');

INSERT INTO menu (id, store_id, name, description, price, available, created_at) VALUES
  ('menu-001', 'store-seoul-1', '직화 제육덮밥', '매콤한 제육과 반숙 계란이 올라간 덮밥', 9500, TRUE, NOW() - INTERVAL '12 day'),
  ('menu-002', 'store-seoul-1', '소불고기 정식', '불고기와 밑반찬이 포함된 정식', 13500, TRUE, NOW() - INTERVAL '12 day'),
  ('menu-003', 'store-seoul-1', '된장찌개 세트', '집된장 스타일 찌개와 공기밥', 9000, TRUE, NOW() - INTERVAL '11 day'),
  ('menu-004', 'store-seoul-2', '후라이드 치킨', '바삭한 시그니처 후라이드 치킨', 21000, TRUE, NOW() - INTERVAL '12 day'),
  ('menu-005', 'store-seoul-2', '양념 반 후라이드 반', '반반 조합 인기 메뉴', 23000, TRUE, NOW() - INTERVAL '11 day'),
  ('menu-006', 'store-seoul-2', '치즈볼 세트', '치킨과 함께 주문 많은 사이드 세트', 6500, TRUE, NOW() - INTERVAL '10 day'),
  ('menu-007', 'store-incheon-1', '아메리카노', '브런치랩 시그니처 원두', 4500, TRUE, NOW() - INTERVAL '10 day'),
  ('menu-008', 'store-incheon-1', '에그 베이글 샌드위치', '베이글과 스크램블 에그 조합', 8900, TRUE, NOW() - INTERVAL '10 day'),
  ('menu-009', 'store-incheon-1', '리코타 샐러드', '가벼운 브런치 샐러드', 9800, TRUE, NOW() - INTERVAL '9 day');

\c db_order

DELETE FROM order_item WHERE id IN ('order-item-001', 'order-item-002', 'order-item-003', 'order-item-004', 'order-item-005', 'order-item-006', 'order-item-007', 'order-item-008');
DELETE FROM orders WHERE id IN ('order-001', 'order-002', 'order-003', 'order-004', 'order-005');

INSERT INTO orders (id, user_id, store_id, total_amount, status, created_at) VALUES
  ('order-001', 'user-minsu', 'store-seoul-1', 23000, 'CREATED', NOW() - INTERVAL '3 hour'),
  ('order-002', 'user-seoyeon', 'store-seoul-2', 29500, 'CONFIRMED', NOW() - INTERVAL '2 hour 20 minute'),
  ('order-003', 'user-yujin', 'store-incheon-1', 13400, 'DELIVERING', NOW() - INTERVAL '95 minute'),
  ('order-004', 'user-subin', 'store-seoul-2', 21000, 'DELIVERED', NOW() - INTERVAL '1 day 4 hour'),
  ('order-005', 'user-jihoon', 'store-seoul-1', 18500, 'CANCELLED', NOW() - INTERVAL '2 day 1 hour');

INSERT INTO order_item (id, order_id, menu_id, quantity, price_snapshot) VALUES
  ('order-item-001', 'order-001', 'menu-001', 1, 9500),
  ('order-item-002', 'order-001', 'menu-003', 1, 9000),
  ('order-item-003', 'order-002', 'menu-005', 1, 23000),
  ('order-item-004', 'order-002', 'menu-006', 1, 6500),
  ('order-item-005', 'order-003', 'menu-008', 1, 8900),
  ('order-item-006', 'order-003', 'menu-007', 1, 4500),
  ('order-item-007', 'order-004', 'menu-004', 1, 21000),
  ('order-item-008', 'order-005', 'menu-002', 1, 13500);

\c db_delivery

DELETE FROM delivery WHERE id IN ('delivery-001', 'delivery-002', 'delivery-003');

INSERT INTO delivery (id, order_id, courier, status, delivery_fee, scheduled_at) VALUES
  ('delivery-001', 'order-002', '라이더 김현수', 'ASSIGNED', 3000, NOW() - INTERVAL '2 hour'),
  ('delivery-002', 'order-003', '라이더 박소연', 'PICKED_UP', 2500, NOW() - INTERVAL '80 minute'),
  ('delivery-003', 'order-004', '라이더 정우성', 'COMPLETED', 3000, NOW() - INTERVAL '1 day 3 hour');

\c db_event

DELETE FROM events WHERE id IN ('event-001', 'event-002', 'event-003', 'event-004', 'event-005');

INSERT INTO events (id, type, payload, created_at) VALUES
  ('event-001', 'ORDER_CREATED', '{"orderId":"order-001","userId":"user-minsu","storeId":"store-seoul-1"}', NOW() - INTERVAL '3 hour'),
  ('event-002', 'ORDER_CONFIRMED', '{"orderId":"order-002","storeId":"store-seoul-2","status":"CONFIRMED"}', NOW() - INTERVAL '2 hour 10 minute'),
  ('event-003', 'DELIVERY_STARTED', '{"orderId":"order-003","deliveryId":"delivery-002"}', NOW() - INTERVAL '85 minute'),
  ('event-004', 'ORDER_CANCELLED', '{"orderId":"order-005","reason":"USER_REQUEST"}', NOW() - INTERVAL '2 day'),
  ('event-005', 'USER_REGISTERED', '{"userId":"user-subin","email":"subin.kang@fdms.local"}', NOW() - INTERVAL '10 day');