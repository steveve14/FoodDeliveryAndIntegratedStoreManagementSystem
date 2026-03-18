-- Datasource: jdbc:postgresql://192.168.0.25:6000/db_store

DELETE FROM menu WHERE id IN ('menu-001', 'menu-002', 'menu-003', 'menu-004', 'menu-005', 'menu-006', 'menu-007', 'menu-008', 'menu-009',
  'menu-010', 'menu-011', 'menu-012', 'menu-013', 'menu-014', 'menu-015', 'menu-016', 'menu-017', 'menu-018',
  'menu-019', 'menu-020', 'menu-021', 'menu-022', 'menu-023', 'menu-024', 'menu-025', 'menu-026', 'menu-027', 'menu-028',
  'menu-029', 'menu-030', 'menu-031', 'menu-032', 'menu-033', 'menu-034', 'menu-035', 'menu-036', 'menu-037');
DELETE FROM store WHERE id IN ('store-seoul-1', 'store-seoul-2', 'store-incheon-1', 'store-busan-1', 'store-daejeon-1', 'store-suwon-1', 'store-daegu-1', 'store-seoul-3',
  'store-seoul-4', 'store-seoul-5', 'store-suwon-2', 'store-seongnam-1', 'store-seongnam-2', 'store-seoul-6',
  'store-seed-001', 'store-seed-002', 'store-seed-003', 'store-seed-004', 'store-seed-005');

INSERT INTO store (id, owner_id, name, address, phone, category, status, latitude, longitude, min_order_amount, rating_avg, description, opening_hours, created_at) VALUES
  ('store-seoul-1', 'store-owner-1', '강남 한식당', '서울특별시 강남구 강남대로 100', '02-555-1001', 'KOREAN', 'OPEN', 37.4979, 127.0276, 15000, 4.7, '직장인 점심 수요가 많은 한식 배달 전문점', '10:00-21:00', NOW() - INTERVAL '15 day'),
  ('store-seoul-2', 'store-owner-2', '송파 치킨클럽', '서울특별시 송파구 오금로 120', '02-555-1002', 'CHICKEN', 'OPEN', 37.5145, 127.1059, 18000, 4.8, '주문량이 많은 야식 전문 치킨 매장', '11:00-23:30', NOW() - INTERVAL '14 day'),
  ('store-incheon-1', 'store-owner-3', '연수 브런치랩', '인천광역시 연수구 센트럴로 240', '032-555-1003', 'CAFE', 'OPEN', 37.3925, 126.6378, 12000, 4.6, '샌드위치와 커피를 판매하는 브런치 전문점', '08:30-20:00', NOW() - INTERVAL '13 day'),
  ('store-busan-1', 'store-owner-4', '해운대 화덕피자', '부산광역시 해운대구 해운대해변로 234', '051-555-1004', 'PIZZA', 'OPEN', 35.1630, 129.1605, 20000, 4.5, '화덕쳠에서 직접 구웨 나폴리 스타일 피자', '11:00-23:00', NOW() - INTERVAL '12 day'),
  ('store-daejeon-1', 'store-owner-5', '유성 스매시버거', '대전광역시 유성구 엑스포로 100', '042-555-1005', 'BURGER', 'OPEN', 36.3742, 127.3669, 13000, 4.4, '신선한 마비로 마다 눈암에서 눏는 스매시버거', '10:30-22:00', NOW() - INTERVAL '11 day'),
  ('store-suwon-1', 'store-owner-6', '수원 스시 오마카세', '경기도 수원시 영통구 경수대로 888', '031-555-1006', 'JAPANESE', 'OPEN', 37.2636, 127.0286, 25000, 4.9, '어심 작싼부터 선별한 신선한 재료로 선보이는 스시 오마카세', '11:30-22:00', NOW() - INTERVAL '10 day'),
  ('store-daegu-1', 'store-owner-7', '대구 짬뽕나라', '대구광역시 연케구 동대구로 5', '053-555-1007', 'CHINESE', 'OPEN', 35.8714, 128.6014, 10000, 4.3, '먹켜야 늘 생각나는 주문자 선정 짬뽕 맛집', '10:00-21:30', NOW() - INTERVAL '9 day'),
  ('store-seoul-3', 'store-owner-8', '마포 디저트카페', '서울특별시 마포구 어쉰새로 47', '02-555-1008', 'DESSERT', 'OPEN', 37.5547, 126.9223, 8000, 4.7, '브런치 디저트와 시즈널 핑케이크를 전문으로 하는 감각적인 카페', '09:00-21:00', NOW() - INTERVAL '8 day'),
  ('store-seoul-4', 'store-owner-9', '홍대 분식연구소', '서울특별시 마포구 와우산로 29', '02-555-1009', 'SNACK', 'OPEN', 37.5559, 126.9236, 9000, 4.5, '떡볶이와 튀김, 라볶이를 빠르게 배달하는 분식 전문점', '10:30-22:30', NOW() - INTERVAL '7 day'),
  ('store-seoul-5', 'store-owner-10', '강서 심야포차', '서울특별시 강서구 공항대로 247', '02-555-1010', 'NIGHT', 'OPEN', 37.5586, 126.8351, 18000, 4.4, '곱도리탕과 국물 안주가 강한 야식 전문 포차', '18:00-03:00', NOW() - INTERVAL '7 day'),
  ('store-suwon-2', 'store-owner-11', '수원 장충보쌈', '경기도 수원시 팔달구 정조로 812', '031-555-1011', 'BOSSAM', 'OPEN', 37.2803, 127.0155, 22000, 4.6, '앞다리 수육과 막국수를 함께 즐기는 족발·보쌈 전문점', '11:00-23:30', NOW() - INTERVAL '6 day'),
  ('store-seongnam-1', 'store-owner-12', '판교 아시안키친', '경기도 성남시 분당구 판교역로 152', '031-555-1012', 'ASIAN', 'OPEN', 37.3947, 127.1112, 15000, 4.5, '팟타이와 나시고렝을 중심으로 한 아시안 푸드 전문점', '11:00-21:30', NOW() - INTERVAL '6 day'),
  ('store-seongnam-2', 'store-owner-13', '분당 그린볼', '경기도 성남시 분당구 정자일로 135', '031-555-1013', 'SALAD', 'OPEN', 37.3676, 127.1079, 13000, 4.7, '프로틴 볼과 샐러드를 판매하는 건강식 전문 매장', '09:00-20:30', NOW() - INTERVAL '5 day'),
  ('store-seoul-6', 'store-owner-14', '용산 한끼도시락', '서울특별시 용산구 한강대로 40', '02-555-1014', 'LUNCHBOX', 'OPEN', 37.5296, 126.9648, 11000, 4.6, '직장인 점심 수요에 맞춘 수제 도시락 전문점', '08:00-19:30', NOW() - INTERVAL '5 day'),
  ('store-seed-001', 'store-owner-1', '시드 강동 분식', '서울특별시 강동구 천호대로 200', '02-599-2001', 'SNACK', 'OPEN', 37.5381, 127.1236, 9000, 4.3, 'DB 시드 확장용 분식 매장', '10:00-22:00', NOW() - INTERVAL '4 hour'),
  ('store-seed-002', 'store-owner-2', '시드 송도 샐러드', '인천광역시 연수구 인천타워대로 323', '032-599-2002', 'SALAD', 'OPEN', 37.3826, 126.6569, 12000, 4.5, 'DB 시드 확장용 샐러드 매장', '09:00-20:00', NOW() - INTERVAL '4 hour'),
  ('store-seed-003', 'store-owner-3', '시드 광안 파스타', '부산광역시 수영구 광안해변로 250', '051-599-2003', 'WESTERN', 'OPEN', 35.1534, 129.1185, 17000, 4.4, 'DB 시드 확장용 파스타 매장', '11:00-22:30', NOW() - INTERVAL '3 hour'),
  ('store-seed-004', 'store-owner-4', '시드 성수 디저트', '서울특별시 성동구 왕십리로 112', '02-599-2004', 'DESSERT', 'OPEN', 37.5444, 127.0557, 8000, 4.8, 'DB 시드 확장용 디저트 매장', '10:00-21:30', NOW() - INTERVAL '3 hour'),
  ('store-seed-005', 'store-owner-5', '시드 대전 국밥', '대전광역시 서구 둔산로 75', '042-599-2005', 'KOREAN', 'OPEN', 36.3504, 127.3845, 10000, 4.2, 'DB 시드 확장용 한식 매장', '07:00-21:00', NOW() - INTERVAL '2 hour');

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
  ('menu-025', 'store-seoul-3', '시즈널 단호박 케이크', '국내산 단호박과 문어설탕이 어우러진 뉴알코먼리스트 케이크 1조각', 11000, TRUE, NOW() - INTERVAL '5 day'),
  -- 신규 카테고리 샘플 메뉴
  ('menu-026', 'store-seoul-4', '즉석 떡볶이', '쫄깃한 밀떡과 어묵이 들어간 대표 분식 메뉴', 9500, TRUE, NOW() - INTERVAL '5 day'),
  ('menu-027', 'store-seoul-4', '모둠 튀김', '김말이, 야채, 새우튀김을 한 번에 즐기는 사이드', 7000, TRUE, NOW() - INTERVAL '5 day'),
  ('menu-028', 'store-seoul-5', '곱도리탕', '곱창과 닭볶음탕을 매콤하게 끓여낸 심야 인기 메뉴', 28000, TRUE, NOW() - INTERVAL '4 day'),
  ('menu-029', 'store-seoul-5', '해물짬뽕탕', '칼칼한 국물과 해산물이 푸짐한 야식 안주', 24000, TRUE, NOW() - INTERVAL '4 day'),
  ('menu-030', 'store-suwon-2', '보쌈 중', '촉촉한 앞다리 수육과 무김치를 곁들인 보쌈', 32000, TRUE, NOW() - INTERVAL '4 day'),
  ('menu-031', 'store-suwon-2', '족발 소', '쫀득한 식감이 살아 있는 미니 족발', 29000, TRUE, NOW() - INTERVAL '4 day'),
  ('menu-032', 'store-seongnam-1', '팟타이', '새우와 숙주를 볶아낸 태국식 볶음면', 14500, TRUE, NOW() - INTERVAL '3 day'),
  ('menu-033', 'store-seongnam-1', '나시고렝', '인도네시아식 볶음밥에 계란프라이를 곁들인 메뉴', 15000, TRUE, NOW() - INTERVAL '3 day'),
  ('menu-034', 'store-seongnam-2', '치킨 시저 샐러드', '닭가슴살과 로메인, 파르메산 치즈가 들어간 샐러드', 12900, TRUE, NOW() - INTERVAL '3 day'),
  ('menu-035', 'store-seongnam-2', '연어 포케볼', '훈제 연어와 현미밥, 채소를 담은 포케볼', 14900, TRUE, NOW() - INTERVAL '3 day'),
  ('menu-036', 'store-seoul-6', '직화 제육 도시락', '직화 제육과 계란말이, 밑반찬이 포함된 도시락', 11000, TRUE, NOW() - INTERVAL '2 day'),
  ('menu-037', 'store-seoul-6', '소불고기 도시락', '소불고기와 계절 반찬을 담은 프리미엄 도시락', 12500, TRUE, NOW() - INTERVAL '2 day');