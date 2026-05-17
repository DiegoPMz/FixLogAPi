package mx.diegopmz.fixlogapi.features.user.shared;

import java.util.UUID;

public interface IUserWriteRepository {
    void save(User user);

    void update(User user);

    void delete(UUID id);
}
