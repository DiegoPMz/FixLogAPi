CREATE MATERIALIZED VIEW order_summary_mview AS
SELECT o.id                      AS id,
       o.ticket_number,
       o.status,
       o.priority,
       o.is_warranty,
       COALESCE(o.final_cost, 0) AS price,
       o.created_at,
       d.id                      AS device_id,
       d.model,
       d.brand,
       d.serial_number,
       c.name                    AS device_owner_name
FROM orders o
         INNER JOIN devices d ON o.device_id = d.id
         INNER JOIN clients c ON d.client_id = c.id;