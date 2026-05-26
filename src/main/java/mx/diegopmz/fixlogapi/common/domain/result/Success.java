package mx.diegopmz.fixlogapi.common.domain.result;

public record Success(String message) {
    public static Result<Success> result() {
        return Result.ok(new Success("Operation Succeeded"));
    }
}
