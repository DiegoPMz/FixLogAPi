package mx.diegopmz.fixlogapi.common.result;

import mx.diegopmz.fixlogapi.common.errors.AppError;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Result<Tvalue> {
    private final Tvalue value;
    private final List<AppError> errors;

    private Result(Tvalue value, List<AppError> errors) {
        this.value = value;
        this.errors = errors;
    }

    public static <U> Result<U> ok(U value) {
        return new Result<>(value, Collections.emptyList());
    }

    public static <U> Result<U> failure(List<AppError> errors) {
        return new Result<>(null, errors);
    }

    public static <U> Result<U> failure(AppError error) {
        return new Result<>(null, List.of(error));
    }

    public Tvalue getValue() {
        return value;
    }

    public List<AppError> getErrors() {
        return errors;
    }

    public Optional<AppError> firstError() {
        return errors.isEmpty()
            ? Optional.empty()
            : Optional.of(this.errors.get(0));
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }
}
