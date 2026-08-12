CREATE TABLE IF NOT EXISTS carts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    product_ids UUID ARRAY,
    stat VARCHAR(32) NOT NULL DEFAULT 'saved',
    amount NUMERIC(38, 0) NOT NULL DEFAULT 0,
    CONSTRAINT carts_stat_check
        CHECK (stat IN ('paid', 'saved', 'sent', 'in_transition'))
);
