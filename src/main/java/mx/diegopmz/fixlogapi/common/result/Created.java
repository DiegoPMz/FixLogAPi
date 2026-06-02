package mx.diegopmz.fixlogapi.common.result;

public record Created(String message) {

    public static Result<Created> result() {
        return Result.ok(new Created("Resource created successfully"));
    }
}
