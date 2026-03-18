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
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES ('88888888-8888-8888-8888-888888880001', '11111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES ('88888888-8888-8888-8888-888888880002', '22222222-2222-2222-2222-222222222222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

-- Categories
INSERT INTO categories (id, name, description, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Electronics', 'Electronic devices and accessories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'Computers', 'Laptops, desktops, and components', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440003', 'Smartphones', 'Mobile phones and accessories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Brands
INSERT INTO brands (id, name, logo_url, created_at, updated_at) VALUES
('660e8400-e29b-41d4-a716-446655440001', 'Apple', 'https://example.com/apple.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440002', 'Samsung', 'https://example.com/samsung.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440003', 'Sony', 'https://example.com/sony.png', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Products
INSERT INTO products (id, name, description, price, stock_quantity, category_id, brand_id, image_url, is_active, created_at, updated_at) VALUES
('770e8400-e29b-41d4-a716-446655440001', 'iPhone 15 Pro', 'Latest Apple smartphone with titanium body', 999.99, 50, '550e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440001', 'https://example.com/iphone15pro.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('770e8400-e29b-41d4-a716-446655440002', 'Galaxy S24 Ultra', 'Samsung flagship with AI features', 1199.99, 40, '550e8400-e29b-41d4-a716-446655440003', '660e8400-e29b-41d4-a716-446655440002', 'https://example.com/s24ultra.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('770e8400-e29b-41d4-a716-446655440003', 'MacBook Pro 16', 'M3 Max chip, 32GB RAM, 1TB SSD', 3499.00, 20, '550e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440001', 'https://example.com/macbookpro.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('770e8400-e29b-41d4-a716-446655440004', 'Sony WH-1000XM5', 'Noise cancelling wireless headphones', 398.00, 100, '550e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440003', 'https://example.com/sonyheadphones.png', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Orders
INSERT INTO "orders" (id, user_id, total_amount, status, shipping_address, billing_address, created_at, updated_at) VALUES
('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 1397.99, 'PENDING', '123 Main St, Tech City', '123 Main St, Tech City', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Order Items
INSERT INTO order_items (id, order_id, product_id, product_name, unit_price, quantity, created_at, updated_at) VALUES
('44444444-4444-4444-4444-444444440001', '33333333-3333-3333-3333-333333333333', '770e8400-e29b-41d4-a716-446655440001', 'iPhone 15 Pro', 999.99, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444440002', '33333333-3333-3333-3333-333333333333', '770e8400-e29b-41d4-a716-446655440004', 'Sony WH-1000XM5', 398.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
