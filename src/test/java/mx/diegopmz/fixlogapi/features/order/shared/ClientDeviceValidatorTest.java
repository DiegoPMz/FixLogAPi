package mx.diegopmz.fixlogapi.features.order.shared;

import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.errors.ErrorTypes;
import mx.diegopmz.fixlogapi.common.domain.result.Result;
import mx.diegopmz.fixlogapi.common.domain.result.Success;
import mx.diegopmz.fixlogapi.features.client.shared.ClientErrors;
import mx.diegopmz.fixlogapi.features.client.shared.ClientReadModel;
import mx.diegopmz.fixlogapi.features.client.shared.DeviceReadModel;
import mx.diegopmz.fixlogapi.features.client.shared.IClientReadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Client Device Validator Unit Tests")
class ClientDeviceValidatorTest {

    private final UUID CLIENT_ID = UUID.randomUUID();
    private final UUID DEVICE_ID = UUID.randomUUID();

    @Mock
    private IClientReadRepository clientReadRepository;

    @InjectMocks
    private ClientDeviceValidator validator;

    @Test
    @DisplayName("shouldReturnSuccess_whenClientExistsAndOwnsTheDevice")
    void shouldReturnSuccess_whenClientExistsAndOwnsTheDevice() {
        // Arrange
        var mockDevice = new DeviceReadModel(DEVICE_ID, "Apple", "iPhone 15", "SN-123");
        var mockClient = new ClientReadModel(CLIENT_ID, "John Doe", "john@example.com", "12345", List.of(mockDevice));

        when(clientReadRepository.findById(CLIENT_ID)).thenReturn(Optional.of(mockClient));

        // Act
        Result<Success> result = validator.validateOwnership(CLIENT_ID, DEVICE_ID);

        // Assert
        assertThat(result.isSuccess()).isTrue();
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @Test
        @DisplayName("shouldReturnFailure_whenClientDoesNotExist")
        void shouldReturnFailure_whenClientDoesNotExist() {
            // Arrange
            when(clientReadRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

            // Act
            Result<Success> result = validator.validateOwnership(CLIENT_ID, DEVICE_ID);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            AppError error = result.getErrors().get(0);
            assertThat(error.type()).isEqualTo(ErrorTypes.NOT_FOUND);
            assertThat(error.code()).isEqualTo(ClientErrors.clientNotFound(CLIENT_ID).code());
        }

        @Test
        @DisplayName("shouldReturnFailure_whenDeviceDoesNotBelongToClient")
        void shouldReturnFailure_whenDeviceDoesNotBelongToClient() {
            // Arrange
            UUID anotherDeviceId = UUID.randomUUID();
            var mockDevice = new DeviceReadModel(anotherDeviceId, "Samsung", "S23", "SN-999");
            var mockClient = new ClientReadModel(CLIENT_ID, "John Doe", "john@example.com", "12345", List.of(mockDevice));

            when(clientReadRepository.findById(CLIENT_ID)).thenReturn(Optional.of(mockClient));

            // Act
            Result<Success> result = validator.validateOwnership(CLIENT_ID, DEVICE_ID);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            AppError error = result.getErrors().get(0);
            assertThat(error.type()).isEqualTo(ErrorTypes.CONFLICT);
            assertThat(error.code()).isEqualTo(ClientErrors.deviceNotOwnedByClient(DEVICE_ID, CLIENT_ID).code());
        }
    }
}
