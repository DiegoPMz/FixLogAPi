package mx.diegopmz.fixlogapi.features.order.shared;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderReadRepository {
    Optional<Order> findById(UUID id);

    Optional<Order> findByTicketNumber(String ticketNumber);

    List<OrderReadModel> findByDeviceId(UUID deviceId);
};

