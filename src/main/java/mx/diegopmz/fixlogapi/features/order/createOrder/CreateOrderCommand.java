package mx.diegopmz.fixlogapi.features.order.createOrder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mx.diegopmz.fixlogapi.features.order.shared.OrderValidationConstants;

import java.util.UUID;

public record CreateOrderCommand(
    @NotNull(message = OrderValidationConstants.ASSIGNED_USER_ID_REQUIRED_MSG)
    UUID assignedUserId,

    @NotNull(message = OrderValidationConstants.CLIENT_ID_REQUIRED_MSG)
    UUID clientId,

    @NotNull(message = OrderValidationConstants.DEVICE_ID_REQUIRED_MSG)
    UUID deviceId,

    @NotBlank(message = OrderValidationConstants.ISSUE_DESCRIPTION_REQUIRED_MSG)
    @Size(max = OrderValidationConstants.ISSUE_DESCRIPTION_MAX_SIZE, message = OrderValidationConstants.ISSUE_DESCRIPTION_MAX_MSG)
    String issueDescription,

    @Size(max = OrderValidationConstants.TECHNICAL_DIAGNOSIS_MAX_SIZE, message = OrderValidationConstants.TECHNICAL_DIAGNOSIS_MAX_MSG)
    String technicalObservations,

    @Min(value = 0, message = OrderValidationConstants.ESTIMATED_COST_NEGATIVE_MSG)
    double estimatedCost
) {
}
