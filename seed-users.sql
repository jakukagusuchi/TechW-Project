-- ============================================================
-- TechW E-Commerce - User Seed Data
-- Run this in SSMS 21 against the TechW database AFTER dropping
-- and recreating it (Hibernate will auto-create the tables).
--
-- Passwords are BCrypt-hashed:
--   admin account  -> password: admin123
--   user account   -> password: user123
-- ============================================================

USE TechW;
GO

-- Admin Account
-- Email: admin@techw.com | Password: admin123
INSERT INTO users (id, email, password, first_name, last_name, role, created_at, updated_at)
VALUES (
    NEWID(),
    'admin@techw.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWq',  -- admin123
    'Admin',
    'TechW',
    'ADMIN',
    GETDATE(),
    GETDATE()
);

-- Regular User Account
-- Email: user@techw.com | Password: user123
INSERT INTO users (id, email, password, first_name, last_name, role, created_at, updated_at)
VALUES (
    NEWID(),
    'user@techw.com',
    '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNBSoL6MVAoTmCRt.e2',   -- user123
    'Regular',
    'User',
    'USER',
    GETDATE(),
    GETDATE()
);

-- Create carts for both users
INSERT INTO carts (user_id)
SELECT id FROM users WHERE email = 'admin@techw.com';

INSERT INTO carts (user_id)
SELECT id FROM users WHERE email = 'user@techw.com';

GO

PRINT 'Seed data imported successfully!';
PRINT '';
PRINT 'Accounts created:';
PRINT '  admin@techw.com  / admin123  (ADMIN role)';
PRINT '  user@techw.com   / user123   (USER role)';
