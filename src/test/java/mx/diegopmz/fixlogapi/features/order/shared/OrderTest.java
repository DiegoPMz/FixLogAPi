package mx.diegopmz.fixlogapi.features.order.shared;

import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Order Model Unit Tests")
class OrderTest {

    private final UUID VALID_DEVICE_ID = UUID.randomUUID();
    private final String VALID_DESCRIPTION = "Screen replacement for iPhone 13";
    private final double VALID_ESTIMATED_COST = 150.0;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldCreateOrder_whenRequiredFieldsAreProvided")
        void shouldCreateOrder_whenRequiredFieldsAreProvided() {
            // Act
            Result<Order> result = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .build();

            // Assert
            assertThat(result.isSuccess()).isTrue();
            Order order = result.getValue();

            assertThat(order.getDeviceId()).isEqualTo(VALID_DEVICE_ID);
            assertThat(order.getIssueDescription()).isEqualTo(VALID_DESCRIPTION);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.RECIBIDO);
        }

        @Test
        @DisplayName("shouldSetUpdatedAtEqualToCreatedAt_whenOrderIsInitiallyCreated")
        void shouldSetUpdatedAtEqualToCreatedAt_whenOrderIsInitiallyCreated() {
            // Act
            Order order = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .build().getValue();

            // Assert
            assertThat(order.getUpdatedAt()).isEqualTo(order.getCreatedAt());
            assertThat(order.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("shouldSetWarrantyForThirtyDays_whenOrderIsCreated")
        void shouldSetWarrantyForThirtyDays_whenOrderIsCreated() {
            // Act
            Order order = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .build().getValue();

            // Assert
            assertThat(order.getWarrantyUntil())
                .isEqualTo(order.getCreatedAt().plusDays(30));
        }

        @Test
        @DisplayName("shouldAllowRepairedStatus_whenTechnicalDiagnosisIsProvided")
        void shouldAllowRepairedStatus_whenTechnicalDiagnosisIsProvided() {
            // Act
            Result<Order> result = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .status(OrderStatus.REPARADO)
                .technicalDiagnosis("Replaced faulty capacitor")
                .build();

            // Assert
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue().getTechnicalDiagnosis()).isEqualTo("Replaced faulty capacitor");
        }

        @Test
        @DisplayName("shouldGenerateTicketWithCorrectFormat_whenOrderIsCreated")
        void shouldGenerateTicketWithCorrectFormat_whenOrderIsCreated() {
            // Act
            Order order = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .build().getValue();

            // Assert
            // Format: FIX-AAMMDD-XXXX
            assertThat(order.getTicketNumber())
                .matches("^FIX-\\d{6}-[A-Z0-9]{4}$");
        }

        @Test
        @DisplayName("shouldIncludeCurrentDateInTicket_whenOrderIsCreated")
        void shouldIncludeCurrentDateInTicket_whenOrderIsCreated() {
            // Arrange
            String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

            // Act
            Order order = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .build().getValue();

            // Assert
            assertThat(order.getTicketNumber()).contains(today);
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @Test
        @DisplayName("shouldReturnError_whenDeviceIdIsNull")
        void shouldReturnError_whenDeviceIdIsNull() {
            // Act
            Result<Order> result = new Order.Builder(null, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .build();

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(OrderErrors.deviceRequired().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenDescriptionIsBlank")
        void shouldReturnError_whenDescriptionIsBlank() {
            // Act
            Result<Order> result = new Order.Builder(VALID_DEVICE_ID, "   ", VALID_ESTIMATED_COST)
                .build();

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(OrderErrors.descriptionRequired().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenEstimatedCostIsNegative")
        void shouldReturnError_whenEstimatedCostIsNegative() {
            // Arrange
            double estimatedCost = -10.0;

            // Act
            Result<Order> result = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, estimatedCost)
                .build();

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(OrderErrors.invalidEstimatedCost(estimatedCost).code());
        }

        @Test
        @DisplayName("shouldReturnError_whenStatusIsRepairedButDiagnosisIsMissing")
        void shouldReturnError_whenStatusIsRepairedButDiagnosisIsMissing() {
            // Act
            Result<Order> result = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .status(OrderStatus.REPARADO)
                .technicalDiagnosis(null)
                .build();

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(OrderErrors.diagnosisRequiredForRepair().code());
        }

        @Test
        @DisplayName("shouldReturnError_whenFinalCostIsNegative")
        void shouldReturnError_whenFinalCostIsNegative() {
            // Arrange
            double finalCost = -50.0;

            // Act
            Result<Order> result = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .finalCost(finalCost)
                .build();

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo(OrderErrors.invalidFinalCost(finalCost).code());
        }

        @Test
        @DisplayName("shouldReturnError_whenDiagnosisIsTooLong")
        void shouldReturnError_whenDiagnosisIsTooLong() {
            // Arrange
            String longDiagnosis = "x".repeat(1001);

            // Act
            Result<Order> result = new Order.Builder(VALID_DEVICE_ID, VALID_DESCRIPTION, VALID_ESTIMATED_COST)
                .status(OrderStatus.REPARADO)
                .technicalDiagnosis(longDiagnosis)
                .build();

            // Assert
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.firstError())
                .isPresent()
                .get()
                .extracting(AppError::code)
                .isEqualTo("ORDER.DIAGNOSIS_TOO_LONG");
        }
    }
}