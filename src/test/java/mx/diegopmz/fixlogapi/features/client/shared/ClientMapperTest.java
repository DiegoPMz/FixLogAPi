package mx.diegopmz.fixlogapi.features.client.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClientMapper Unit Tests")
class ClientMapperTest {

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldMapToEntity_whenDomainClientIsValid")
        void shouldMapToEntity_whenDomainClientIsValid() {
            // Arrange
            Client domainClient = Client.create("Diego Peralta", "5703410681", "diego@fixlog.com").getValue();
            domainClient.registerDevice("Apple", "iPhone 13", "SN-APPLE-12345");

            // Act
            ClientEntity entity = ClientMapper.toEntity(domainClient);

            // Assert
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(domainClient.getId());
            assertThat(entity.getName()).isEqualTo(domainClient.getName());
            assertThat(entity.getPhoneNumber()).isEqualTo(domainClient.getPhoneNumber());
            assertThat(entity.getEmail()).isEqualTo(domainClient.getEmail());
            assertThat(entity.getCreatedAt()).isEqualTo(domainClient.getCreatedAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domainClient.getUpdatedAt());

            // Devices validations
            assertThat(entity.getDevices()).hasSize(1);
            DeviceEntity deviceEntity = entity.getDevices().get(0);
            Device domainDevice = domainClient.getDevices().get(0);

            assertThat(deviceEntity.getId()).isEqualTo(domainDevice.getId());
            assertThat(deviceEntity.getBrand()).isEqualTo(domainDevice.getBrand());
            assertThat(deviceEntity.getModel()).isEqualTo(domainDevice.getModel());
            assertThat(deviceEntity.getSerialNumber()).isEqualTo(domainDevice.getSerialNumber());
            assertThat(deviceEntity.getCreatedAt()).isEqualTo(domainDevice.getCreatedAt());
        }

        @Test
        @DisplayName("shouldMapToDomain_whenClientEntityIsValid")
        void shouldMapToDomain_whenClientEntityIsValid() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            ClientEntity entity = new ClientEntity();
            entity.setId(clientId);
            entity.setName("Diego Ramirez");
            entity.setPhoneNumber("5703410681");
            entity.setEmail("diego@fixlog.com");
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            DeviceEntity deviceEntity = new DeviceEntity();
            deviceEntity.setId(UUID.randomUUID());
            deviceEntity.setBrand("Samsung");
            deviceEntity.setModel("Galaxy S24");
            deviceEntity.setSerialNumber("SN-SAMSUNG-987");
            deviceEntity.setCreatedAt(now);

            entity.setDevices(new ArrayList<>(List.of(deviceEntity)));

            // Act
            Client domain = ClientMapper.toDomain(entity);

            // Assert
            assertThat(domain).isNotNull();
            assertThat(domain.getId()).isEqualTo(entity.getId());
            assertThat(domain.getName()).isEqualTo(entity.getName());
            assertThat(domain.getPhoneNumber()).isEqualTo(entity.getPhoneNumber());
            assertThat(domain.getEmail()).isEqualTo(entity.getEmail());
            assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());

            // Devices validations
            assertThat(domain.getDevices()).hasSize(1);
            Device domainDevice = domain.getDevices().get(0);

            assertThat(domainDevice.getId()).isEqualTo(deviceEntity.getId());
            assertThat(domainDevice.getBrand()).isEqualTo(deviceEntity.getBrand());
            assertThat(domainDevice.getModel()).isEqualTo(deviceEntity.getModel());
            assertThat(domainDevice.getSerialNumber()).isEqualTo(deviceEntity.getSerialNumber());
            assertThat(domainDevice.getCreatedAt()).isEqualTo(deviceEntity.getCreatedAt());
        }

        @Test
        @DisplayName("shouldReturnNull_whenMappingNullObjects")
        void shouldReturnNull_whenMappingNullObjects() {
            // Assert & Act
            assertThat(ClientMapper.toEntity(null)).isNull();
            assertThat(ClientMapper.toDomain(null)).isNull();
        }

        @Test
        @DisplayName("shouldMapToDomainWithEmptyDevices_whenEntityDevicesListIsNull")
        void shouldMapToDomainWithEmptyDevices_whenEntityDevicesListIsNull() {
            // Arrange
            ClientEntity entity = new ClientEntity();
            entity.setId(UUID.randomUUID());
            entity.setDevices(null);

            // Act
            Client domain = ClientMapper.toDomain(entity);

            // Assert
            assertThat(domain).isNotNull();
            assertThat(domain.getDevices()).isNotNull().isEmpty();
        }
    }
}
