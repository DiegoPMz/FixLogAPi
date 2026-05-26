package mx.diegopmz.fixlogapi.features.client.shared;

import java.util.UUID;

public record DeviceReadModel(
    UUID id,
    String brand,
    String model,
    String serialNumber
) {
}
