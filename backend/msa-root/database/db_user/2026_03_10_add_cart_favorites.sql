CREATE TABLE IF NOT EXISTS db_user.cart_items (
    id         VARCHAR(100) PRIMARY KEY,
    user_id    VARCHAR(100) NOT NULL,
    store_id   VARCHAR(100) NOT NULL,
    store_name VARCHAR(255) NOT NULL,
    menu_id    VARCHAR(100) NOT NULL,
    menu_name  VARCHAR(255) NOT NULL,
    quantity   INTEGER      NOT NULL,
    price      INTEGER      NOT NULL,
    created_at TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES db_user.users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cart_items_user_id ON db_user.cart_items(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_user_menu ON db_user.cart_items(user_id, store_id, menu_id);

CREATE TABLE IF NOT EXISTS db_user.favorite_stores (
    id            VARCHAR(100) PRIMARY KEY,
    user_id       VARCHAR(100) NOT NULL,
    store_id      VARCHAR(100) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    category      VARCHAR(100) NOT NULL,
    rating        DOUBLE PRECISION DEFAULT 0,
    delivery_time VARCHAR(100) NOT NULL,
    min_order     VARCHAR(100) NOT NULL,
    image_icon    VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES db_user.users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_favorite_stores_user_id ON db_user.favorite_stores(user_id);
CREATE INDEX IF NOT EXISTS idx_favorite_stores_user_store ON db_user.favorite_stores(user_id, store_id);