package mx.diegopmz.fixlogapi.features.order.shared;

public class OrderValidationConstants {
    public static final String ISSUE_DESCRIPTION_REQUIRED_MSG = "La descripción de la falla es obligatoria.";
    public static final int ISSUE_DESCRIPTION_MAX_SIZE = 1000;
    public static final String ISSUE_DESCRIPTION_MAX_MSG = "La descripción de la falla no puede superar los 1000 caracteres.";

    public static final int TECHNICAL_DIAGNOSIS_MAX_SIZE = 2000;
    public static final String TECHNICAL_DIAGNOSIS_MAX_MSG = "El diagnóstico técnico no puede superar los 2000 caracteres.";

    public static final String ESTIMATED_COST_NEGATIVE_MSG = "El costo estimado no puede ser un valor negativo.";
    public static final String FINAL_COST_NEGATIVE_MSG = "El costo final no puede ser un valor negativo.";

    public static final String DEVICE_ID_REQUIRED_MSG = "El identificador del dispositivo es obligatorio.";

    public static final String CLIENT_ID_REQUIRED_MSG = "El identificador del cliente es obligatorio.";

    public static final String ASSIGNED_USER_ID_REQUIRED_MSG = "El identificador del técnico asignado es obligatorio.";

    private OrderValidationConstants() {
    }
}
