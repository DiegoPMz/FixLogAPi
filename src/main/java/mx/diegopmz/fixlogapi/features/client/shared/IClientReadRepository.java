package mx.diegopmz.fixlogapi.features.client.shared;

import java.util.Optional;
import java.util.UUID;

public interface IClientReadRepository {
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Client> findByPhoneNumber(String phoneNumber);

    Optional<ClientReadModel> findById(UUID clientId);
}
