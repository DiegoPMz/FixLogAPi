package mx.diegopmz.fixlogapi.common.result;

public record Deleted(String message) {
    public static Result<Deleted> result() {
        return Result.ok(new Deleted("Resource deleted successfully"));
    }
}
