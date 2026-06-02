package mx.diegopmz.fixlogapi.features.client.getPageOverview;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientSummaryReadModel(
    UUID clientId,
    String name,
    String email,
    String phoneNumber,
    long totalOrders,
    double totalSpent,
    LocalDateTime createdAt
) {
}
