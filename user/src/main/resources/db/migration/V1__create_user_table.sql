CREATE TABLE "app_user" (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL CONSTRAINT uk_user_email UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);
