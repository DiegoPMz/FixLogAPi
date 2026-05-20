package mx.diegopmz.fixlogapi.features.client.registerClient;

public record RegisterClientCommand(
    String phoneNumber,
    String email,
    String name
) {
}
