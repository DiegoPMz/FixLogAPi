package mx.diegopmz.fixlogapi.features.client.shared;

public final class ClientValidationConstants {
    public static final String NAME_REQUIRED_MSG = "El nombre es obligatorio.";
    public static final int NAME_MAX_SIZE = 150;
    public static final String NAME_MAX_MSG = "El nombre no puede superar los 150 caracteres.";

    public static final String PHONE_REQUIRED_MSG = "El número de teléfono es obligatorio.";
    public static final int PHONE_MAX_SIZE = 20;
    public static final String PHONE_MAX_MSG = "El número de teléfono es demasiado largo.";

    public static final String EMAIL_REQUIRED_MSG = "El correo electrónico es obligatorio.";
    public static final int EMAIL_MAX_SIZE = 255;
    public static final String EMAIL_MAX_MSG = "El correo electrónico no puede superar los 255 caracteres.";

    private ClientValidationConstants() {
    }
}
