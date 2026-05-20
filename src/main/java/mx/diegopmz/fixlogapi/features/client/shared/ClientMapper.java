package mx.diegopmz.fixlogapi.features.client.shared;

import java.util.ArrayList;
import java.util.List;

public final class ClientMapper {
    private ClientMapper() {
    }

    public static ClientEntity toEntity(Client domain) {
        if (domain == null) {
            return null;
        }

        var entity = new ClientEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setEmail(domain.getEmail());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        List<DeviceEntity> entityDevices = domain.getDevices()
            .stream()
            .map(d -> new DeviceEntity(d.getId(), d.getBrand(), d.getModel(), d.getSerialNumber(), d.getCreatedAt()))
            .toList();

        entity.setDevices(entityDevices);

        return entity;
    }

    public static Client toDomain(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        List<Device> domainDevices = new ArrayList<>();
        if (entity.getDevices() != null) {
            domainDevices = entity.getDevices()
                .stream()
                .map(de -> new Device(
                    de.getId(),
                    de.getBrand(),
                    de.getModel(),
                    de.getSerialNumber(),
                    de.getCreatedAt()
                ))
                .toList();
        }

        return new Client(
            entity.getId(),
            entity.getName(),
            entity.getPhoneNumber(),
            entity.getEmail(),
            domainDevices,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
