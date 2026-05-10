package mx.diegopmz.fixlogapi.common.domain.result;

public record Updated(String message) {
    public static Result<Updated> result() {
        return Result.ok(new Updated("Resource updated successfully"));
    }
}