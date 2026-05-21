package mx.diegopmz.fixlogapi.common.http;

import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.errors.ErrorTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Http Error Translator Unit Tests")
class HttpErrorTranslatorTest {

    private final String VALID_MESSAGE = "El recurso solicitado no fue encontrado.";
    private final String VALID_CODE = "CLIENT_NOT_FOUND";

    @Nested
    @DisplayName("Mapping Scenarios")
    class MappingCases {

        @Test
        @DisplayName("shouldMapToNotFoundProblemDetail_whenErrorTypeIsNotFound")
        void shouldMapToNotFoundProblemDetail_whenErrorTypeIsNotFound() {
            // Arrange
            var appError = AppError.of(VALID_MESSAGE, ErrorTypes.NOT_FOUND, VALID_CODE);

            // Act
            ResponseEntity<?> response = HttpErrorTranslator.handle(appError);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);

            ProblemDetail problemDetail = (ProblemDetail) response.getBody();
            assertThat(problemDetail).isNotNull();

            assertThat(problemDetail.getTitle()).isEqualTo("NOT_FOUND");
            assertThat(problemDetail.getDetail()).isEqualTo(VALID_MESSAGE);
        }

        @Test
        @DisplayName("shouldMapToBadRequestProblemDetail_whenErrorTypeIsBadRequest")
        void shouldMapToBadRequestProblemDetail_whenErrorTypeIsBadRequest() {
            // Arrange
            AppError appError = AppError.of("Datos inválidos", ErrorTypes.BAD_REQUEST, "INVALID_DATA");

            // Act
            ResponseEntity<?> response = HttpErrorTranslator.handle(appError);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            ProblemDetail problemDetail = (ProblemDetail) response.getBody();
            assertThat(problemDetail).isNotNull();
            assertThat(problemDetail.getTitle()).isEqualTo("BAD_REQUEST");
        }
    }
}
