-- Day30 - MySQL 8 local development environment only
-- 1. SELECT database; inspect existing columns before any ALTER.
USE campus_task;
SHOW COLUMNS FROM users LIKE 'role';
-- ONLY IF role DOES NOT ALREADY EXIST, execute next statement:
-- ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';
-- 2. After column exists, verify old accounts are USER:
SELECT id, username, role FROM users ORDER BY id;
-- 3. Register day30_admin_demo normally with POST /users first.
--    ONLY in your local practice DB, manually grant that trusted demo account ADMIN:
-- UPDATE users SET role = 'ADMIN' WHERE username = 'day30_admin_demo' AND role = 'USER';
-- SELECT ROW_COUNT() AS changed_rows;
-- SELECT id, username, role FROM users WHERE username = 'day30_admin_demo';
-- Do not execute lines beginning with -- unless you have reviewed them.
