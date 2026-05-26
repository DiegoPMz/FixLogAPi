package mx.diegopmz.fixlogapi.features.client.shared;

import java.util.List;
import java.util.UUID;

public record ClientReadModel(
    UUID id,
    String name,
    String email,
    String phoneNumber,
    List<DeviceReadModel> devices
) {
    // JPQL CONSTRUCTOR
    public ClientReadModel(UUID id, String name, String email, String phoneNumber) {
        this(id, name, email, phoneNumber, List.of());
    }
}
