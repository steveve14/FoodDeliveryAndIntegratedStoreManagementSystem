CREATE TABLE IF NOT EXISTS store (
                                     id VARCHAR(100) PRIMARY KEY,
    owner_id VARCHAR(100) NOT NULL, -- User Service ID (Logical Ref)
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    min_order_amount INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'CLOSED',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
    );

CREATE TRIGGER update_store_modtime
    BEFORE UPDATE ON store
    FOR EACH ROW EXECUTE PROCEDURE update_timestamp_column();

CREATE TABLE IF NOT EXISTS store_table (
                                           id VARCHAR(100) PRIMARY KEY,
    store_id VARCHAR(100) NOT NULL,
    table_number VARCHAR(20) NOT NULL,
    x_position INTEGER DEFAULT 0,
    y_position INTEGER DEFAULT 0,

    CONSTRAINT fk_table_store FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS menu_category (
                                             id VARCHAR(100) PRIMARY KEY,
    store_id VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    display_order INTEGER DEFAULT 0,

    CONSTRAINT fk_category_store FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS menu (
                                    id VARCHAR(100) PRIMARY KEY,
    category_id VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    price INTEGER NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    is_soldout BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES menu_category(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS option_group (
                                            id VARCHAR(100) PRIMARY KEY,
    menu_id VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    is_required BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_option_group_menu FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS options (
                                       id VARCHAR(100) PRIMARY KEY,
    group_id VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    extra_price INTEGER DEFAULT 0,

    CONSTRAINT fk_option_group FOREIGN KEY (group_id) REFERENCES option_group(id) ON DELETE CASCADE
    );