package mx.diegopmz.fixlogapi.features.client.shared;

import mx.diegopmz.fixlogapi.common.errors.AppError;
import mx.diegopmz.fixlogapi.common.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Device Domain Model Unit Tests")
class DeviceTest {

    private static final String VALID_BRAND = "Apple";
    private static final String VALID_MODEL = "iPhone 13";
    private static final String VALID_SERIAL = "G6V2X8P9Q1";

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldCreateDevice_whenAllFieldsAreValid")
        void shouldCreateDevice_whenAllFieldsAreValid() {
            // Act
            Result<Device> result = Device.create(VALID_BRAND, VALID_MODEL, VALID_SERIAL);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            Device device = result.getValue();

            assertThat(device.getId()).isNotNull();
            assertThat(device.getBrand()).isEqualTo(VALID_BRAND);
            assertThat(device.getModel()).isEqualTo(VALID_MODEL);
            assertThat(device.getSerialNumber()).isEqualTo(VALID_SERIAL);
        }

        @Test
        @DisplayName("shouldTrimWhitespace_whenParametersHaveExtraSpaces")
        void shouldTrimWhitespace_whenParametersHaveExtraSpaces() {
            // Act
            Device device = Device.create("  Samsung  ", "  Galaxy S24  ", "  A1B2C3D4  ").getValue();

            // Assert
            assertThat(device.getBrand()).isEqualTo("Samsung");
            assertThat(device.getModel()).isEqualTo("Galaxy S24");
            assertThat(device.getSerialNumber()).isEqualTo("A1B2C3D4");
        }

        @Test
        @DisplayName("shouldSetCreatedAt_whenDeviceIsInitiallyCreated")
        void shouldSetCreatedAt_whenDeviceIsInitiallyCreated() {
            // Arrange
            LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

            // Act
            Device device = Device.create(VALID_BRAND, VALID_MODEL, VALID_SERIAL).getValue();

            // Assert
            assertThat(device.getCreatedAt()).isNotNull();
            assertThat(device.getCreatedAt()).isAfterOrEqualTo(beforeCreation);
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("shouldReturnError_whenBrandIsBlank")
        void shouldReturnError_whenBrandIsBlank(String invalidBrand) {
            // Act
            Result<Device> result = Device.create(invalidBrand, VALID_MODEL, VALID_SERIAL);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(DeviceErrors.brandRequired().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenBrandIsNull")
        void shouldReturnError_whenBrandIsNull() {
            // Act
            Result<Device> result = Device.create(null, VALID_MODEL, VALID_SERIAL);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(DeviceErrors.brandRequired().code());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("shouldReturnError_whenModelIsBlank")
        void shouldReturnError_whenModelIsBlank(String invalidModel) {
            // Act
            Result<Device> result = Device.create(VALID_BRAND, invalidModel, VALID_SERIAL);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(DeviceErrors.modelRequired().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenModelIsNull")
        void shouldReturnError_whenModelIsNull() {
            // Act
            Result<Device> result = Device.create(VALID_BRAND, null, VALID_SERIAL);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(DeviceErrors.modelRequired().code());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("shouldReturnError_whenSerialNumberIsBlank")
        void shouldReturnError_whenSerialNumberIsBlank(String invalidSerial) {
            // Act
            Result<Device> result = Device.create(VALID_BRAND, VALID_MODEL, invalidSerial);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(DeviceErrors.serialNumberRequired().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenSerialNumberIsNull")
        void shouldReturnError_whenSerialNumberIsNull() {
            // Act
            Result<Device> result = Device.create(VALID_BRAND, VALID_MODEL, null);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(DeviceErrors.serialNumberRequired().code());
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "12", "123", "  1  "})
        @DisplayName("shouldReturnError_whenSerialNumberIsTooShort")
        void shouldReturnError_whenSerialNumberIsTooShort(String shortSerial) {
            // Act
            Result<Device> result = Device.create(VALID_BRAND, VALID_MODEL, shortSerial);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(DeviceErrors.serialNumberTooShort().code());
        }
    }
}
