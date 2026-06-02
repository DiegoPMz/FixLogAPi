package mx.diegopmz.fixlogapi.features.order.createOrder;

import mx.diegopmz.fixlogapi.common.errors.AppError;
import mx.diegopmz.fixlogapi.common.errors.ErrorTypes;
import mx.diegopmz.fixlogapi.common.result.Created;
import mx.diegopmz.fixlogapi.common.result.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreateOrderController.class)
@DisplayName("Create Order Controller Unit Tests")
class CreateOrderControllerTest {

    private final String API_URL = "/api/v1/orders";

    private final UUID VALID_CLIENT_ID = UUID.randomUUID();
    private final UUID VALID_DEVICE_ID = UUID.randomUUID();
    private final UUID VALID_TECHNICIAN_ID = UUID.randomUUID();
    private final String VALID_DESCRIPTION = "La pantalla parpadea en verde.";
    private final String VALID_OBSERVATIONS = "Se observa golpe en la esquina inferior.";
    private final double VALID_ESTIMATED_COST = 120.50;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICreateOrderCommandHandler commandHandler;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldReturnNoContent_whenOrderIsSuccessfullyCreated")
        void shouldReturnNoContent_whenOrderIsSuccessfullyCreated() throws Exception {
            // Arrange
            var command = new CreateOrderCommand(
                VALID_TECHNICIAN_ID,
                VALID_CLIENT_ID,
                VALID_DEVICE_ID,
                VALID_DESCRIPTION,
                VALID_OBSERVATIONS,
                VALID_ESTIMATED_COST
            );

            when(commandHandler.handle(any())).thenReturn(Created.result());

            // Act & Assert
            mockMvc.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Failure Scenarios")
    class FailureCases {

        @Test
        @DisplayName("shouldReturnBadRequest_whenRequestBodyIsInvalid")
        void shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
            // Arrange
            var invalidCommand = new CreateOrderCommand(
                VALID_TECHNICIAN_ID,
                null,
                null,
                "   ",
                VALID_OBSERVATIONS,
                -50.0
            );

            // Act & Assert
            mockMvc.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(ErrorTypes.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errors.clientId").exists())
                .andExpect(jsonPath("$.errors.deviceId").exists())
                .andExpect(jsonPath("$.errors.issueDescription").exists())
                .andExpect(jsonPath("$.errors.estimatedCost").exists());
        }

        @Test
        @DisplayName("shouldReturnBadRequest_whenDomainBusinessRulesFail")
        void shouldReturnBadRequest_whenDomainBusinessRulesFail() throws Exception {
            // Arrange
            var command = new CreateOrderCommand(
                VALID_TECHNICIAN_ID,
                VALID_CLIENT_ID,
                VALID_DEVICE_ID,
                VALID_DESCRIPTION,
                VALID_OBSERVATIONS,
                VALID_ESTIMATED_COST
            );

            var domainError = AppError.of("El dispositivo no pertenece al cliente especificado.", ErrorTypes.BAD_REQUEST, null);
            when(commandHandler.handle(any())).thenReturn(Result.failure(domainError));

            // Act & Assert
            mockMvc.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.detail").value("El dispositivo no pertenece al cliente especificado."));
        }
    }
}
