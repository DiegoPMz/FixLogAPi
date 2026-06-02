package mx.diegopmz.fixlogapi.features.client.registerClient;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RegisterClientController.class)
@DisplayName("Register Client Controller Unit Tests")
class RegisterClientControllerTest {
    private final String API_URL = "/api/v1/clients";
    private final String VALID_NAME = "Diego Perez";
    private final String VALID_PHONE = "1234567890";
    private final String VALID_EMAIL = "diego@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRegisterClientCommandHandler commandHandler;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldReturnNoContent_whenClientIsSuccessfullyRegistered")
        void shouldReturnNoContent_whenClientIsSuccessfullyRegistered() throws Exception {
            // Arrange
            var command = new RegisterClientCommand(VALID_NAME, VALID_PHONE, VALID_EMAIL);
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
            var invalidCommand = new RegisterClientCommand("", "", "not-an-email");

            // Act & Assert
            mockMvc.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(ErrorTypes.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errors.phoneNumber").exists())
                .andExpect(jsonPath("$.errors.email").exists());
        }

        @Test
        @DisplayName("shouldReturnBadRequest_whenDomainBusinessRulesFail")
        void shouldReturnBadRequest_whenDomainBusinessRulesFail() throws Exception {
            // Arrange
            var command = new RegisterClientCommand(VALID_NAME, "12345", VALID_EMAIL);

            var domainError = AppError.of("El teléfono debe tener exactamente 10 dígitos.", ErrorTypes.BAD_REQUEST, null);
            when(commandHandler.handle(any())).thenReturn(Result.failure(domainError));

            // Act & Assert
            mockMvc.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.detail").value("El teléfono debe tener exactamente 10 dígitos."));
        }
    }
}