-- Create product_image table

CREATE TABLE product_image (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bucket_name VARCHAR(255) NOT NULL,
    object_path VARCHAR(1000) NOT NULL,
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    public_url TEXT NOT NULL,
    alt_text TEXT,
    main_image BOOLEAN,
    product_id UUID NOT NULL,
    CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_image_product ON product_image(product_id);
CREATE INDEX idx_product_image_main ON product_image(main_image);
