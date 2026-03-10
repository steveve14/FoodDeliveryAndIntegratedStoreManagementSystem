-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_store

DELETE FROM menu WHERE id IN ('menu-001', 'menu-002', 'menu-003', 'menu-004', 'menu-005', 'menu-006', 'menu-007', 'menu-008', 'menu-009',
  'menu-010', 'menu-011', 'menu-012', 'menu-013', 'menu-014', 'menu-015', 'menu-016', 'menu-017', 'menu-018',
  'menu-019', 'menu-020', 'menu-021', 'menu-022', 'menu-023', 'menu-024', 'menu-025');
DELETE FROM store WHERE id IN ('store-seoul-1', 'store-seoul-2', 'store-incheon-1', 'store-busan-1', 'store-daejeon-1', 'store-suwon-1', 'store-daegu-1', 'store-seoul-3');

INSERT INTO store (id, owner_id, name, address, phone, category, status, latitude, longitude, min_order_amount, rating_avg, description, opening_hours, created_at) VALUES
  ('store-seoul-1', 'store-owner-1', '강남 한식당', '서울특별시 강남구 강남대로 100', '02-555-1001', 'KOREAN', 'OPEN', 37.4979, 127.0276, 15000, 4.7, '직장인 점심 수요가 많은 한식 배달 전문점', '10:00-21:00', NOW() - INTERVAL '15 day'),
  ('store-seoul-2', 'store-owner-2', '송파 치킨클럽', '서울특별시 송파구 오금로 120', '02-555-1002', 'CHICKEN', 'OPEN', 37.5145, 127.1059, 18000, 4.8, '주문량이 많은 야식 전문 치킨 매장', '11:00-23:30', NOW() - INTERVAL '14 day'),
  ('store-incheon-1', 'store-owner-3', '연수 브런치랩', '인천광역시 연수구 센트럴로 240', '032-555-1003', 'CAFE', 'OPEN', 37.3925, 126.6378, 12000, 4.6, '샌드위치와 커피를 판매하는 브런치 전문점', '08:30-20:00', NOW() - INTERVAL '13 day'),
  ('store-busan-1', 'store-owner-4', '해운대 화덕피자', '부산광역시 해운대구 해운대해변로 234', '051-555-1004', 'PIZZA', 'OPEN', 35.1630, 129.1605, 20000, 4.5, '화덕쳠에서 직접 구웨 나폴리 스타일 피자', '11:00-23:00', NOW() - INTERVAL '12 day'),
  ('store-daejeon-1', 'store-owner-5', '유성 스매시버거', '대전광역시 유성구 엑스포로 100', '042-555-1005', 'BURGER', 'OPEN', 36.3742, 127.3669, 13000, 4.4, '신선한 마비로 마다 눈암에서 눏는 스매시버거', '10:30-22:00', NOW() - INTERVAL '11 day'),
  ('store-suwon-1', 'store-owner-6', '수원 스시 오마카세', '경기도 수원시 영통구 경수대로 888', '031-555-1006', 'JAPANESE', 'OPEN', 37.2636, 127.0286, 25000, 4.9, '어심 작싼부터 선별한 신선한 재료로 선보이는 스시 오마카세', '11:30-22:00', NOW() - INTERVAL '10 day'),
  ('store-daegu-1', 'store-owner-7', '대구 짬뽕나라', '대구광역시 연케구 동대구로 5', '053-555-1007', 'CHINESE', 'OPEN', 35.8714, 128.6014, 10000, 4.3, '먹켜야 늘 생각나는 주문자 선정 짬뽕 맛집', '10:00-21:30', NOW() - INTERVAL '9 day'),
  ('store-seoul-3', 'store-owner-8', '마포 디저트카페', '서울특별시 마포구 어쉰새로 47', '02-555-1008', 'DESSERT', 'OPEN', 37.5547, 126.9223, 8000, 4.7, '브런치 디저트와 시즈널 핑케이크를 전문으로 하는 감각적인 카페', '09:00-21:00', NOW() - INTERVAL '8 day');

INSERT INTO menu (id, store_id, name, description, price, available, created_at) VALUES
  ('menu-001', 'store-seoul-1', '직화 제육덮밥', '매콤한 제육과 반숙 계란이 올라간 덮밥', 9500, TRUE, NOW() - INTERVAL '12 day'),
  ('menu-002', 'store-seoul-1', '소불고기 정식', '불고기와 밑반찬이 포함된 정식', 13500, TRUE, NOW() - INTERVAL '12 day'),
  ('menu-003', 'store-seoul-1', '된장찌개 세트', '집된장 스타일 찌개와 공기밥', 9000, TRUE, NOW() - INTERVAL '11 day'),
  ('menu-004', 'store-seoul-2', '후라이드 치킨', '바삭한 시그니처 후라이드 치킨', 21000, TRUE, NOW() - INTERVAL '12 day'),
  ('menu-005', 'store-seoul-2', '양념 반 후라이드 반', '반반 조합 인기 메뉴', 23000, TRUE, NOW() - INTERVAL '11 day'),
  ('menu-006', 'store-seoul-2', '치즈볼 세트', '치킨과 함께 주문 많은 사이드 세트', 6500, TRUE, NOW() - INTERVAL '10 day'),
  ('menu-007', 'store-incheon-1', '아메리카노', '브런치랩 시그니처 원두', 4500, TRUE, NOW() - INTERVAL '10 day'),
  ('menu-008', 'store-incheon-1', '에그 베이글 샌드위치', '베이글과 스크램블 에그 조합', 8900, TRUE, NOW() - INTERVAL '10 day'),
  ('menu-009', 'store-incheon-1', '리코타 샐러드', '가벼운 브런치 샐러드', 9800, TRUE, NOW() - INTERVAL '9 day'),
  -- 해운대 화덕피자 메뉴
  ('menu-010', 'store-busan-1', '마르게리타 화덕피자', '신선한 모짐라 치즈와 토마토소스의 트레이드마크 피자', 22000, TRUE, NOW() - INTERVAL '10 day'),
  ('menu-011', 'store-busan-1', '페퍼로니 피자', '스파이시 페퍼로니와 올리브가 가득한 피자', 25000, TRUE, NOW() - INTERVAL '10 day'),
  ('menu-012', 'store-busan-1', '고르곤졸라 피자', '신콘한 고르곤즐라 크림과 꽁의 조화', 26000, TRUE, NOW() - INTERVAL '9 day'),
  -- 유성 스매시버거 메뉴
  ('menu-013', 'store-daejeon-1', '스매시버거', '신선한 선데 스매시와 브리오치즈로 마릴리 쇼가 일품인 버거', 12000, TRUE, NOW() - INTERVAL '9 day'),
  ('menu-014', 'store-daejeon-1', '치즈버거 세트', '더블 치즈버거 + 쉘 프라이 + 콜라 세트당', 15000, TRUE, NOW() - INTERVAL '8 day'),
  ('menu-015', 'store-daejeon-1', '베이컨 더블 버거', '두툴한 패티에 베이컨과 카라멜리아드 양파가 들어간 데일리 베스트셀러', 16000, TRUE, NOW() - INTERVAL '8 day'),
  -- 수원 스시 오마카세 메뉴
  ('menu-016', 'store-suwon-1', '연어 초밥 세트', '노르웨이 연어와 가리 치라시 세트8pcs 구성', 28000, TRUE, NOW() - INTERVAL '8 day'),
  ('menu-017', 'store-suwon-1', '요거문통 참치 사시미', '호주산 사바에 음세으준 요거문통 참치', 32000, TRUE, NOW() - INTERVAL '7 day'),
  ('menu-018', 'store-suwon-1', '새우 텐동', '대생 새우와 들깨름내음 좀체 딥티쿠라', 18000, TRUE, NOW() - INTERVAL '7 day'),
  -- 대구 짬뽕나라 메뉴
  ('menu-019', 'store-daegu-1', '짬뽕', '옥수수수연기와 해산물이 들어간 진한 맛의 짬뽕', 10000, TRUE, NOW() - INTERVAL '7 day'),
  ('menu-020', 'store-daegu-1', '짜장면', '진한 사나이 짜장으로 만든 시그니치 짜장면', 9000, TRUE, NOW() - INTERVAL '7 day'),
  ('menu-021', 'store-daegu-1', '탕수육', '바삭한 돼지고기와 신선한 쇼스의 황금 조합', 24000, TRUE, NOW() - INTERVAL '6 day'),
  -- 마포 디저트카페 메뉴
  ('menu-022', 'store-seoul-3', '크로플', '버터를 저어 구운 바트킵크래커 + 시즈널 크림', 8500, TRUE, NOW() - INTERVAL '6 day'),
  ('menu-023', 'store-seoul-3', '티라미수', '마스카르폰에 식취븀레 치즈를 업은 이탈리아동 디저트', 7500, TRUE, NOW() - INTERVAL '6 day'),
  ('menu-024', 'store-seoul-3', '아이스크림 와플', '편속한 와플 반죽에 바닐라 아이스크림을 없은 홉보 디저트', 9000, TRUE, NOW() - INTERVAL '5 day'),
  ('menu-025', 'store-seoul-3', '시즈널 단호박 케이크', '국내산 단호박과 문어설탕이 어우러진 뉴알코먼리스트 케이크 1조각', 11000, TRUE, NOW() - INTERVAL '5 day');