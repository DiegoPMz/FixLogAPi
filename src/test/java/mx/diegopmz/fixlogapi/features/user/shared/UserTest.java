package mx.diegopmz.fixlogapi.features.user.shared;

import mx.diegopmz.fixlogapi.common.errors.AppError;
import mx.diegopmz.fixlogapi.common.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Model Unit Tests")
class UserTest {

    private final String VALID_EXTERNAL_ID = "auth0|64f1a2b3c4d5e6f7";
    private final String VALID_NAME = "John";
    private final String VALID_LAST_NAME = "Doe";
    private final String VALID_EMAIL = "john.doe@example.com";
    private final UserRoles VALID_ROLE = UserRoles.TECHNICIAN;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldCreateUser_whenAllFieldsAreValid")
        void shouldCreateUser_whenAllFieldsAreValid() {
            // Act
            Result<User> result = User.create(
                VALID_EXTERNAL_ID, VALID_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_ROLE
            );

            // Assert
            assertThat(result.isSuccess()).isTrue();
            User user = result.getValue();

            assertThat(user.getId()).isNotNull();
            assertThat(user.getExternalId()).isEqualTo(VALID_EXTERNAL_ID);
            assertThat(user.getEmail()).isEqualTo(VALID_EMAIL.toLowerCase());
            assertThat(user.getName()).isEqualTo(VALID_NAME);
            assertThat(user.getRole()).isEqualTo(VALID_ROLE);
        }

        @Test
        @DisplayName("shouldTrimWhitespace_whenNamesHaveExtraSpaces")
        void shouldTrimWhitespace_whenNamesHaveExtraSpaces() {
            // Act
            User user = User.create(
                VALID_EXTERNAL_ID, "  Jane  ", "  Smith  ", VALID_EMAIL, VALID_ROLE
            ).getValue();

            // Assert
            assertThat(user.getName()).isEqualTo("Jane");
            assertThat(user.getLastName()).isEqualTo("Smith");
        }

        @Test
        @DisplayName("shouldConvertEmailToLowerCase_whenEmailHasUpperChars")
        void shouldConvertEmailToLowerCase_whenEmailHasUpperChars() {
            // Act
            User user = User.create(
                VALID_EXTERNAL_ID, VALID_NAME, VALID_LAST_NAME, "UPPER@case.com", VALID_ROLE
            ).getValue();

            // Assert
            assertThat(user.getEmail()).isEqualTo("upper@case.com");
        }

        @Test
        @DisplayName("shouldSetCreatedAtAndUpdatedAt_whenUserIsInitiallyCreated")
        void shouldSetCreatedAtAndUpdatedAt_whenUserIsInitiallyCreated() {
            // Arrange
            LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

            // Act
            User user = User.create(
                VALID_EXTERNAL_ID, VALID_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_ROLE
            ).getValue();

            // Assert
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();

            assertThat(user.getCreatedAt()).isAfterOrEqualTo(beforeCreation);
            assertThat(user.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());

            assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @Test
        @DisplayName("shouldReturnError_whenExternalIdIsBlank")
        void shouldReturnError_whenExternalIdIsBlank() {
            // Act
            Result<User> result = User.create("", VALID_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_ROLE);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(UserErrors.externalIdRequired().code());
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid-email", "test@", "@domain.com", "plainaddress"})
        @DisplayName("shouldReturnError_whenEmailFormatIsInvalid")
        void shouldReturnError_whenEmailFormatIsInvalid(String invalidEmail) {
            // Act
            Result<User> result = User.create(VALID_EXTERNAL_ID, VALID_NAME, VALID_LAST_NAME, invalidEmail, VALID_ROLE);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(UserErrors.invalidEmail(invalidEmail).code());
        }

        @Test
        @DisplayName("shouldReturnError_whenNameIsTooShort")
        void shouldReturnError_whenNameIsTooShort() {
            // Act
            Result<User> result = User.create(VALID_EXTERNAL_ID, "A", VALID_LAST_NAME, VALID_EMAIL, VALID_ROLE);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(UserErrors.invalidName().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenRoleIsNull")
        void shouldReturnError_whenRoleIsNull() {
            // Act
            Result<User> result = User.create(VALID_EXTERNAL_ID, VALID_NAME, VALID_LAST_NAME, VALID_EMAIL, null);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(UserErrors.roleRequired().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenAttemptingToCreateAdminViaStandardFlow")
        void shouldReturnError_whenAttemptingToCreateAdminViaStandardFlow() {
            // Act
            Result<User> result = User.create(
                VALID_EXTERNAL_ID, VALID_NAME, VALID_LAST_NAME, VALID_EMAIL, UserRoles.ADMIN
            );

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(UserErrors.adminCreationNotAllowed().code());
        }
    }
}
