package mx.diegopmz.fixlogapi.common.domain.orderAggregate;

import jakarta.persistence.Id;
import lombok.Getter;
import mx.diegopmz.fixlogapi.common.domain.result.Result;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class Order {
    @Id
    private final UUID id;
    private final String ticketNumber;
    private final UUID deviceId;
    private final LocalDateTime createdAt;
    private final LocalDateTime warrantyUntil;
    private UUID assignedUserId;
    private OrderStatus status;
    private OrderPriority priority;
    private LocalDateTime updatedAt;
    private double finalCost;
    private double estimatedCost;
    private String issueDescription;
    private String technicalDiagnosis;
    private boolean isWarranty;

    private Order(Builder builder) {
        this.ticketNumber = generateTicketNumber();
        this.id = UUID.randomUUID();

        this.deviceId = builder.deviceId;
        this.assignedUserId = builder.assignedUserId;
        this.issueDescription = builder.issueDescription;
        this.technicalDiagnosis = builder.technicalDiagnosis;
        this.status = builder.status;
        this.priority = builder.priority;
        this.estimatedCost = builder.estimatedCost;
        this.finalCost = builder.finalCost;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.warrantyUntil = this.createdAt.plusDays(30);
    }

    private String generateTicketNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("FIX-%s-%s", datePart, randomPart);
    }

    public static class Builder {
        private final UUID deviceId;
        private final String issueDescription;
        private final double estimatedCost;

        // Optionals
        private UUID assignedUserId;
        private String technicalDiagnosis;
        private OrderStatus status = OrderStatus.RECIBIDO;
        private OrderPriority priority = OrderPriority.BAJA;
        private double finalCost = 0.0;

        public Builder(UUID deviceId, String issueDescription, double estimatedCost) {
            this.deviceId = deviceId;
            this.issueDescription = issueDescription;
            this.estimatedCost = estimatedCost;
        }

        public Builder assignedUserId(UUID assignedUserId) {
            this.assignedUserId = assignedUserId;
            return this;
        }

        public Builder technicalDiagnosis(String technicalDiagnosis) {
            this.technicalDiagnosis = technicalDiagnosis;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder priority(OrderPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder finalCost(double finalCost) {
            this.finalCost = finalCost;
            return this;
        }

        public Result<Order> build() {
            if (this.deviceId == null) return Result.failure(OrderErrors.deviceRequired());
            if (this.issueDescription == null || this.issueDescription.isBlank()) {
                return Result.failure(OrderErrors.descriptionRequired());
            }
            if (this.estimatedCost < 0) return Result.failure(OrderErrors.invalidEstimatedCost(this.estimatedCost));

            if (this.assignedUserId != null && this.assignedUserId.equals(new UUID(0, 0))) {
                return Result.failure(OrderErrors.invalidTechnician());
            }

            if (this.status == OrderStatus.REPARADO) {
                if (this.technicalDiagnosis == null || this.technicalDiagnosis.isBlank()) {
                    return Result.failure(OrderErrors.diagnosisRequiredForRepair());
                }
            }

            if (this.technicalDiagnosis != null && this.technicalDiagnosis.trim().isEmpty()) {
                this.technicalDiagnosis = null;
            }

            if (this.technicalDiagnosis != null && this.technicalDiagnosis.length() > 1000) {
                return Result.failure(OrderErrors.diagnosisTooLong(this.technicalDiagnosis.length(), 1000));
            }

            if (this.finalCost < 0) {
                return Result.failure(OrderErrors.invalidFinalCost(this.finalCost));
            }

            return Result.ok(new Order(this));
        }
    }
}