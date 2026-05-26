package mx.diegopmz.fixlogapi.features.order.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JpaOrderWriteRepository implements IOrderWriteRepository {
    private final EntityManager entityManager;

    public JpaOrderWriteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Order order) {
        var entity = OrderMapper.toEntity(order);

        entityManager.persist(entity);
    }

    @Override
    public void update(Order order) {
        var entity = OrderMapper.toEntity(order);

        entityManager.merge(entity);
    }

    @Override
    public void delete(UUID id) {
        var entity = entityManager.find(OrderEntity.class, id);

        if (entity == null) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }

        entityManager.remove(entity);
    }
}
