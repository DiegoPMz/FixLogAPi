CREATE TABLE orders
(
    id                  UUID                        NOT NULL,
    ticket_number       VARCHAR(255)                NOT NULL,
    device_id           UUID                        NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    warranty_until      TIMESTAMP WITHOUT TIME ZONE,
    user_id             UUID,
    status              VARCHAR(50)                 NOT NULL,
    priority            VARCHAR(50)                 NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    final_cost          DOUBLE PRECISION            NOT NULL DEFAULT 0.0,
    estimated_cost      DOUBLE PRECISION            NOT NULL DEFAULT 0.0,
    issue_description   VARCHAR(1000)               NOT NULL,
    technical_diagnosis VARCHAR(2000),
    is_warranty         BOOLEAN                     NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT uq_orders_ticket_number UNIQUE (ticket_number)
);

CREATE INDEX idx_orders_device_id ON orders (device_id);
CREATE INDEX idx_orders_user_id ON orders (user_id);