package mx.diegopmz.fixlogapi.features.user.provisionUser;


import jakarta.persistence.EntityManager;
import mx.diegopmz.fixlogapi.BaseIntegrationTest;
import mx.diegopmz.fixlogapi.common.errors.AppError;
import mx.diegopmz.fixlogapi.features.user.shared.JpaUserReadRepository;
import mx.diegopmz.fixlogapi.features.user.shared.UserErrors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Provision User Command Handler Integration Tests")
class ProvisionUserCommandHandlerIT extends BaseIntegrationTest {
    private final String VALID_EXTERNAL_ID = "auth0|64f1a2b3c4d5e6f7";
    private final String VALID_NAME = "John";
    private final String VALID_LAST_NAME = "Doe";
    private final String VALID_EMAIL = "john.doe@example.com";

    @Autowired
    private ProvisionUserCommandHandler commandHandler;

    @Autowired
    private JpaUserReadRepository readRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldProvisionNewUserSuccessfully_whenCommandIsValidAndUserDoesNotExist")
        void shouldProvisionNewUserSuccessfully_whenCommandIsValidAndUserDoesNotExist() {
            // Arrange
            var command = new ProvisionUserCommand(
                VALID_EXTERNAL_ID,
                VALID_EMAIL,
                VALID_NAME,
                VALID_LAST_NAME
            );

            // Act
            var result = commandHandler.handle(command);

            // Assert
            assertThat(result.isSuccess()).isTrue();

            boolean userExists = readRepository.existsByEmail(VALID_EMAIL);
            assertThat(userExists).isTrue();
        }

        @Test
        @DisplayName("shouldNotCreateUserButReturnSuccess_whenUserAlreadyExistsWithIdenticalExternalId")
        void shouldNotCreateUserButReturnSuccess_whenUserAlreadyExistsWithIdenticalExternalId() {
            // Arrange
            var firstCommand = new ProvisionUserCommand(
                VALID_EXTERNAL_ID,
                VALID_EMAIL,
                VALID_NAME,
                VALID_LAST_NAME
            );
            var firstResult = commandHandler.handle(firstCommand);
            assertThat(firstResult.isSuccess()).isTrue();

            var identicalRetryCommand = new ProvisionUserCommand(
                VALID_EXTERNAL_ID,
                VALID_EMAIL,
                "John (Retry)",
                "Doe (Retry)"
            );

            // Act
            var secondResult = commandHandler.handle(identicalRetryCommand);

            // Assert
            assertThat(secondResult.isSuccess()).isTrue();

            Long userCount = entityManager.createQuery(
                    "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class)
                .setParameter("email", VALID_EMAIL)
                .getSingleResult();

            assertThat(userCount).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {
        @Test
        @DisplayName("shouldReturnConflictError_whenEmailAlreadyExistsWithDifferentExternalId")
        void shouldReturnConflictError_whenEmailAlreadyExistsWithDifferentExternalId() {
            // Arrange
            var firstCommand = new ProvisionUserCommand(
                VALID_EXTERNAL_ID,
                VALID_EMAIL,
                VALID_NAME,
                VALID_LAST_NAME
            ); // legit User
            commandHandler.handle(firstCommand);

            var maliciousCommand = new ProvisionUserCommand(
                "auth0|malicious_attacker_123",
                VALID_EMAIL,
                "Jane",
                "Smith"
            );

            // Act
            var result = commandHandler.handle(maliciousCommand);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(UserErrors.emailAlreadyInUse().code());
        }

        @Test
        @DisplayName("shouldReturnValidationError_whenCommandContainsInvalidData")
        void shouldReturnValidationError_whenCommandContainsInvalidData() {
            // Arrange
            String invalidEmail = "plainaddress";
            var invalidCommand = new ProvisionUserCommand(
                VALID_EXTERNAL_ID,
                invalidEmail,
                VALID_NAME,
                VALID_LAST_NAME
            );

            // Act
            var result = commandHandler.handle(invalidCommand);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError()).isPresent();
        }
    }
}
