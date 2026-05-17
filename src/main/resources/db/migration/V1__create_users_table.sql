CREATE TABLE Users
(
    id          UUID PRIMARY KEY,
    external_id VARCHAR(255) UNIQUE NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    name        VARCHAR(100),
    last_name   VARCHAR(100),
    role        VARCHAR(50),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP           NOT NULL
);