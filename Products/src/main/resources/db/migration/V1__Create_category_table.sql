-- Create category table

CREATE TABLE category (
    category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT false
);

CREATE INDEX idx_category_name ON category(name);
CREATE INDEX idx_category_active ON category(active);
