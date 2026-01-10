CREATE TABLE IF NOT EXISTS orders (
                                      id VARCHAR(100) PRIMARY KEY,
    user_id VARCHAR(100), -- 비회원 NULL 가능
    store_id VARCHAR(100) NOT NULL,
    store_table_id VARCHAR(100), -- 배달 시 NULL

    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',

    total_amount INTEGER NOT NULL,
    delivery_address VARCHAR(255),
    requests VARCHAR(255),

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
    );

CREATE TRIGGER update_orders_modtime
    BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE PROCEDURE update_timestamp_column();

CREATE TABLE IF NOT EXISTS order_item (
                                          id VARCHAR(100) PRIMARY KEY,
    order_id VARCHAR(100) NOT NULL,
    menu_id VARCHAR(100) NOT NULL, -- 통계용 원본 ID

    menu_name_snapshot VARCHAR(100) NOT NULL,
    price_snapshot INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS order_item_option (
                                                 id VARCHAR(100) PRIMARY KEY,
    order_item_id VARCHAR(100) NOT NULL,

    option_name_snapshot VARCHAR(50) NOT NULL,
    extra_price_snapshot INTEGER DEFAULT 0,

    CONSTRAINT fk_item_option_item FOREIGN KEY (order_item_id) REFERENCES order_item(id) ON DELETE CASCADE
    );