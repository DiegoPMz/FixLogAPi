package mx.diegopmz.fixlogapi.features.client.shared;

import java.util.Optional;

public interface IClientReadRepository {
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Client> findByPhoneNumber(String phoneNumber);
}
