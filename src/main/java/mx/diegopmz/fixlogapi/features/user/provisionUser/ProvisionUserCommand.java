package mx.diegopmz.fixlogapi.features.user.provisionUser;

public record ProvisionUserCommand(
    String externalId,
    String email,
    String name,
    String lastName
) {
}
