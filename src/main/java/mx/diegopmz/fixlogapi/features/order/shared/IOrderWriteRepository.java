package mx.diegopmz.fixlogapi.features.order.shared;

import java.util.UUID;

public interface IOrderWriteRepository {
    void save(Order order);

    void update(Order order);

    void delete(UUID id);
}
