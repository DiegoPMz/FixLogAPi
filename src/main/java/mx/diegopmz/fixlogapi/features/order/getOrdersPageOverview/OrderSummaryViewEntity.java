package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mx.diegopmz.fixlogapi.features.order.shared.OrderPriority;
import mx.diegopmz.fixlogapi.features.order.shared.OrderStatus;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "order_summary_mview")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderSummaryViewEntity {
    @Enumerated(EnumType.STRING)
    OrderPriority priority;

    @Column(name = "is_warranty")
    boolean isWarranty;

    @Id
    private UUID id;

    @Column(name = "ticket_number")
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private double price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "device_id")
    private UUID deviceId;

    private String model;
    private String brand;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "device_owner_name")
    private String deviceOwnerName;
}
