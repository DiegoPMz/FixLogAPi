package mx.diegopmz.fixlogapi.features.order.shared;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOrderReadRepository implements IOrderReadRepository {

    private final EntityManager entityManager;

    public JpaOrderReadRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        OrderEntity entity = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.id = :id", OrderEntity.class)
            .setParameter("id", id)
            .setHint("org.hibernate.readOnly", true)
            .getSingleResultOrNull();

        return Optional.ofNullable(entity).map(OrderMapper::toDomain);
    }

    @Override
    public Optional<Order> findByTicketNumber(String ticketNumber) {
        OrderEntity entity = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.ticketNumber= :ticketNumber", OrderEntity.class)
            .setParameter("ticketNumber", ticketNumber)
            .setHint("org.hibernate.readOnly", true)
            .getSingleResultOrNull();

        return Optional.ofNullable(entity).map(OrderMapper::toDomain);
    }
    
    @Override
    public List<OrderReadModel> findByDeviceId(UUID deviceId) {
        return entityManager.createQuery(
                """
                    SELECT new OrderReadModel(
                        o.id,
                        o.ticketNumber,
                        o.deviceId,
                        o.createdAt,
                        o.warrantyUntil,
                        o.assignedUserId,
                        o.status,
                        o.priority,
                        o.updatedAt,
                        o.finalCost,
                        o.estimatedCost,
                        o.issueDescription,
                        o.technicalDiagnosis,
                        o.isWarranty
                    )
                    FROM OrderEntity o WHERE o.deviceId = :deviceId
                    """, OrderReadModel.class)
            .setParameter("deviceId", deviceId)
            .setHint("org.hibernate.readOnly", true)
            .getResultList();
    }
}
