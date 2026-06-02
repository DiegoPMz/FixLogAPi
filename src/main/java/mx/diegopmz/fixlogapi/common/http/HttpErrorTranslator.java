package mx.diegopmz.fixlogapi.common.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mx.diegopmz.fixlogapi.common.errors.AppError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpErrorTranslator {
    public static ResponseEntity<ProblemDetail> handle(AppError error) {

        HttpStatus status = switch (error.type()) {
            case VALIDATION, BAD_REQUEST, CONFLICT, INTERNAL, TIMEOUT, SERVICE_UNAVAILABLE -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, error.message());

        problemDetail.setTitle(error.type().name());

        if (error.metadata() != null) {
            problemDetail.setProperty("errors", error.metadata());
        }

        return ResponseEntity.status(status).body(problemDetail);
    }
}
