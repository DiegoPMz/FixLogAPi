package mx.diegopmz.fixlogapi.common.result;

import mx.diegopmz.fixlogapi.common.errors.AppError;
import mx.diegopmz.fixlogapi.common.errors.ErrorTypes;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Result Object Unit Tests")
class ResultTest {

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldEncapsulateValue_whenOperationIsSuccessful")
        void shouldEncapsulateValue_whenOperationIsSuccessful() {
            // Arrange
            Map<String, Object> value = Map.of("id", 123, "status", "active");

            // Act
            Result<Map<String, Object>> result = Result.ok(value);

            // Assert
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue()).isEqualTo(value);
            assertThat(result.getErrors()).isEmpty();
        }

        @Test
        @DisplayName("shouldHaveEmptyErrorList_whenResultIsOk")
        void shouldHaveEmptyErrorList_whenResultIsOk() {
            Result<String> result = Result.ok("Success");

            assertThat(result.getErrors()).isEmpty();
            assertThat(result.firstError()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @Test
        @DisplayName("shouldReturnNullValue_whenOperationFails")
        void shouldReturnNullValue_whenOperationFails() {
            // Arrange
            AppError error = AppError.of("Validation failed", ErrorTypes.VALIDATION, "ERR_VAL");

            // Act
            Result<Object> result = Result.failure(error);

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getValue()).isNull();
        }

        @Test
        @DisplayName("shouldPreserveErrorMetadata_whenFailureWithMetadataIsCreated")
        void shouldPreserveErrorMetadata_whenFailureWithMetadataIsCreated() {
            // Arrange
            var meta = new HashMap<String, String>();
            meta.put("retry", "true");
            AppError error = AppError.of("System busy", "SYS_503", ErrorTypes.TIMEOUT, meta);

            // Act
            Result<Object> result = Result.failure(error);

            // Assert
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::metadata)
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsEntry("retry", "true")
                .hasSize(1);
        }

        @Test
        @DisplayName("shouldMaintainErrorOrder_whenMultipleErrorsAreProvided")
        void shouldMaintainErrorOrder_whenMultipleErrorsAreProvided() {
            // Arrange
            AppError e1 = AppError.of("First error", ErrorTypes.VALIDATION, "E1");
            AppError e2 = AppError.of("Second error", ErrorTypes.CONFLICT, "E2");

            // Act
            Result<Object> result = Result.failure(List.of(e1, e2));

            // Assert
            assertThat(result.getErrors())
                .hasSize(2)
                .containsExactly(e1, e2);

            assertThat(result.firstError()).contains(e1);
        }

        @Test
        @DisplayName("shouldReturnEmptyMap_whenErrorHasNoMetadata")
        void shouldReturnEmptyMap_whenErrorHasNoMetadata() {
            // Arrange
            AppError error = AppError.of("Simple error", ErrorTypes.BAD_REQUEST, "E0");

            // Act
            Result<Object> result = Result.failure(error);

            // Assert
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::metadata)
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .isEmpty();
        }
    }
}