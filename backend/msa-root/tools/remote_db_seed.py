from __future__ import annotations

import os
from dataclasses import dataclass
from datetime import datetime, timedelta, timezone

import psycopg


DB_HOST = os.getenv("SEED_DB_HOST", "192.168.0.25")
DB_PORT = int(os.getenv("SEED_DB_PORT", "6000"))
DB_USER = os.getenv("SEED_DB_USER", "postgres")
DB_PASSWORD = os.getenv("SEED_DB_PASSWORD", os.getenv("DB_PASSWORD", "1234"))
NOW = datetime.now(timezone.utc)
PASSWORD_HASH = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"


def ago(*, days: int = 0, hours: int = 0, minutes: int = 0) -> datetime:
    return NOW - timedelta(days=days, hours=hours, minutes=minutes)


@dataclass(frozen=True)
class DbConfig:
    dbname: str

    def dsn(self) -> str:
        return f"host={DB_HOST} port={DB_PORT} user={DB_USER} password={DB_PASSWORD} dbname={self.dbname}"


MAINTENANCE_DB = DbConfig("postgres")
TARGET_DATABASES = ["db_user", "db_store", "db_order", "db_delivery", "db_event"]

USER_SCHEMA_SQL = [
    """
    CREATE TABLE IF NOT EXISTS users (
        id VARCHAR(100) PRIMARY KEY,
        email VARCHAR(255) UNIQUE NOT NULL,
        password_hash VARCHAR(255),
        name VARCHAR(50) NOT NULL,
        phone VARCHAR(20),
        roles VARCHAR(20) NOT NULL,
        provider VARCHAR(20),
        provider_id VARCHAR(255),
        username VARCHAR(100),
        avatar_url TEXT,
        marketing_status VARCHAR(30),
        location VARCHAR(255),
        team_role VARCHAR(30),
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
    "ALTER TABLE users ADD COLUMN IF NOT EXISTS username VARCHAR(100)",
    "ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url TEXT",
    "ALTER TABLE users ADD COLUMN IF NOT EXISTS marketing_status VARCHAR(30)",
    "ALTER TABLE users ADD COLUMN IF NOT EXISTS location VARCHAR(255)",
    "ALTER TABLE users ADD COLUMN IF NOT EXISTS team_role VARCHAR(30)",
    "CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)",
    "CREATE INDEX IF NOT EXISTS idx_users_roles ON users(roles)",
    "CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)",
    """
    CREATE TABLE IF NOT EXISTS addresses (
        id VARCHAR(100) PRIMARY KEY,
        user_id VARCHAR(100) NOT NULL,
        label VARCHAR(100),
        line1 VARCHAR(255) NOT NULL,
        line2 VARCHAR(255),
        city VARCHAR(100),
        state VARCHAR(100),
        postal_code VARCHAR(10),
        country VARCHAR(100),
        primary_address BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
    "CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id)",
    """
    CREATE TABLE IF NOT EXISTS cart_items (
        id VARCHAR(100) PRIMARY KEY,
        user_id VARCHAR(100) NOT NULL,
        store_id VARCHAR(100) NOT NULL,
        store_name VARCHAR(255) NOT NULL,
        menu_id VARCHAR(100) NOT NULL,
        menu_name VARCHAR(255) NOT NULL,
        quantity INTEGER NOT NULL,
        price INTEGER NOT NULL,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
    "CREATE INDEX IF NOT EXISTS idx_cart_items_user_id ON cart_items(user_id)",
    "CREATE INDEX IF NOT EXISTS idx_cart_items_user_menu ON cart_items(user_id, store_id, menu_id)",
    """
    CREATE TABLE IF NOT EXISTS favorite_stores (
        id VARCHAR(100) PRIMARY KEY,
        user_id VARCHAR(100) NOT NULL,
        store_id VARCHAR(100) NOT NULL,
        name VARCHAR(255) NOT NULL,
        category VARCHAR(100) NOT NULL,
        rating DOUBLE PRECISION DEFAULT 0,
        delivery_time VARCHAR(100) NOT NULL,
        min_order VARCHAR(100) NOT NULL,
        image_icon VARCHAR(100) NOT NULL,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
    "CREATE INDEX IF NOT EXISTS idx_favorite_stores_user_id ON favorite_stores(user_id)",
    "CREATE INDEX IF NOT EXISTS idx_favorite_stores_user_store ON favorite_stores(user_id, store_id)",
    """
    CREATE TABLE IF NOT EXISTS refresh_tokens (
        id VARCHAR(100) PRIMARY KEY,
        user_id VARCHAR(100) NOT NULL,
        token TEXT NOT NULL,
        revoked BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        expires_at TIMESTAMPTZ NOT NULL
    )
    """,
    "CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id)",
    "CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token)",
    """
    CREATE TABLE IF NOT EXISTS mail_messages (
        id BIGINT PRIMARY KEY,
        from_user_id VARCHAR(100) NOT NULL,
        subject VARCHAR(255) NOT NULL,
        body TEXT NOT NULL,
        unread BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
    "CREATE INDEX IF NOT EXISTS idx_mail_messages_from_user_id ON mail_messages(from_user_id)",
    "CREATE INDEX IF NOT EXISTS idx_mail_messages_created_at ON mail_messages(created_at DESC)",
    """
    CREATE TABLE IF NOT EXISTS user_notifications (
        id BIGINT PRIMARY KEY,
        user_id VARCHAR(100) NOT NULL,
        body TEXT NOT NULL,
        unread BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
    "CREATE INDEX IF NOT EXISTS idx_user_notifications_user_id ON user_notifications(user_id)",
    "CREATE INDEX IF NOT EXISTS idx_user_notifications_created_at ON user_notifications(created_at DESC)",
]

STORE_SCHEMA_SQL = [
    """
    CREATE TABLE IF NOT EXISTS store (
        id VARCHAR(100) PRIMARY KEY,
        owner_id VARCHAR(100) NOT NULL,
        name VARCHAR(100) NOT NULL,
        address VARCHAR(255) NOT NULL,
        phone VARCHAR(20),
        category VARCHAR(20) NOT NULL,
        status VARCHAR(20) DEFAULT 'OPEN',
        latitude DOUBLE PRECISION,
        longitude DOUBLE PRECISION,
        min_order_amount BIGINT DEFAULT 0,
        rating_avg DOUBLE PRECISION DEFAULT 0.0,
        description TEXT,
        opening_hours VARCHAR(255),
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
    "CREATE INDEX IF NOT EXISTS idx_store_owner ON store(owner_id)",
    """
    CREATE TABLE IF NOT EXISTS menu (
        id VARCHAR(100) PRIMARY KEY,
        store_id VARCHAR(100) NOT NULL,
        name VARCHAR(100) NOT NULL,
        description TEXT,
        price BIGINT NOT NULL,
        available BOOLEAN DEFAULT TRUE,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
]

ORDER_SCHEMA_SQL = [
    """
    CREATE TABLE IF NOT EXISTS orders (
        id VARCHAR(100) PRIMARY KEY,
        user_id VARCHAR(100),
        store_id VARCHAR(100) NOT NULL,
        total_amount INTEGER NOT NULL,
        status VARCHAR(20) NOT NULL,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS order_item (
        id VARCHAR(100) PRIMARY KEY,
        order_id VARCHAR(100) NOT NULL,
        menu_id VARCHAR(100) NOT NULL,
        quantity INTEGER NOT NULL,
        price_snapshot BIGINT NOT NULL
    )
    """,
]

DELIVERY_SCHEMA_SQL = [
    """
    CREATE TABLE IF NOT EXISTS delivery (
        id VARCHAR(100) PRIMARY KEY,
        order_id VARCHAR(100) NOT NULL UNIQUE,
        courier VARCHAR(100),
        status VARCHAR(20) NOT NULL,
        delivery_fee INTEGER NOT NULL DEFAULT 0,
        scheduled_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
]

EVENT_SCHEMA_SQL = [
    """
    CREATE TABLE IF NOT EXISTS events (
        id VARCHAR(100) PRIMARY KEY,
        type VARCHAR(50),
        payload TEXT,
        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    )
    """,
]

SCHEMA_BY_DB = {
    "db_user": USER_SCHEMA_SQL,
    "db_store": STORE_SCHEMA_SQL,
    "db_order": ORDER_SCHEMA_SQL,
    "db_delivery": DELIVERY_SCHEMA_SQL,
    "db_event": EVENT_SCHEMA_SQL,
}

USER_ROWS = [
    {
        "id": "admin-main",
        "email": "admin@foodplatform.local",
        "name": "플랫폼 운영 관리자",
        "phone": "010-1000-0001",
        "roles": "ADMIN",
        "provider": "local",
        "username": "admin_ops",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-team-1",
        "marketing_status": "internal",
        "location": "서울특별시 중구",
        "team_role": "owner",
        "created_at": ago(days=20),
    },
    {
        "id": "admin-cs",
        "email": "support@foodplatform.local",
        "name": "고객센터 매니저",
        "phone": "010-1000-0002",
        "roles": "ADMIN",
        "provider": "local",
        "username": "cs_manager",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-team-2",
        "marketing_status": "internal",
        "location": "서울특별시 영등포구",
        "team_role": "member",
        "created_at": ago(days=18),
    },
    {
        "id": "admin-finance",
        "email": "finance@foodplatform.local",
        "name": "정산 담당자",
        "phone": "010-1000-0003",
        "roles": "ADMIN",
        "provider": "local",
        "username": "finance_admin",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-team-3",
        "marketing_status": "internal",
        "location": "서울특별시 강서구",
        "team_role": "member",
        "created_at": ago(days=17),
    },
    {
        "id": "admin-store-ops",
        "email": "storeops@foodplatform.local",
        "name": "매장 운영 담당",
        "phone": "010-1000-0004",
        "roles": "ADMIN",
        "provider": "local",
        "username": "store_ops",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-team-4",
        "marketing_status": "internal",
        "location": "경기도 성남시",
        "team_role": "member",
        "created_at": ago(days=17),
    },
    {
        "id": "admin-audit",
        "email": "audit@foodplatform.local",
        "name": "보안 감사 관리자",
        "phone": "010-1000-0005",
        "roles": "ADMIN",
        "provider": "local",
        "username": "audit_owner",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-team-5",
        "marketing_status": "internal",
        "location": "서울특별시 서초구",
        "team_role": "owner",
        "created_at": ago(days=16),
    },
    {
        "id": "store-owner-1",
        "email": "owner1@foodplatform.local",
        "name": "김도윤",
        "phone": "010-2000-0001",
        "roles": "STORE",
        "provider": "local",
        "username": "gangnam_owner",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-store-1",
        "marketing_status": "internal",
        "location": "서울특별시 강남구",
        "team_role": "member",
        "created_at": ago(days=17),
    },
    {
        "id": "store-owner-2",
        "email": "owner2@foodplatform.local",
        "name": "이수아",
        "phone": "010-2000-0002",
        "roles": "STORE",
        "provider": "local",
        "username": "songpa_owner",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-store-2",
        "marketing_status": "internal",
        "location": "서울특별시 송파구",
        "team_role": "member",
        "created_at": ago(days=17),
    },
    {
        "id": "store-owner-3",
        "email": "owner3@foodplatform.local",
        "name": "박현우",
        "phone": "010-2000-0003",
        "roles": "STORE",
        "provider": "local",
        "username": "yeonsu_owner",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-store-3",
        "marketing_status": "internal",
        "location": "인천광역시 연수구",
        "team_role": "member",
        "created_at": ago(days=16),
    },
    {
        "id": "user-minsu",
        "email": "minsu.kim@fdms.local",
        "name": "김민수",
        "phone": "010-3000-0001",
        "roles": "USER",
        "provider": "local",
        "username": "minsu_kim",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-user-1",
        "marketing_status": "subscribed",
        "location": "서울특별시 강남구",
        "team_role": None,
        "created_at": ago(days=15),
    },
    {
        "id": "user-seoyeon",
        "email": "seoyeon.lee@fdms.local",
        "name": "이서연",
        "phone": "010-3000-0002",
        "roles": "USER",
        "provider": "local",
        "username": "seoyeon_lee",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-user-2",
        "marketing_status": "subscribed",
        "location": "서울특별시 송파구",
        "team_role": None,
        "created_at": ago(days=14),
    },
    {
        "id": "user-jihoon",
        "email": "jihoon.park@fdms.local",
        "name": "박지훈",
        "phone": "010-3000-0003",
        "roles": "USER",
        "provider": "local",
        "username": "jihoon_park",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-user-3",
        "marketing_status": "unsubscribed",
        "location": "경기도 성남시",
        "team_role": None,
        "created_at": ago(days=13),
    },
    {
        "id": "user-yujin",
        "email": "yujin.choi@fdms.local",
        "name": "최유진",
        "phone": "010-3000-0004",
        "roles": "USER",
        "provider": "local",
        "username": "yujin_choi",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-user-4",
        "marketing_status": "subscribed",
        "location": "인천광역시 연수구",
        "team_role": None,
        "created_at": ago(days=12),
    },
    {
        "id": "user-haneul",
        "email": "haneul.jung@fdms.local",
        "name": "정하늘",
        "phone": "010-3000-0005",
        "roles": "USER",
        "provider": "local",
        "username": "haneul_jung",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-user-5",
        "marketing_status": "bounced",
        "location": "대전광역시 유성구",
        "team_role": None,
        "created_at": ago(days=11),
    },
    {
        "id": "user-subin",
        "email": "subin.kang@fdms.local",
        "name": "강수빈",
        "phone": "010-3000-0006",
        "roles": "USER",
        "provider": "local",
        "username": "subin_kang",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-user-6",
        "marketing_status": "subscribed",
        "location": "부산광역시 해운대구",
        "team_role": None,
        "created_at": ago(days=10),
    },
    {
        "id": "user-dohyun",
        "email": "dohyun.yoon@fdms.local",
        "name": "윤도현",
        "phone": "010-3000-0007",
        "roles": "USER",
        "provider": "local",
        "username": "dohyun_yoon",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-user-7",
        "marketing_status": "subscribed",
        "location": "광주광역시 서구",
        "team_role": None,
        "created_at": ago(days=9),
    },
    {
        "id": "user-sohee",
        "email": "sohee.han@fdms.local",
        "name": "한소희",
        "phone": "010-3000-0008",
        "roles": "USER",
        "provider": "local",
        "username": "sohee_han",
        "avatar_url": "https://i.pravatar.cc/128?u=fdms-user-8",
        "marketing_status": "subscribed",
        "location": "제주특별자치도 제주시",
        "team_role": None,
        "created_at": ago(days=8),
    },
]

ADDRESS_ROWS = [
    ("addr-001", "user-minsu", "집", "강남대로 123", "101동 1201호", "서울특별시", "강남구", "06236", "대한민국", True, ago(days=10)),
    ("addr-002", "user-minsu", "회사", "테헤란로 51", "8층", "서울특별시", "강남구", "06142", "대한민국", False, ago(days=9)),
    ("addr-003", "user-seoyeon", "집", "올림픽로 300", "2202호", "서울특별시", "송파구", "05551", "대한민국", True, ago(days=8)),
    ("addr-004", "user-jihoon", "집", "판교역로 166", None, "경기도", "성남시", "13494", "대한민국", True, ago(days=7)),
    ("addr-005", "user-yujin", "집", "컨벤시아대로 165", "1205호", "인천광역시", "연수구", "22004", "대한민국", True, ago(days=6)),
    ("addr-006", "user-subin", "집", "센텀중앙로 90", "1803호", "부산광역시", "해운대구", "48058", "대한민국", True, ago(days=5)),
]

FAVORITE_STORE_ROWS = [
    ("fav-001", "user-minsu", "store-seoul-1", "강남 한식당", "KOREAN", 4.7, "32분", "15000원", "i-lucide-store", ago(days=4)),
    ("fav-002", "user-minsu", "store-seoul-2", "송파 치킨클럽", "CHICKEN", 4.8, "28분", "18000원", "i-lucide-store", ago(days=3)),
    ("fav-003", "user-seoyeon", "store-incheon-1", "연수 브런치랩", "CAFE", 4.6, "25분", "12000원", "i-lucide-store", ago(days=3)),
    ("fav-004", "user-yujin", "store-seoul-1", "강남 한식당", "KOREAN", 4.7, "32분", "15000원", "i-lucide-store", ago(days=2)),
]

CART_ITEM_ROWS = [
    ("cart-001", "user-minsu", "store-seoul-1", "강남 한식당", "menu-001", "직화 제육덮밥", 1, 9500, ago(hours=2)),
    ("cart-002", "user-minsu", "store-seoul-1", "강남 한식당", "menu-002", "소불고기 정식", 2, 13500, ago(minutes=110)),
    ("cart-003", "user-seoyeon", "store-seoul-2", "송파 치킨클럽", "menu-004", "후라이드 치킨", 1, 21000, ago(minutes=90)),
    ("cart-004", "user-subin", "store-incheon-1", "연수 브런치랩", "menu-008", "에그 베이글 샌드위치", 1, 8900, ago(minutes=70)),
]

MAIL_ROWS = [
    (1, "user-seoyeon", "주문 지연 문의 대응 요청", "강남권 주문 지연 관련 문의가 증가하고 있습니다. 점심 피크 시간 주문 처리 현황을 확인해 주세요.", True, ago(hours=1, minutes=12)),
    (2, "user-yujin", "쿠폰 정책 검토 회의", "신규 가입 쿠폰과 재방문 쿠폰 정책을 3월 주간 캠페인 일정에 맞춰 재검토해야 합니다.", False, ago(days=1, hours=2)),
    (3, "user-subin", "정산 오류 후보 건 공유", "정산 금액이 주문 총액과 차이 나는 후보 건 3건을 확인했습니다. 거래 로그 화면 연동 전 임시 검토용입니다.", True, ago(days=2, hours=3)),
    (4, "user-dohyun", "이벤트 발행 실패 로그 확인", "service-event 적재 실패 건이 있어 재시도 전 traceId 기준 필터 화면이 필요합니다.", False, ago(days=3, hours=4)),
]

NOTIFICATION_ROWS = [
    (1, "user-seoyeon", "1:1 문의를 등록했습니다", True, ago(minutes=12)),
    (2, "user-minsu", "신규 주문이 접수되었습니다", False, ago(hours=1)),
    (3, "user-subin", "환불 요청을 남겼습니다", True, ago(hours=2)),
    (4, "user-yujin", "리뷰 답글 등록을 요청했습니다", False, ago(hours=5)),
    (5, "user-sohee", "이벤트 페이지를 조회했습니다", False, ago(days=1)),
    (6, "user-jihoon", "장바구니에 메뉴를 다시 담았습니다", False, ago(days=2)),
    (7, "user-haneul", "이메일 수신 상태가 반송으로 변경되었습니다", True, ago(days=3)),
    (8, "user-dohyun", "관리자 공지를 확인했습니다", False, ago(days=4)),
]

STORE_ROWS = [
    ("store-seoul-1", "store-owner-1", "강남 한식당", "서울특별시 강남구 강남대로 100", "02-555-1001", "KOREAN", "OPEN", 37.4979, 127.0276, 15000, 4.7, "직장인 점심 수요가 많은 한식 배달 전문점", "10:00-21:00", ago(days=15)),
    ("store-seoul-2", "store-owner-2", "송파 치킨클럽", "서울특별시 송파구 오금로 120", "02-555-1002", "CHICKEN", "OPEN", 37.5145, 127.1059, 18000, 4.8, "주문량이 많은 야식 전문 치킨 매장", "11:00-23:30", ago(days=14)),
    ("store-incheon-1", "store-owner-3", "연수 브런치랩", "인천광역시 연수구 센트럴로 240", "032-555-1003", "CAFE", "OPEN", 37.3925, 126.6378, 12000, 4.6, "샌드위치와 커피를 판매하는 브런치 전문점", "08:30-20:00", ago(days=13)),
]

MENU_ROWS = [
    ("menu-001", "store-seoul-1", "직화 제육덮밥", "매콤한 제육과 반숙 계란이 올라간 덮밥", 9500, True, ago(days=12)),
    ("menu-002", "store-seoul-1", "소불고기 정식", "불고기와 밑반찬이 포함된 정식", 13500, True, ago(days=12)),
    ("menu-003", "store-seoul-1", "된장찌개 세트", "집된장 스타일 찌개와 공기밥", 9000, True, ago(days=11)),
    ("menu-004", "store-seoul-2", "후라이드 치킨", "바삭한 시그니처 후라이드 치킨", 21000, True, ago(days=12)),
    ("menu-005", "store-seoul-2", "양념 반 후라이드 반", "반반 조합 인기 메뉴", 23000, True, ago(days=11)),
    ("menu-006", "store-seoul-2", "치즈볼 세트", "치킨과 함께 주문 많은 사이드 세트", 6500, True, ago(days=10)),
    ("menu-007", "store-incheon-1", "아메리카노", "브런치랩 시그니처 원두", 4500, True, ago(days=10)),
    ("menu-008", "store-incheon-1", "에그 베이글 샌드위치", "베이글과 스크램블 에그 조합", 8900, True, ago(days=10)),
    ("menu-009", "store-incheon-1", "리코타 샐러드", "가벼운 브런치 샐러드", 9800, True, ago(days=9)),
]

ORDER_ROWS = [
    ("order-001", "user-minsu", "store-seoul-1", 23000, "CREATED", ago(hours=3)),
    ("order-002", "user-seoyeon", "store-seoul-2", 29500, "CONFIRMED", ago(hours=2, minutes=20)),
    ("order-003", "user-yujin", "store-incheon-1", 13400, "DELIVERING", ago(minutes=95)),
    ("order-004", "user-subin", "store-seoul-2", 21000, "DELIVERED", ago(days=1, hours=4)),
    ("order-005", "user-jihoon", "store-seoul-1", 18500, "CANCELLED", ago(days=2, hours=1)),
]

ORDER_ITEM_ROWS = [
    ("order-item-001", "order-001", "menu-001", 1, 9500),
    ("order-item-002", "order-001", "menu-003", 1, 9000),
    ("order-item-003", "order-002", "menu-005", 1, 23000),
    ("order-item-004", "order-002", "menu-006", 1, 6500),
    ("order-item-005", "order-003", "menu-008", 1, 8900),
    ("order-item-006", "order-003", "menu-007", 1, 4500),
    ("order-item-007", "order-004", "menu-004", 1, 21000),
    ("order-item-008", "order-005", "menu-002", 1, 13500),
]

DELIVERY_ROWS = [
    ("delivery-001", "order-002", "라이더 김현수", "ASSIGNED", 3000, ago(hours=2)),
    ("delivery-002", "order-003", "라이더 박소연", "PICKED_UP", 2500, ago(minutes=80)),
    ("delivery-003", "order-004", "라이더 정우성", "COMPLETED", 3000, ago(days=1, hours=3)),
]

EVENT_ROWS = [
    ("event-001", "ORDER_CREATED", '{"orderId":"order-001","userId":"user-minsu","storeId":"store-seoul-1"}', ago(hours=3)),
    ("event-002", "ORDER_CONFIRMED", '{"orderId":"order-002","storeId":"store-seoul-2","status":"CONFIRMED"}', ago(hours=2, minutes=10)),
    ("event-003", "DELIVERY_STARTED", '{"orderId":"order-003","deliveryId":"delivery-002"}', ago(minutes=85)),
    ("event-004", "ORDER_CANCELLED", '{"orderId":"order-005","reason":"USER_REQUEST"}', ago(days=2)),
    ("event-005", "USER_REGISTERED", '{"userId":"user-subin","email":"subin.kang@fdms.local"}', ago(days=10)),
]


def connect(config: DbConfig, *, autocommit: bool = False):
    connection = psycopg.connect(config.dsn())
    connection.autocommit = autocommit
    return connection


def ensure_databases() -> None:
    with connect(MAINTENANCE_DB, autocommit=True) as conn:
        with conn.cursor() as cur:
            cur.execute("SELECT datname FROM pg_database")
            existing = {row[0] for row in cur.fetchall()}
            for database in TARGET_DATABASES:
                if database not in existing:
                    cur.execute(f'CREATE DATABASE "{database}"')


def ensure_schema(database: str) -> None:
    with connect(DbConfig(database)) as conn:
        with conn.cursor() as cur:
            for statement in SCHEMA_BY_DB[database]:
                cur.execute(statement)
        conn.commit()


def seed_user_db() -> None:
    with connect(DbConfig("db_user")) as conn:
        with conn.cursor() as cur:
            for row in USER_ROWS:
                cur.execute(
                    """
                    INSERT INTO users (
                        id, email, password_hash, name, phone, roles, provider, provider_id,
                        username, avatar_url, marketing_status, location, team_role, created_at
                    ) VALUES (
                        %(id)s, %(email)s, %(password_hash)s, %(name)s, %(phone)s, %(roles)s, %(provider)s, %(provider_id)s,
                        %(username)s, %(avatar_url)s, %(marketing_status)s, %(location)s, %(team_role)s, %(created_at)s
                    )
                    ON CONFLICT (id) DO UPDATE SET
                        email = EXCLUDED.email,
                        password_hash = EXCLUDED.password_hash,
                        name = EXCLUDED.name,
                        phone = EXCLUDED.phone,
                        roles = EXCLUDED.roles,
                        provider = EXCLUDED.provider,
                        provider_id = EXCLUDED.provider_id,
                        username = EXCLUDED.username,
                        avatar_url = EXCLUDED.avatar_url,
                        marketing_status = EXCLUDED.marketing_status,
                        location = EXCLUDED.location,
                        team_role = EXCLUDED.team_role,
                        created_at = EXCLUDED.created_at
                    """,
                    {
                        **row,
                        "password_hash": PASSWORD_HASH,
                        "provider_id": None,
                    },
                )

            for row in ADDRESS_ROWS:
                cur.execute(
                    """
                    INSERT INTO addresses (id, user_id, label, line1, line2, city, state, postal_code, country, primary_address, created_at)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        user_id = EXCLUDED.user_id,
                        label = EXCLUDED.label,
                        line1 = EXCLUDED.line1,
                        line2 = EXCLUDED.line2,
                        city = EXCLUDED.city,
                        state = EXCLUDED.state,
                        postal_code = EXCLUDED.postal_code,
                        country = EXCLUDED.country,
                        primary_address = EXCLUDED.primary_address,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )

            for row in FAVORITE_STORE_ROWS:
                cur.execute(
                    """
                    INSERT INTO favorite_stores (id, user_id, store_id, name, category, rating, delivery_time, min_order, image_icon, created_at)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        user_id = EXCLUDED.user_id,
                        store_id = EXCLUDED.store_id,
                        name = EXCLUDED.name,
                        category = EXCLUDED.category,
                        rating = EXCLUDED.rating,
                        delivery_time = EXCLUDED.delivery_time,
                        min_order = EXCLUDED.min_order,
                        image_icon = EXCLUDED.image_icon,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )

            for row in CART_ITEM_ROWS:
                cur.execute(
                    """
                    INSERT INTO cart_items (id, user_id, store_id, store_name, menu_id, menu_name, quantity, price, created_at)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        user_id = EXCLUDED.user_id,
                        store_id = EXCLUDED.store_id,
                        store_name = EXCLUDED.store_name,
                        menu_id = EXCLUDED.menu_id,
                        menu_name = EXCLUDED.menu_name,
                        quantity = EXCLUDED.quantity,
                        price = EXCLUDED.price,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )

            for row in MAIL_ROWS:
                cur.execute(
                    """
                    INSERT INTO mail_messages (id, from_user_id, subject, body, unread, created_at)
                    VALUES (%s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        from_user_id = EXCLUDED.from_user_id,
                        subject = EXCLUDED.subject,
                        body = EXCLUDED.body,
                        unread = EXCLUDED.unread,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )

            for row in NOTIFICATION_ROWS:
                cur.execute(
                    """
                    INSERT INTO user_notifications (id, user_id, body, unread, created_at)
                    VALUES (%s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        user_id = EXCLUDED.user_id,
                        body = EXCLUDED.body,
                        unread = EXCLUDED.unread,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )

        conn.commit()


def seed_store_db() -> None:
    with connect(DbConfig("db_store")) as conn:
        with conn.cursor() as cur:
            for row in STORE_ROWS:
                cur.execute(
                    """
                    INSERT INTO store (
                        id, owner_id, name, address, phone, category, status,
                        latitude, longitude, min_order_amount, rating_avg, description, opening_hours, created_at
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        owner_id = EXCLUDED.owner_id,
                        name = EXCLUDED.name,
                        address = EXCLUDED.address,
                        phone = EXCLUDED.phone,
                        category = EXCLUDED.category,
                        status = EXCLUDED.status,
                        latitude = EXCLUDED.latitude,
                        longitude = EXCLUDED.longitude,
                        min_order_amount = EXCLUDED.min_order_amount,
                        rating_avg = EXCLUDED.rating_avg,
                        description = EXCLUDED.description,
                        opening_hours = EXCLUDED.opening_hours,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )

            for row in MENU_ROWS:
                cur.execute(
                    """
                    INSERT INTO menu (id, store_id, name, description, price, available, created_at)
                    VALUES (%s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        store_id = EXCLUDED.store_id,
                        name = EXCLUDED.name,
                        description = EXCLUDED.description,
                        price = EXCLUDED.price,
                        available = EXCLUDED.available,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )
        conn.commit()


def seed_order_db() -> None:
    with connect(DbConfig("db_order")) as conn:
        with conn.cursor() as cur:
            for row in ORDER_ROWS:
                cur.execute(
                    """
                    INSERT INTO orders (id, user_id, store_id, total_amount, status, created_at)
                    VALUES (%s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        user_id = EXCLUDED.user_id,
                        store_id = EXCLUDED.store_id,
                        total_amount = EXCLUDED.total_amount,
                        status = EXCLUDED.status,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )

            for row in ORDER_ITEM_ROWS:
                cur.execute(
                    """
                    INSERT INTO order_item (id, order_id, menu_id, quantity, price_snapshot)
                    VALUES (%s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        order_id = EXCLUDED.order_id,
                        menu_id = EXCLUDED.menu_id,
                        quantity = EXCLUDED.quantity,
                        price_snapshot = EXCLUDED.price_snapshot
                    """,
                    row,
                )
        conn.commit()


def seed_delivery_db() -> None:
    with connect(DbConfig("db_delivery")) as conn:
        with conn.cursor() as cur:
            for row in DELIVERY_ROWS:
                cur.execute(
                    """
                    INSERT INTO delivery (id, order_id, courier, status, delivery_fee, scheduled_at)
                    VALUES (%s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        order_id = EXCLUDED.order_id,
                        courier = EXCLUDED.courier,
                        status = EXCLUDED.status,
                        delivery_fee = EXCLUDED.delivery_fee,
                        scheduled_at = EXCLUDED.scheduled_at
                    """,
                    row,
                )
        conn.commit()


def seed_event_db() -> None:
    with connect(DbConfig("db_event")) as conn:
        with conn.cursor() as cur:
            for row in EVENT_ROWS:
                cur.execute(
                    """
                    INSERT INTO events (id, type, payload, created_at)
                    VALUES (%s, %s, %s, %s)
                    ON CONFLICT (id) DO UPDATE SET
                        type = EXCLUDED.type,
                        payload = EXCLUDED.payload,
                        created_at = EXCLUDED.created_at
                    """,
                    row,
                )
        conn.commit()


def print_counts() -> None:
    checks = {
        "db_user": ["users", "addresses", "favorite_stores", "cart_items", "mail_messages", "user_notifications"],
        "db_store": ["store", "menu"],
        "db_order": ["orders", "order_item"],
        "db_delivery": ["delivery"],
        "db_event": ["events"],
    }
    for database, tables in checks.items():
        with connect(DbConfig(database), autocommit=True) as conn:
            with conn.cursor() as cur:
                print(f"[{database}]")
                for table in tables:
                    cur.execute(f'SELECT COUNT(*) FROM "{table}"')
                    print(f"  {table}: {cur.fetchone()[0]}")


def main() -> None:
    ensure_databases()
    for database in TARGET_DATABASES:
        ensure_schema(database)
    seed_user_db()
    seed_store_db()
    seed_order_db()
    seed_delivery_db()
    seed_event_db()
    print_counts()


if __name__ == "__main__":
    main()