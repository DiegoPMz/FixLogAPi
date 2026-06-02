package mx.diegopmz.fixlogapi.common.errors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record AppError(
    String message,
    String code,
    ErrorTypes type,
    Map<String, String> metadata
) {
    public AppError {
        if (metadata == null) {
            metadata = Collections.emptyMap();
        }
        metadata = Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    public static AppError of(String message, ErrorTypes type, String code) {
        return new AppError(message, code, type, Collections.emptyMap());
    }

    public static AppError of(String message, String code, ErrorTypes type, Map<String, String> metadata) {
        return new AppError(message, code, type, metadata);
    }
}