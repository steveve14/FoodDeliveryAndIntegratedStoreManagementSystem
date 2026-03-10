-- Migration: Align db_user schema with Spring Data JDBC entity model
-- Date: 2026-03-02
-- Reason: Entity field names did not match DB column names, AND Spring Data JDBC
--         generates quoted uppercase identifiers ("PASSWORD_HASH") which are
--         case-sensitive in PostgreSQL. @Column annotations now use explicit
--         lowercase names, so the DB columns must match exactly.

-- ============================================================
-- 1. users table: rename columns to match User.java entity
-- ============================================================

-- password → password_hash  (entity field: passwordHash)
ALTER TABLE db_user.users RENAME COLUMN password TO password_hash;

-- nickname → name  (entity field: name)
ALTER TABLE db_user.users RENAME COLUMN nickname TO name;

-- phone_number → phone  (entity field: phone)
ALTER TABLE db_user.users RENAME COLUMN phone_number TO phone;
ALTER TABLE db_user.users ALTER COLUMN phone DROP NOT NULL;

-- role → roles  (entity field: roles)
ALTER TABLE db_user.users RENAME COLUMN role TO roles;

-- social_type → provider  (entity field: provider)
ALTER TABLE db_user.users RENAME COLUMN social_type TO provider;

-- Add provider_id column (entity field: providerId, was missing)
ALTER TABLE db_user.users ADD COLUMN provider_id VARCHAR(255);

-- Drop columns not in entity (optional, can keep for future use)
-- ALTER TABLE db_user.users DROP COLUMN IF EXISTS status;
-- ALTER TABLE db_user.users DROP COLUMN IF EXISTS updated_at;

-- Update index to use new column name
DROP INDEX IF EXISTS db_user.idx_users_role;
CREATE INDEX idx_users_roles ON db_user.users(roles);

-- ============================================================
-- 2. address → addresses: rename table and columns for Address.java
-- ============================================================

ALTER TABLE db_user.address RENAME TO addresses;

-- address_name → label
ALTER TABLE db_user.addresses RENAME COLUMN address_name TO label;

-- road_address → line1
ALTER TABLE db_user.addresses RENAME COLUMN road_address TO line1;

-- detail_address → line2
ALTER TABLE db_user.addresses RENAME COLUMN detail_address TO line2;
ALTER TABLE db_user.addresses ALTER COLUMN line2 DROP NOT NULL;

-- zip_code → postal_code
ALTER TABLE db_user.addresses RENAME COLUMN zip_code TO postal_code;

-- is_main → primary_address
ALTER TABLE db_user.addresses RENAME COLUMN is_main TO primary_address;

-- Add new columns from entity
ALTER TABLE db_user.addresses ADD COLUMN city VARCHAR(100);
ALTER TABLE db_user.addresses ADD COLUMN state VARCHAR(100);
ALTER TABLE db_user.addresses ADD COLUMN country VARCHAR(100);
ALTER TABLE db_user.addresses ADD COLUMN created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;

-- Drop columns not in entity (latitude/longitude removed from entity)
-- ALTER TABLE db_user.addresses DROP COLUMN IF EXISTS latitude;
-- ALTER TABLE db_user.addresses DROP COLUMN IF EXISTS longitude;
