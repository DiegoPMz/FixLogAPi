CREATE MATERIALIZED VIEW client_summary_mview AS
SELECT c.id                           AS client_id,
       c.name,
       c.email,
       c.phone_number,
       COUNT(o.id)                    AS total_orders,
       COALESCE(SUM(o.final_cost), 0) AS total_spent,
       c.created_at
FROM clients c
         LEFT JOIN devices d ON c.id = d.client_id
         LEFT JOIN orders o ON d.id = o.device_id
GROUP BY c.id, c.name, c.email, c.phone_number, c.created_at;