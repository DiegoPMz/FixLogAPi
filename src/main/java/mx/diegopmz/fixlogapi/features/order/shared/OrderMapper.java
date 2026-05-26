package mx.diegopmz.fixlogapi.features.order.shared;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }

        var entity = new OrderEntity();

        entity.setId(domain.getId());
        entity.setTicketNumber(domain.getTicketNumber());
        entity.setDeviceId(domain.getDeviceId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setWarrantyUntil(domain.getWarrantyUntil());
        entity.setAssignedUserId(domain.getAssignedUserId());
        entity.setStatus(domain.getStatus());
        entity.setPriority(domain.getPriority());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setFinalCost(domain.getFinalCost());
        entity.setEstimatedCost(domain.getEstimatedCost());
        entity.setIssueDescription(domain.getIssueDescription());
        entity.setTechnicalDiagnosis(domain.getTechnicalDiagnosis());
        entity.setWarranty(domain.isWarranty());

        return entity;
    }

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new Order(
            entity.getId(),
            entity.getTicketNumber(),
            entity.getDeviceId(),
            entity.getCreatedAt(),
            entity.getWarrantyUntil(),
            entity.getAssignedUserId(),
            entity.getStatus(),
            entity.getPriority(),
            entity.getUpdatedAt(),
            entity.getFinalCost(),
            entity.getEstimatedCost(),
            entity.getIssueDescription(),
            entity.getTechnicalDiagnosis(),
            entity.isWarranty()
        );
    }
}

