CREATE TABLE product_image_product (
    product_image_id UUID NOT NULL,
    product_id UUID NOT NULL,
    CONSTRAINT pk_product_image_product PRIMARY KEY (product_image_id, product_id),
    CONSTRAINT fk_product_image_product_image
        FOREIGN KEY (product_image_id) REFERENCES product_image(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_image_product_product
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

INSERT INTO product_image_product (product_image_id, product_id)
SELECT id, product_id
FROM product_image;

ALTER TABLE product_image
    DROP CONSTRAINT fk_product_image_product;

DROP INDEX idx_product_image_product;

ALTER TABLE product_image
    DROP COLUMN product_id;

CREATE INDEX idx_product_image_product_product
    ON product_image_product(product_id);
