package mx.diegopmz.fixlogapi.features.client.shared;

import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Client Domain Model Unit Tests")
class ClientTest {

    private static final String VALID_NAME = "Diego Perez";
    private static final String VALID_PHONE = "5583126598";
    private static final String VALID_EMAIL = "diego.Perez11@gmail.com";

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldCreateClient_whenAllFieldsAreValid")
        void shouldCreateClient_whenAllFieldsAreValid() {
            // Act
            Result<Client> result = Client.create(VALID_NAME, VALID_PHONE, VALID_EMAIL);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            Client client = result.getValue();

            assertThat(client.getId()).isNotNull();
            assertThat(client.getName()).isEqualTo(VALID_NAME);
            assertThat(client.getPhoneNumber()).isEqualTo(VALID_PHONE);
            assertThat(client.getEmail()).isEqualTo(VALID_EMAIL.toLowerCase());
            assertThat(client.getDevices()).isEmpty();
        }

        @Test
        @DisplayName("shouldTrimWhitespace_whenNameHasExtraSpaces")
        void shouldTrimWhitespace_whenNameHasExtraSpaces() {
            // Act
            Client client = Client.create("   Diego Perez   ", VALID_PHONE, VALID_EMAIL).getValue();

            // Assert
            assertThat(client.getName()).isEqualTo("Diego Perez");
        }

        @Test
        @DisplayName("shouldConvertEmailToLowerCase_whenEmailHasUpperChars")
        void shouldConvertEmailToLowerCase_whenEmailHasUpperChars() {
            // Act
            Client client = Client.create(VALID_NAME, VALID_PHONE, "DIEGO@Perez.COM").getValue();

            // Assert
            assertThat(client.getEmail()).isEqualTo("diego@perez.com");
        }

        @Test
        @DisplayName("shouldSetCreatedAtAndUpdatedAt_whenClientIsInitiallyCreated")
        void shouldSetCreatedAtAndUpdatedAt_whenClientIsInitiallyCreated() {
            // Arrange
            LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

            // Act
            Client client = Client.create(VALID_NAME, VALID_PHONE, VALID_EMAIL).getValue();

            // Assert
            assertThat(client.getCreatedAt()).isNotNull();
            assertThat(client.getUpdatedAt()).isNotNull();

            assertThat(client.getCreatedAt()).isAfterOrEqualTo(beforeCreation);
            assertThat(client.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());

            assertThat(client.getUpdatedAt()).isEqualTo(client.getCreatedAt());
        }

        @Test
        @DisplayName("shouldCreateClient_whenEmailIsNull")
        void shouldCreateClient_whenEmailIsNull() {
            // Act
            Result<Client> result = Client.create(VALID_NAME, VALID_PHONE, null);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue().getEmail()).isNull();
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("shouldReturnError_whenNameIsBlank")
        void shouldReturnError_whenNameIsBlank(String invalidName) {
            // Act
            Result<Client> result = Client.create(invalidName, VALID_PHONE, VALID_EMAIL);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(ClientErrors.nameRequired().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenPhoneNumberIsNull")
        void shouldReturnError_whenPhoneNumberIsNull() {
            // Act
            Result<Client> result = Client.create(VALID_NAME, null, VALID_EMAIL);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(ClientErrors.phoneNumberRequired().code());
        }

        @ParameterizedTest
        @ValueSource(strings = {"123456789", "12345678901", "", "   "})
        @DisplayName("shouldReturnError_whenPhoneNumberLengthIsNotExactlyTen")
        void shouldReturnError_whenPhoneNumberLengthIsNotExactlyTen(String invalidPhone) {
            // Act
            Result<Client> result = Client.create(VALID_NAME, invalidPhone, VALID_EMAIL);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(ClientErrors.invalidPhoneNumberLength(invalidPhone).code());
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid-email", "test@", "@domain.com", "plainaddress"})
        @DisplayName("shouldReturnError_whenEmailFormatIsInvalid")
        void shouldReturnError_whenEmailFormatIsInvalid(String invalidEmail) {
            // Act
            Result<Client> result = Client.create(VALID_NAME, VALID_PHONE, invalidEmail);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(ClientErrors.invalidEmail(invalidEmail).code());
        }
    }

    @Nested
    @DisplayName("Client - Register Device Unit Tests")
    class ClientDeviceTest {

        private Client client;

        @BeforeEach
        void setUp() {
            this.client = Client.create(VALID_NAME, VALID_PHONE, VALID_EMAIL).getValue();
        }

        @Nested
        @DisplayName("Success Scenarios")
        class SuccessCases {

            @Test
            @DisplayName("shouldRegisterDeviceSuccessfully_whenDataIsValidAndNotDuplicate")
            void shouldRegisterDeviceSuccessfully_whenDataIsValidAndNotDuplicate() {
                // Act
                var result = client.registerDevice("Apple", "iPhone 13", "SN-APPLE-12345");

                // Assert
                assertThat(result.isSuccess()).isTrue();
                assertThat(client.getDevices()).hasSize(1);

                Device registeredDevice = client.getDevices().get(0);
                assertThat(registeredDevice.getBrand()).isEqualTo("Apple");
                assertThat(registeredDevice.getModel()).isEqualTo("iPhone 13");
                assertThat(registeredDevice.getSerialNumber()).isEqualTo("SN-APPLE-12345");
                assertThat(registeredDevice.getCreatedAt()).isNotNull();
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class FailureCases {

            @Test
            @DisplayName("shouldReturnError_whenDeviceSerialNumberIsAlreadyRegisteredToClient")
            void shouldReturnError_whenDeviceSerialNumberIsAlreadyRegisteredToClient() {
                // Arrange
                client.registerDevice("Apple", "iPhone 13", "SN-DUPLICATE-123");

                // Act
                var result = client.registerDevice("Samsung", "Galaxy S24", "SN-DUPLICATE-123");

                // Assert
                assertThat(result.isSuccess()).isFalse();
                assertThat(result.firstError())
                    .isPresent()
                    .get()
                    .extracting(AppError::code)
                    .isEqualTo(ClientErrors.deviceAlreadyRegistered("SN-DUPLICATE-123").code());

                assertThat(client.getDevices()).hasSize(1);
            }

            @Test
            @DisplayName("shouldForwardValidationError_whenDeviceFieldsAreInvalid")
            void shouldForwardValidationError_whenDeviceFieldsAreInvalid() {
                // Act
                var result = client.registerDevice("   ", "iPhone 13", "SN-APPLE-123");

                // Assert
                assertThat(result.isSuccess()).isFalse();
                assertThat(result.firstError())
                    .isPresent()
                    .get()
                    .extracting(AppError::code)
                    .isEqualTo(DeviceErrors.brandRequired().code());

                assertThat(client.getDevices()).isEmpty();
            }
        }
    }

}
