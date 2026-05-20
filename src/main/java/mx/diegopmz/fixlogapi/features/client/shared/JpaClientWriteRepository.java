package mx.diegopmz.fixlogapi.features.client.shared;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class JpaClientWriteRepository implements IClientWriteRepository {
    private final EntityManager entityManager;

    public JpaClientWriteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Client client) {
        var entity = ClientMapper.toEntity(client);
        entityManager.persist(entity);
    }
}
