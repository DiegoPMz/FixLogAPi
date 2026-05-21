package mx.diegopmz.fixlogapi.features.client.registerClient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static mx.diegopmz.fixlogapi.features.client.shared.ClientValidationConstants.*;

public record RegisterClientCommand(
    @NotBlank(message = "phoneNumber:" + PHONE_REQUIRED_MSG)
    @Size(max = PHONE_MAX_SIZE, message = "phoneNumber:" + PHONE_MAX_MSG)
    String phoneNumber,

    @NotBlank(message = "email:" + EMAIL_REQUIRED_MSG)
    @Size(max = EMAIL_MAX_SIZE, message = "email:" + EMAIL_MAX_MSG)
    String email,

    @NotBlank(message = "name:" + NAME_REQUIRED_MSG)
    @Size(max = NAME_MAX_SIZE, message = "name:" + NAME_MAX_MSG)
    String name
) {
}
