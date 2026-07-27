-- Create products table

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    description TEXT,
    price NUMERIC(19, 2),
    quantity INTEGER,
    tags TEXT[],
    active BOOLEAN,
    category_id UUID NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE
);

CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_active ON products(active);
