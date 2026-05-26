package mx.diegopmz.fixlogapi.features.order.shared;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class OrderEntity {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "ticket_number", unique = true, nullable = false, updatable = false)
    private String ticketNumber;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "warranty_until")
    private LocalDateTime warrantyUntil;

    @Column(name = "user_id")
    private UUID assignedUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderPriority priority;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "final_cost")
    private double finalCost;

    @Column(name = "estimated_cost")
    private double estimatedCost;

    @Column(name = "issue_description", length = 1000, nullable = false)
    private String issueDescription;

    @Column(name = "technical_diagnosis", length = 2000)
    private String technicalDiagnosis;

    @Column(name = "is_warranty", nullable = false)
    private boolean isWarranty;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
