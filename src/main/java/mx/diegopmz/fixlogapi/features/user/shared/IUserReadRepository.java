package mx.diegopmz.fixlogapi.features.user.shared;

import java.util.Optional;

public interface IUserReadRepository {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
