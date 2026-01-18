INSERT INTO membership_plans (name, description, duration, price, active, version, created_at, updated_at) VALUES
('Monthly Basic', 'Basic membership with monthly billing', 'MONTHLY', 9.99, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Quarterly Premium', 'Premium membership with quarterly billing and savings', 'QUARTERLY', 24.99, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Yearly Elite', 'Elite membership with yearly billing and maximum savings', 'YEARLY', 89.99, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO membership_tiers (name, description, tier_level, min_order_count, min_order_value, required_cohort, active, version, created_at, updated_at) VALUES
('Silver', 'Entry level membership tier', 1, 0, 0.00, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Gold', 'Mid-level membership tier with enhanced benefits', 2, 5, 500.00, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Platinum', 'Premium membership tier with exclusive benefits', 3, 15, 2000.00, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Diamond', 'Elite membership tier for VIP cohort members', 4, 10, 1500.00, 'VIP', true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tier_benefits (tier_id, benefit_type, description, discount_percentage, applicable_categories, active, version, created_at, updated_at) VALUES
(1, 'FREE_DELIVERY', 'Free delivery on orders above $50', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'DISCOUNT', '5% discount on electronics', 5.00, 'Electronics', true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'FREE_DELIVERY', 'Free delivery on all orders', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'DISCOUNT', '10% discount on electronics and fashion', 10.00, 'Electronics,Fashion', true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'EXCLUSIVE_DEALS', 'Access to exclusive member-only deals', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'FREE_DELIVERY', 'Free express delivery on all orders', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'DISCOUNT', '15% discount on all categories', 15.00, 'All', true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'EXCLUSIVE_DEALS', 'Access to exclusive member-only deals', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'EARLY_ACCESS', 'Early access to sales and new products', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'PRIORITY_SUPPORT', '24/7 priority customer support', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'FREE_DELIVERY', 'Free same-day delivery on all orders', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'DISCOUNT', '20% discount on all categories', 20.00, 'All', true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'EXCLUSIVE_DEALS', 'Access to VIP exclusive deals', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'EARLY_ACCESS', 'VIP early access to sales and new products', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'PRIORITY_SUPPORT', 'Dedicated VIP support team', NULL, NULL, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
