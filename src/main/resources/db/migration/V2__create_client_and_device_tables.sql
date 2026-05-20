CREATE TABLE clients
(
    id           UUID PRIMARY KEY,
    name         VARCHAR(250) NOT NULL,
    phone_number VARCHAR(15)  NOT NULL UNIQUE,
    email        VARCHAR(255),
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL
);

CREATE TABLE devices
(
    id            UUID PRIMARY KEY,
    client_id     UUID         NOT NULL,
    brand         VARCHAR(100) NOT NULL,
    model         VARCHAR(100) NOT NULL,
    serial_number VARCHAR(150) NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    CONSTRAINT fk_devices_client FOREIGN KEY (client_id) REFERENCES clients (id) ON DELETE CASCADE
);


CREATE UNIQUE INDEX uq_client_email
    ON clients (email)
    WHERE email IS NOT NULL;