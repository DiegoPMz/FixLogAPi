package mx.diegopmz.fixlogapi.features.client.shared;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public Optional<ClientReadModel> findById(UUID clientId) {
        ClientReadModel client = entityManager.createQuery(
                "SELECT new ClientReadModel(c.id, c.name, c.email, c.phoneNumber) " +
                    "FROM ClientEntity c WHERE c.id = :clientId", ClientReadModel.class)
            .setParameter("clientId", clientId)
            .setHint("org.hibernate.readOnly", true)
            .getSingleResultOrNull();

        if (client == null) {
            return Optional.empty();
        }

        List<DeviceReadModel> devices = entityManager.createQuery(
                """
                    SELECT new DeviceReadModel(
                        d.id, d.brand, d.model, d.serialNumber
                    )
                    FROM ClientEntity c
                    JOIN c.devices d
                    WHERE c.id = :clientId
                    """, DeviceReadModel.class)
            .setParameter("clientId", clientId)
            .setHint("org.hibernate.readOnly", true)
            .getResultList();

        return Optional.of(new ClientReadModel(
            client.id(), client.name(), client.email(), client.phoneNumber(), devices
        ));
    }

}
