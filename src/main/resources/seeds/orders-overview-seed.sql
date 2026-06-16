INSERT INTO clients (id, name, phone_number, email, created_at, updated_at)
VALUES ('c1111111-1111-1111-1111-111111111111', 'María García', '5512345678', 'maria@example.com', NOW(), NOW()),
       ('c2222222-2222-2222-2222-222222222222', 'Carlos López', '5587654321', 'carlos@example.com', NOW(), NOW());

INSERT INTO devices (id, client_id, brand, model, serial_number, created_at)
VALUES ('d1111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', 'Apple', 'iPhone 13 Pro',
        'SN12345', NOW()),
       ('d2222222-2222-2222-2222-222222222222', 'c2222222-2222-2222-2222-222222222222', 'Apple', 'MacBook Air M2',
        'SN67890', NOW()),
       ('d3333333-3333-3333-3333-333333333333', 'c1111111-1111-1111-1111-111111111111', 'Nintendo', 'Switch', 'SN55555',
        NOW());


INSERT INTO orders (id, ticket_number, device_id, created_at, status, priority, final_cost, estimated_cost,
                    issue_description, is_warranty)
VALUES ('01111111-1111-1111-1111-111111111111', '#001', 'd1111111-1111-1111-1111-111111111111', '2026-06-10 10:00:00',
        'RECIBIDO', 'ALTA', 150.0, 150.0, 'Pantalla rota', true),
       ('02222222-2222-2222-2222-222222222222', '#002', 'd2222222-2222-2222-2222-222222222222', '2026-06-10 09:00:00',
        'RECIBIDO', 'MEDIA', 280.0, 300.0, 'Batería inflada', false),
       ('03333333-3333-3333-3333-333333333333', '#004', 'd3333333-3333-3333-3333-333333333333', '2026-06-10 08:00:00',
        'REPARADO', 'BAJA', 0.0, 50.0, 'Mantenimiento preventivo', true);


REFRESH MATERIALIZED VIEW order_summary_mview;