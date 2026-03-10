-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_user

DELETE FROM refresh_tokens WHERE user_id IN (
  'admin-main', 'admin-cs', 'admin-finance', 'admin-store-ops', 'admin-audit', 'admin-marketing', 'admin-devops',
  'store-owner-1', 'store-owner-2', 'store-owner-3', 'store-owner-4', 'store-owner-5', 'store-owner-6', 'store-owner-7', 'store-owner-8',
  'user-minsu', 'user-seoyeon', 'user-jihoon', 'user-yujin', 'user-haneul', 'user-subin', 'user-dohyun', 'user-sohee',
  'user-taehyun', 'user-jisoo', 'user-minho', 'user-naeun', 'user-junho', 'user-hyerin',
  'user-sangjun', 'user-yoona', 'user-dongwook', 'user-chaeyeon', 'user-hyunwoo', 'user-sojung'
);

DELETE FROM user_notifications WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
DELETE FROM mail_messages WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
DELETE FROM favorite_stores WHERE id IN ('fav-001', 'fav-002', 'fav-003', 'fav-004', 'fav-005', 'fav-006', 'fav-007', 'fav-008', 'fav-009', 'fav-010', 'fav-011', 'fav-012', 'fav-013', 'fav-014', 'fav-015');
DELETE FROM cart_items WHERE id IN ('cart-001', 'cart-002', 'cart-003', 'cart-004', 'cart-005', 'cart-006', 'cart-007', 'cart-008', 'cart-009', 'cart-010', 'cart-011', 'cart-012');
DELETE FROM addresses WHERE id IN ('addr-001', 'addr-002', 'addr-003', 'addr-004', 'addr-005', 'addr-006', 'addr-007', 'addr-008', 'addr-009', 'addr-010', 'addr-011', 'addr-012', 'addr-013', 'addr-014', 'addr-015');
DELETE FROM users WHERE id IN (
  'admin-main', 'admin-cs', 'admin-finance', 'admin-store-ops', 'admin-audit', 'admin-marketing', 'admin-devops',
  'store-owner-1', 'store-owner-2', 'store-owner-3', 'store-owner-4', 'store-owner-5', 'store-owner-6', 'store-owner-7', 'store-owner-8',
  'user-minsu', 'user-seoyeon', 'user-jihoon', 'user-yujin', 'user-haneul', 'user-subin', 'user-dohyun', 'user-sohee',
  'user-taehyun', 'user-jisoo', 'user-minho', 'user-naeun', 'user-junho', 'user-hyerin',
  'user-sangjun', 'user-yoona', 'user-dongwook', 'user-chaeyeon', 'user-hyunwoo', 'user-sojung'
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
  ('user-sohee', 'sohee.han@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '한소희', '010-3000-0008', 'USER', 'local', NULL, 'sohee_han', 'https://i.pravatar.cc/128?u=fdms-user-8', 'subscribed', '제주특별자치도 제주시', NULL, NOW() - INTERVAL '8 day'),
  -- 추가 관리자 계정
  ('admin-marketing', 'marketing@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '마케팅 담당자', '010-1000-0006', 'ADMIN', 'local', NULL, 'marketing_admin', 'https://i.pravatar.cc/128?u=fdms-team-6', 'internal', '서울특별시 마포구', 'member', NOW() - INTERVAL '15 day'),
  ('admin-devops', 'devops@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '인프라 관리자', '010-1000-0007', 'ADMIN', 'local', NULL, 'devops_admin', 'https://i.pravatar.cc/128?u=fdms-team-7', 'internal', '서울특별시 강동구', 'member', NOW() - INTERVAL '14 day'),
  -- 추가 스토어 오너 계정
  ('store-owner-4', 'owner4@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '최재민', '010-2000-0004', 'STORE', 'local', NULL, 'busan_pizza_owner', 'https://i.pravatar.cc/128?u=fdms-store-4', 'internal', '부산광역시 해운대구', 'member', NOW() - INTERVAL '15 day'),
  ('store-owner-5', 'owner5@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '송미라', '010-2000-0005', 'STORE', 'local', NULL, 'daejeon_burger_owner', 'https://i.pravatar.cc/128?u=fdms-store-5', 'internal', '대전광역시 유성구', 'member', NOW() - INTERVAL '14 day'),
  ('store-owner-6', 'owner6@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '오준혁', '010-2000-0006', 'STORE', 'local', NULL, 'suwon_sushi_owner', 'https://i.pravatar.cc/128?u=fdms-store-6', 'internal', '경기도 수원시', 'member', NOW() - INTERVAL '13 day'),
  ('store-owner-7', 'owner7@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '임소영', '010-2000-0007', 'STORE', 'local', NULL, 'daegu_chinese_owner', 'https://i.pravatar.cc/128?u=fdms-store-7', 'internal', '대구광역시 중구', 'member', NOW() - INTERVAL '12 day'),
  ('store-owner-8', 'owner8@foodplatform.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '정유나', '010-2000-0008', 'STORE', 'local', NULL, 'mapo_dessert_owner', 'https://i.pravatar.cc/128?u=fdms-store-8', 'internal', '서울특별시 마포구', 'member', NOW() - INTERVAL '11 day'),
  -- 추가 일반 사용자 계정
  ('user-taehyun', 'taehyun.kim@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '김태현', '010-3000-0009', 'USER', 'local', NULL, 'taehyun_kim', 'https://i.pravatar.cc/128?u=fdms-user-9', 'subscribed', '서울특별시 마포구', NULL, NOW() - INTERVAL '7 day'),
  ('user-jisoo', 'jisoo.park@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '박지수', '010-3000-0010', 'USER', 'local', NULL, 'jisoo_park', 'https://i.pravatar.cc/128?u=fdms-user-10', 'subscribed', '서울특별시 용산구', NULL, NOW() - INTERVAL '7 day'),
  ('user-minho', 'minho.lee@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '이민호', '010-3000-0011', 'USER', 'local', NULL, 'minho_lee', 'https://i.pravatar.cc/128?u=fdms-user-11', 'unsubscribed', '경기도 수원시', NULL, NOW() - INTERVAL '6 day'),
  ('user-naeun', 'naeun.choi@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '최나은', '010-3000-0012', 'USER', 'local', NULL, 'naeun_choi', 'https://i.pravatar.cc/128?u=fdms-user-12', 'subscribed', '대전광역시 유성구', NULL, NOW() - INTERVAL '6 day'),
  ('user-junho', 'junho.oh@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '오준호', '010-3000-0013', 'USER', 'local', NULL, 'junho_oh', 'https://i.pravatar.cc/128?u=fdms-user-13', 'subscribed', '대구광역시 수성구', NULL, NOW() - INTERVAL '5 day'),
  ('user-hyerin', 'hyerin.song@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '송혜린', '010-3000-0014', 'USER', 'local', NULL, 'hyerin_song', 'https://i.pravatar.cc/128?u=fdms-user-14', 'bounced', '경기도 고양시', NULL, NOW() - INTERVAL '5 day'),
  ('user-sangjun', 'sangjun.im@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '임상준', '010-3000-0015', 'USER', 'local', NULL, 'sangjun_im', 'https://i.pravatar.cc/128?u=fdms-user-15', 'subscribed', '서울특별시 동작구', NULL, NOW() - INTERVAL '4 day'),
  ('user-yoona', 'yoona.kwon@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '권유나', '010-3000-0016', 'USER', 'local', NULL, 'yoona_kwon', 'https://i.pravatar.cc/128?u=fdms-user-16', 'subscribed', '경기도 용인시', NULL, NOW() - INTERVAL '4 day'),
  ('user-dongwook', 'dongwook.shin@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '신동욱', '010-3000-0017', 'USER', 'google', 'google-uid-dongwook', 'dongwook_shin', 'https://i.pravatar.cc/128?u=fdms-user-17', 'unsubscribed', '서울특별시 강북구', NULL, NOW() - INTERVAL '3 day'),
  ('user-chaeyeon', 'chaeyeon.baek@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '백채연', '010-3000-0018', 'USER', 'google', 'google-uid-chaeyeon', 'chaeyeon_baek', 'https://i.pravatar.cc/128?u=fdms-user-18', 'subscribed', '경기도 성남시', NULL, NOW() - INTERVAL '3 day'),
  ('user-hyunwoo', 'hyunwoo.jang@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '장현우', '010-3000-0019', 'USER', 'kakao', 'kakao-uid-hyunwoo', 'hyunwoo_jang', 'https://i.pravatar.cc/128?u=fdms-user-19', 'subscribed', '부산광역시 남구', NULL, NOW() - INTERVAL '2 day'),
  ('user-sojung', 'sojung.hong@fdms.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '홍소정', '010-3000-0020', 'USER', 'kakao', 'kakao-uid-sojung', 'sojung_hong', 'https://i.pravatar.cc/128?u=fdms-user-20', 'subscribed', '인천광역시 남동구', NULL, NOW() - INTERVAL '1 day');

INSERT INTO addresses (id, user_id, label, line1, line2, city, state, postal_code, country, primary_address, created_at) VALUES
  ('addr-001', 'user-minsu', '집', '강남대로 123', '101동 1201호', '서울특별시', '강남구', '06236', '대한민국', TRUE, NOW() - INTERVAL '10 day'),
  ('addr-002', 'user-minsu', '회사', '테헤란로 51', '8층', '서울특별시', '강남구', '06142', '대한민국', FALSE, NOW() - INTERVAL '9 day'),
  ('addr-003', 'user-seoyeon', '집', '올림픽로 300', '2202호', '서울특별시', '송파구', '05551', '대한민국', TRUE, NOW() - INTERVAL '8 day'),
  ('addr-004', 'user-jihoon', '집', '판교역로 166', NULL, '경기도', '성남시', '13494', '대한민국', TRUE, NOW() - INTERVAL '7 day'),
  ('addr-005', 'user-yujin', '집', '컨벤시아대로 165', '1205호', '인천광역시', '연수구', '22004', '대한민국', TRUE, NOW() - INTERVAL '6 day'),
  ('addr-006', 'user-subin', '집', '센텀중앙로 90', '1803호', '부산광역시', '해운대구', '48058', '대한민국', TRUE, NOW() - INTERVAL '5 day'),
  ('addr-007', 'user-dohyun', '집', '상무대로 1 번지', '301동 405호', '광주광역시', '서구', '61913', '대한민국', TRUE, NOW() - INTERVAL '5 day'),
  ('addr-008', 'user-sohee', '집', '연동로 100', '제주 힐링하우스 201호', '제주특별자치도', '제주시', '63122', '대한민국', TRUE, NOW() - INTERVAL '4 day'),
  ('addr-009', 'user-taehyun', '집', '월드컵북로 400', '상암 아파트 502호', '서울특별시', '마포구', '03925', '대한민국', TRUE, NOW() - INTERVAL '4 day'),
  ('addr-010', 'user-taehyun', '회사', '성수이로 77', '3층', '서울특별시', '성동구', '04781', '대한민국', FALSE, NOW() - INTERVAL '3 day'),
  ('addr-011', 'user-jisoo', '집', '한강대로 92', '1102호', '서울특별시', '용산구', '04317', '대한민국', TRUE, NOW() - INTERVAL '4 day'),
  ('addr-012', 'user-minho', '집', '경수대로 697', '2층', '경기도', '수원시', '16517', '대한민국', TRUE, NOW() - INTERVAL '3 day'),
  ('addr-013', 'user-naeun', '집', '엑스포로 107', '유성타워 803호', '대전광역시', '유성구', '34126', '대한민국', TRUE, NOW() - INTERVAL '3 day'),
  ('addr-014', 'user-junho', '집', '동대구로 240', '대구 현대 아파트 601호', '대구광역시', '수성구', '42111', '대한민국', TRUE, NOW() - INTERVAL '2 day'),
  ('addr-015', 'user-hyunwoo', '집', '수영로 309', '해운대 타워 1501호', '부산광역시', '남구', '48247', '대한민국', TRUE, NOW() - INTERVAL '1 day');

INSERT INTO favorite_stores (id, user_id, store_id, name, category, rating, delivery_time, min_order, image_icon, created_at) VALUES
  ('fav-001', 'user-minsu', 'store-seoul-1', '강남 한식당', 'KOREAN', 4.7, '32분', '15000원', 'i-lucide-store', NOW() - INTERVAL '4 day'),
  ('fav-002', 'user-minsu', 'store-seoul-2', '송파 치킨클럽', 'CHICKEN', 4.8, '28분', '18000원', 'i-lucide-store', NOW() - INTERVAL '3 day'),
  ('fav-003', 'user-seoyeon', 'store-incheon-1', '연수 브런치랩', 'CAFE', 4.6, '25분', '12000원', 'i-lucide-store', NOW() - INTERVAL '3 day'),
  ('fav-004', 'user-yujin', 'store-seoul-1', '강남 한식당', 'KOREAN', 4.7, '32분', '15000원', 'i-lucide-store', NOW() - INTERVAL '2 day'),
  ('fav-005', 'user-jihoon', 'store-seoul-2', '송파 치킨클럽', 'CHICKEN', 4.8, '28분', '18000원', 'i-lucide-store', NOW() - INTERVAL '2 day'),
  ('fav-006', 'user-subin', 'store-seoul-1', '강남 한식당', 'KOREAN', 4.7, '32분', '15000원', 'i-lucide-store', NOW() - INTERVAL '2 day'),
  ('fav-007', 'user-haneul', 'store-incheon-1', '연수 브런치랩', 'CAFE', 4.6, '25분', '12000원', 'i-lucide-store', NOW() - INTERVAL '1 day'),
  ('fav-008', 'user-taehyun', 'store-busan-1', '해운대 화덕피자', 'PIZZA', 4.5, '35분', '20000원', 'i-lucide-pizza', NOW() - INTERVAL '1 day'),
  ('fav-009', 'user-taehyun', 'store-seoul-2', '송파 치킨클럽', 'CHICKEN', 4.8, '28분', '18000원', 'i-lucide-store', NOW() - INTERVAL '20 hour'),
  ('fav-010', 'user-jisoo', 'store-daejeon-1', '유성 스매시버거', 'BURGER', 4.4, '30분', '13000원', 'i-lucide-store', NOW() - INTERVAL '18 hour'),
  ('fav-011', 'user-minho', 'store-suwon-1', '수원 스시 오마카세', 'JAPANESE', 4.9, '40분', '25000원', 'i-lucide-utensils', NOW() - INTERVAL '16 hour'),
  ('fav-012', 'user-junho', 'store-daegu-1', '대구 짬뽕나라', 'CHINESE', 4.3, '30분', '10000원', 'i-lucide-store', NOW() - INTERVAL '12 hour'),
  ('fav-013', 'user-hyunwoo', 'store-busan-1', '해운대 화덕피자', 'PIZZA', 4.5, '35분', '20000원', 'i-lucide-pizza', NOW() - INTERVAL '10 hour'),
  ('fav-014', 'user-sojung', 'store-seoul-3', '마포 디저트카페', 'DESSERT', 4.7, '22분', '8000원', 'i-lucide-cake', NOW() - INTERVAL '8 hour'),
  ('fav-015', 'user-chaeyeon', 'store-daejeon-1', '유성 스매시버거', 'BURGER', 4.4, '30분', '13000원', 'i-lucide-store', NOW() - INTERVAL '5 hour');

INSERT INTO cart_items (id, user_id, store_id, store_name, menu_id, menu_name, quantity, price, created_at) VALUES
  ('cart-001', 'user-minsu', 'store-seoul-1', '강남 한식당', 'menu-001', '직화 제육덮밥', 1, 9500, NOW() - INTERVAL '2 hour'),
  ('cart-002', 'user-minsu', 'store-seoul-1', '강남 한식당', 'menu-002', '소불고기 정식', 2, 13500, NOW() - INTERVAL '110 minute'),
  ('cart-003', 'user-seoyeon', 'store-seoul-2', '송파 치킨클럽', 'menu-004', '후라이드 치킨', 1, 21000, NOW() - INTERVAL '90 minute'),
  ('cart-004', 'user-subin', 'store-incheon-1', '연수 브런치랩', 'menu-008', '에그 베이글 샌드위치', 1, 8900, NOW() - INTERVAL '70 minute'),
  ('cart-005', 'user-taehyun', 'store-busan-1', '해운대 화덕피자', 'menu-010', '마르게리타 화덕피자', 1, 22000, NOW() - INTERVAL '55 minute'),
  ('cart-006', 'user-taehyun', 'store-busan-1', '해운대 화덕피자', 'menu-012', '고르곤졸라 피자', 1, 26000, NOW() - INTERVAL '50 minute'),
  ('cart-007', 'user-jisoo', 'store-daejeon-1', '유성 스매시버거', 'menu-013', '스매시버거', 2, 12000, NOW() - INTERVAL '45 minute'),
  ('cart-008', 'user-minho', 'store-suwon-1', '수원 스시 오마카세', 'menu-016', '연어 초밥 세트', 1, 28000, NOW() - INTERVAL '40 minute'),
  ('cart-009', 'user-junho', 'store-daegu-1', '대구 짬뽕나라', 'menu-019', '짬뽕', 1, 10000, NOW() - INTERVAL '35 minute'),
  ('cart-010', 'user-junho', 'store-daegu-1', '대구 짬뽕나라', 'menu-021', '탕수육', 1, 24000, NOW() - INTERVAL '33 minute'),
  ('cart-011', 'user-sojung', 'store-seoul-3', '마포 디저트카페', 'menu-022', '크로플', 2, 8500, NOW() - INTERVAL '25 minute'),
  ('cart-012', 'user-hyunwoo', 'store-busan-1', '해운대 화덕피자', 'menu-011', '페퍼로니 피자', 1, 25000, NOW() - INTERVAL '20 minute');

INSERT INTO mail_messages (id, from_user_id, subject, body, unread, created_at) VALUES
  (1, 'user-seoyeon', '주문 지연 문의 대응 요청', '강남권 주문 지연 관련 문의가 증가하고 있습니다. 점심 피크 시간 주문 처리 현황을 확인해 주세요.', TRUE, NOW() - INTERVAL '1 hour 12 minute'),
  (2, 'user-yujin', '쿠폰 정책 검토 회의', '신규 가입 쿠폰과 재방문 쿠폰 정책을 3월 주간 캠페인 일정에 맞춰 재검토해야 합니다.', FALSE, NOW() - INTERVAL '1 day 2 hour'),
  (3, 'user-subin', '정산 오류 후보 건 공유', '정산 금액이 주문 총액과 차이 나는 후보 건 3건을 확인했습니다. 거래 로그 화면 연동 전 임시 검토용입니다.', TRUE, NOW() - INTERVAL '2 day 3 hour'),
  (4, 'user-dohyun', '이벤트 발행 실패 로그 확인', 'service-event 적재 실패 건이 있어 재시도 전 traceId 기준 필터 화면이 필요합니다.', FALSE, NOW() - INTERVAL '3 day 4 hour'),
  (5, 'user-taehyun', '신규 가입자 온보딩 이메일 전송 요청', '이번 주 신규 가입자 12명을 대상으로 온보딩 이메일 발송이 필요합니다. 마케팅팀 검토 후 진행 부탁드립니다.', TRUE, NOW() - INTERVAL '4 day 2 hour'),
  (6, 'user-minho', '배달 기사 앱 GPS 오류 신고', '배달 기사 박소연 님이 GPS 위치가 실제와 200m 이상 차이 난다고 신고했습니다. 로그 확인 부탁드립니다.', TRUE, NOW() - INTERVAL '5 day 1 hour'),
  (7, 'user-junho', '리뷰 어뷰징 계정 처리 요청', '동일 IP에서 단시간 내 리뷰 30건 이상 작성된 계정이 감지되었습니다. 어뷰징 여부 검토 후 제재 부탁드립니다.', FALSE, NOW() - INTERVAL '6 day 3 hour'),
  (8, 'user-hyunwoo', '피크타임 주문 실패율 보고', '저녁 7-9시 피크타임 주문 실패율이 3.2%로 평소 대비 2배 증가했습니다. 서버 스케일 아웃 검토가 필요합니다.', TRUE, NOW() - INTERVAL '7 day'),
  (9, 'user-chaeyeon', '쿠폰 중복 사용 이슈 제보', '동일 쿠폰 코드로 2회 결제된 사례가 발견되었습니다. 쿠폰 적용 로직 점검이 필요합니다.', FALSE, NOW() - INTERVAL '8 day 2 hour'),
  (10, 'user-sojung', '메뉴 가격 오기입 정정 요청', '마포 디저트카페 크로플 메뉴의 가격이 8500원이 아닌 9000원으로 잘못 입력되었습니다. 정정 부탁드립니다.', TRUE, NOW() - INTERVAL '9 day');

INSERT INTO user_notifications (id, user_id, body, unread, created_at) VALUES
  (1, 'user-seoyeon', '1:1 문의를 등록했습니다', TRUE, NOW() - INTERVAL '12 minute'),
  (2, 'user-minsu', '신규 주문이 접수되었습니다', FALSE, NOW() - INTERVAL '1 hour'),
  (3, 'user-subin', '환불 요청을 남겼습니다', TRUE, NOW() - INTERVAL '2 hour'),
  (4, 'user-yujin', '리뷰 답글 등록을 요청했습니다', FALSE, NOW() - INTERVAL '5 hour'),
  (5, 'user-sohee', '이벤트 페이지를 조회했습니다', FALSE, NOW() - INTERVAL '1 day'),
  (6, 'user-jihoon', '장바구니에 메뉴를 다시 담았습니다', FALSE, NOW() - INTERVAL '2 day'),
  (7, 'user-haneul', '이메일 수신 상태가 반송으로 변경되었습니다', TRUE, NOW() - INTERVAL '3 day'),
  (8, 'user-dohyun', '관리자 공지를 확인했습니다', FALSE, NOW() - INTERVAL '4 day'),
  (9, 'user-taehyun', '주문이 배달원에게 배정되었습니다', TRUE, NOW() - INTERVAL '30 minute'),
  (10, 'user-jisoo', '쿠폰이 적용되어 결제가 완료되었습니다', FALSE, NOW() - INTERVAL '1 hour 30 minute'),
  (11, 'user-minho', '잔여 포인트가 1,000P 미만입니다', TRUE, NOW() - INTERVAL '3 hour'),
  (12, 'user-naeun', '리뷰를 작성해 포인트를 받으세요', FALSE, NOW() - INTERVAL '6 hour'),
  (13, 'user-junho', '주문이 완료되었습니다. 맛있게 드세요!', FALSE, NOW() - INTERVAL '1 day 1 hour'),
  (14, 'user-hyerin', '이메일 수신 상태가 반송으로 변경되었습니다', TRUE, NOW() - INTERVAL '1 day 12 hour'),
  (15, 'user-sangjun', '신규 쿠폰이 발급되었습니다', FALSE, NOW() - INTERVAL '2 day'),
  (16, 'user-yoona', '즐겨찾기 매장에 새 메뉴가 추가되었습니다', FALSE, NOW() - INTERVAL '2 day 6 hour'),
  (17, 'user-dongwook', '배달이 지연되고 있습니다. 양해 부탁드립니다', TRUE, NOW() - INTERVAL '3 day 2 hour'),
  (18, 'user-chaeyeon', '주문 취소가 완료되었습니다', FALSE, NOW() - INTERVAL '3 day 8 hour'),
  (19, 'user-hyunwoo', '배달이 완료되었습니다. 리뷰를 남겨주세요', FALSE, NOW() - INTERVAL '4 day 3 hour'),
  (20, 'user-sojung', '장바구니에 담은 메뉴의 가격이 변경되었습니다', TRUE, NOW() - INTERVAL '5 day');