package mx.diegopmz.fixlogapi.features.client.shared;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaClientReadRepository implements IClientReadRepository {
    private final EntityManager entityManager;

    public JpaClientReadRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(c) FROM ClientEntity c WHERE c.phoneNumber = :phone", Long.class)
            .setParameter("phone", phoneNumber)
            .setHint("org.hibernate.readOnly", true)
            .getSingleResult();

        return count > 0;
    }

    @Override
    public Optional<Client> findByPhoneNumber(String phoneNumber) {
        ClientEntity entity = entityManager.createQuery(
                "SELECT c FROM ClientEntity c WHERE c.phoneNumber = :phone", ClientEntity.class)
            .setParameter("phone", phoneNumber)
            .setHint("org.hibernate.readOnly", true)
            .getSingleResultOrNull();

        return Optional.ofNullable(entity).map(ClientMapper::toDomain);
    }
}
