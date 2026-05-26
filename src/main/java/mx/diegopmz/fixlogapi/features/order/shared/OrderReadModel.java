package mx.diegopmz.fixlogapi.features.order.shared;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderReadModel(
    UUID id,
    String ticketNumber,
    UUID deviceId,
    LocalDateTime createdAt,
    LocalDateTime warrantyUntil,
    UUID assignedUserId,
    OrderStatus status,
    OrderPriority priority,
    LocalDateTime updatedAt,
    double finalCost,
    double estimatedCost,
    String issueDescription,
    String technicalDiagnosis,
    boolean isWarranty
) {
}
