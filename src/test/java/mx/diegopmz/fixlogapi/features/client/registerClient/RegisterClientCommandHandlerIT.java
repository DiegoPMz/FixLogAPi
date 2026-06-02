package mx.diegopmz.fixlogapi.features.client.registerClient;

import jakarta.persistence.EntityManager;
import mx.diegopmz.fixlogapi.BaseIntegrationTest;
import mx.diegopmz.fixlogapi.common.errors.AppError;
import mx.diegopmz.fixlogapi.common.result.Created;
import mx.diegopmz.fixlogapi.common.result.Result;
import mx.diegopmz.fixlogapi.features.client.shared.Client;
import mx.diegopmz.fixlogapi.features.client.shared.ClientErrors;
import mx.diegopmz.fixlogapi.features.client.shared.IClientReadRepository;
import mx.diegopmz.fixlogapi.features.client.shared.IClientWriteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Register Client Command Handler Integration Tests")
public class RegisterClientCommandHandlerIT extends BaseIntegrationTest {

    @Autowired
    private RegisterClientCommandHandler commandHandler;

    @Autowired
    private IClientReadRepository readRepository;

    @Autowired
    private IClientWriteRepository writeRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldRegisterClient_whenCommandDataIsValid")
        void shouldRegisterClient_whenCommandDataIsValid() {
            // Arrange
            var command = new RegisterClientCommand("5551234567", "john.doe@example.com", "John Doe");

            // Act
            Result<Created> result = commandHandler.handle(command);

            // Assert
            assertThat(result.isSuccess()).isTrue();

            boolean exists = readRepository.existsByPhoneNumber("5551234567");
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("shouldBeIdempotentAndReturnSuccess_whenClientPhoneNumberAlreadyExists")
        void shouldBeIdempotentAndReturnSuccess_whenClientPhoneNumberAlreadyExists() {
            // Arrange
            Client existingClient = Client.create("Jane Doe", "5559876543", "jane.doe@example.com").getValue();
            writeRepository.save(existingClient);

            var command = new RegisterClientCommand("5559876543", "duplicate@example.com", "Jane Duplicate");

            // Act
            Result<Created> result = commandHandler.handle(command);

            // Assert
            assertThat(result.isSuccess()).isTrue();

            Optional<Client> persistedClientOpt = readRepository.findByPhoneNumber("5559876543");
            assertThat(persistedClientOpt).isPresent();

            Client persistedClient = persistedClientOpt.get();
            assertThat(persistedClient.getName()).isEqualTo("Jane Doe");
            assertThat(persistedClient.getEmail()).isEqualTo("jane.doe@example.com");

            entityManager.flush();

            Long count = entityManager.createQuery(
                    "SELECT COUNT(c) FROM ClientEntity c", Long.class)
                .setHint("org.hibernate.readOnly", true)
                .getSingleResult();

            assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @Test
        @DisplayName("shouldReturnValidationError_whenCommandContainsInvalidClientData")
        void shouldReturnValidationError_whenCommandContainsInvalidClientData() {
            // Arrange
            var command = new RegisterClientCommand("5551112223", "invalid.name@example.com", " ");

            // Act
            Result<Created> result = commandHandler.handle(command);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(ClientErrors.nameRequired().code());

            boolean exists = readRepository.existsByPhoneNumber("5551112223");
            assertThat(exists).isFalse();
        }
    }
}
