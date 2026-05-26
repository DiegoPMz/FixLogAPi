package mx.diegopmz.fixlogapi.features.order.createOrder;

import mx.diegopmz.fixlogapi.BaseIntegrationTest;
import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.errors.ErrorTypes;
import mx.diegopmz.fixlogapi.common.domain.result.Created;
import mx.diegopmz.fixlogapi.common.domain.result.Result;
import mx.diegopmz.fixlogapi.features.client.shared.Client;
import mx.diegopmz.fixlogapi.features.client.shared.ClientErrors;
import mx.diegopmz.fixlogapi.features.client.shared.IClientWriteRepository;
import mx.diegopmz.fixlogapi.features.order.shared.IOrderReadRepository;
import mx.diegopmz.fixlogapi.features.order.shared.OrderReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Create Order Command Handler Integration Tests")
public class CreateOrderCommandHandlerIT extends BaseIntegrationTest {

    @Autowired
    private CreateOrderCommandHandler commandHandler;

    @Autowired
    private IOrderReadRepository orderReadRepository;

    @Autowired
    private IClientWriteRepository clientWriteRepository;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldCreateAnOrder_whenCommandDataIsValid")
        void shouldCreateAnOrder_whenCommandDataIsValid() {
            // Arrange
            var client = Client.create("Tester Name", "5538765050", "tester@gmail.com").getValue();
            client.registerDevice("Apple", "iPhone 15", "SN-123456");

            clientWriteRepository.save(client);

            UUID registeredDeviceId = client.getDevices().get(0).getId();
            UUID mockedTechnicianId = UUID.randomUUID();

            var command = new CreateOrderCommand(
                mockedTechnicianId,
                client.getId(),
                registeredDeviceId,
                "Screen is cracked",
                "Requires display replacement",
                150.0
            );

            // Act
            Result<Created> result = commandHandler.handle(command);

            // Assert
            assertThat(result.isSuccess()).isTrue();

            List<OrderReadModel> orders = orderReadRepository.findByDeviceId(registeredDeviceId);

            assertThat(orders).isNotEmpty();
            assertThat(orders.get(0).deviceId()).isEqualTo(registeredDeviceId);
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @Test
        @DisplayName("shouldReturnFailure_whenClientDoesNotExist")
        void shouldReturnFailure_whenClientDoesNotExist() {
            // Arrange
            UUID nonExistentClientId = UUID.randomUUID();
            UUID randomDeviceId = UUID.randomUUID();
            UUID mockedTechnicianId = UUID.randomUUID();

            var command = new CreateOrderCommand(
                mockedTechnicianId,
                nonExistentClientId,
                randomDeviceId,
                "Battery replacement",
                "Battery health 70%",
                75.0
            );

            // Act
            Result<Created> result = commandHandler.handle(command);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            AppError error = result.getErrors().get(0);
            assertThat(error.code()).isEqualTo(ClientErrors.clientNotFound(nonExistentClientId).code());
            assertThat(error.type()).isEqualTo(ErrorTypes.NOT_FOUND);
        }

        @Test
        @DisplayName("shouldReturnFailure_whenDeviceDoesNotBelongToClient")
        void shouldReturnFailure_whenDeviceDoesNotBelongToClient() {
            // Arrange
            var clientA = Client.create("Client A", "5512345678", "clienta@gmail.com").getValue();
            clientA.registerDevice("Samsung", "Galaxy S23", "SN-SAMSUNG123");
            clientWriteRepository.save(clientA);

            var clientB = Client.create("Client B", "5587654321", "clientb@gmail.com").getValue();
            clientWriteRepository.save(clientB);

            UUID deviceOfClientA = clientA.getDevices().get(0).getId();
            UUID mockedTechnicianId = UUID.randomUUID();

            // Order Client B with device of Client A
            var command = new CreateOrderCommand(
                mockedTechnicianId,
                clientB.getId(),
                deviceOfClientA,
                "Back glass broken",
                "Visual damage only",
                50.0
            );

            // Act
            Result<Created> result = commandHandler.handle(command);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            AppError error = result.getErrors().get(0);
            assertThat(error.code()).isEqualTo(
                ClientErrors.deviceNotOwnedByClient(deviceOfClientA, clientB.getId()).code()
            );
            assertThat(error.type()).isEqualTo(ErrorTypes.CONFLICT);
        }
    }
}