package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import mx.diegopmz.fixlogapi.features.order.shared.OrderPriority;
import mx.diegopmz.fixlogapi.features.order.shared.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;


public record OrderSummaryReadModel(
    UUID id,
    String ticketNumber,
    OrderStatus status,
    OrderPriority priority,
    boolean isWarranty,
    double price,
    LocalDateTime createdAt,
    OrderOverviewDevice device
) {
    public static OrderSummaryReadModel fromEntity(OrderSummaryViewEntity entity) {
        return new OrderSummaryReadModel(
            entity.getId(),
            entity.getTicketNumber(),
            entity.getStatus(),
            entity.getPriority(),
            entity.isWarranty(),
            entity.getPrice(),
            entity.getCreatedAt(),
            new OrderOverviewDevice(
                entity.getDeviceId(),
                entity.getModel(),
                entity.getBrand(),
                entity.getSerialNumber(),
                entity.getDeviceOwnerName()
            )
        );
    }

    public record OrderOverviewDevice(
        UUID id,
        String model,
        String brand,
        String serialNumber,
        String ownerName
    ) {
    }
}
