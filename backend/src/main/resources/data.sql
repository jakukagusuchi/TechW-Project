-- ============================================================
-- TechW E-Commerce - User Seed Data (PostgreSQL)
-- ============================================================

-- Admin Account
-- Email: admin@techw.com | Password: admin123
INSERT INTO users (id, email, password, first_name, last_name, role, created_at, updated_at)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'admin@techw.com',
    '$2a$10$5JFLIHXUrH9Jxxlp.vXNO./QiH0OdQ6DUz8cR1psRmxYT2S9.nNzq',
    'Admin',
    'TechW',
    'ADMIN', -- ADMIN role
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- Regular User Account
-- Email: user@techw.com | Password: user123
INSERT INTO users (id, email, password, first_name, last_name, role, created_at, updated_at)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'user@techw.com',
    '$2a$10$Gk6XG7uTkOZXmfmWuj0aveoTlN83kgrUBTmoEMdwYmX9.K7jonn0m',
    'Regular',
    'User',
    'USER', -- USER role
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT DO NOTHING;

-- Carts
INSERT INTO carts (user_id) VALUES ('11111111-1111-1111-1111-111111111111') ON CONFLICT DO NOTHING;
INSERT INTO carts (user_id) VALUES ('22222222-2222-2222-2222-222222222222') ON CONFLICT DO NOTHING;

-- Categories
INSERT INTO categories (id, name, description) VALUES
(1, 'Electronics', 'Electronic devices and accessories'),
(2, 'Computers', 'Laptops, desktops, and components'),
(3, 'Smartphones', 'Mobile phones and accessories')
ON CONFLICT DO NOTHING;

-- Brands
INSERT INTO brands (id, name, logo_url) VALUES
(1, 'Apple', 'https://example.com/apple.png'),
(2, 'Samsung', 'https://example.com/samsung.png'),
(3, 'Sony', 'https://example.com/sony.png')
ON CONFLICT DO NOTHING;

-- Products
INSERT INTO products (id, name, description, price, stock_quantity, category_id, brand_id, image_url, is_active, created_at, updated_at) VALUES
(1, 'iPhone 15 Pro', 'Latest Apple smartphone with titanium body', 999.99, 50, 3, 1, 'https://example.com/iphone15pro.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Galaxy S24 Ultra', 'Samsung flagship with AI features', 1199.99, 40, 3, 2, 'https://example.com/s24ultra.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'MacBook Pro 16', 'M3 Max chip, 32GB RAM, 1TB SSD', 3499.00, 20, 2, 1, 'https://example.com/macbookpro.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Sony WH-1000XM5', 'Noise cancelling wireless headphones', 398.00, 100, 1, 3, 'https://example.com/sonyheadphones.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Orders
INSERT INTO "orders" (id, user_id, total_amount, status, shipping_address, billing_address, created_at) VALUES
('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 1397.99, 'PENDING', '123 Main St, Tech City', '123 Main St, Tech City', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Order Items
INSERT INTO order_items (id, order_id, product_id, product_name, unit_price, quantity) VALUES
(1, '33333333-3333-3333-3333-333333333333', 1, 'iPhone 15 Pro', 999.99, 1),
(2, '33333333-3333-3333-3333-333333333333', 4, 'Sony WH-1000XM5', 398.00, 1)
ON CONFLICT DO NOTHING;
