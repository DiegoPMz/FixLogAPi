package mx.diegopmz.fixlogapi.common.domain.orderAggregate;

import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.errors.ErrorTypes;

public final class OrderErrors {

    private OrderErrors() {
    }

    // --- ERRORES DE CREACIÓN (FACTORY METHOD) ---
    public static AppError deviceRequired() {
        return AppError.of(
            "The device ID is mandatory to initiate a work order.",
            ErrorTypes.VALIDATION,
            "ORDER.DEVICE_REQUIRED"
        );
    }

    public static AppError descriptionRequired() {
        return AppError.of(
            "A detailed issue description must be provided.",
            ErrorTypes.VALIDATION,
            "ORDER.DESCRIPTION_REQUIRED"
        );
    }

    public static AppError invalidEstimatedCost(double providedCost) {
        var metadata = new java.util.HashMap<String, String>();
        metadata.put("provided_cost", String.valueOf(providedCost));
        metadata.put("min_allowed", "0.0");

        return AppError.of(
            "The estimated cost cannot be negative.",
            "ORDER.INVALID_ESTIMATED_COST",
            ErrorTypes.BAD_REQUEST,
            metadata
        );
    }

    // --- ERRORES DE FLUJO DE TRABAJO (ESTADOS) ---
    public static AppError invalidStatusTransition(String current, String target) {
        var metadata = new java.util.HashMap<String, String>();
        metadata.put("current_status", current);
        metadata.put("target_status", target);

        return AppError.of(
            "Cannot transition order from " + current + " to " + target + ".",
            "ORDER.INVALID_TRANSITION",
            ErrorTypes.CONFLICT,
            metadata
        );
    }

    public static AppError diagnosisRequiredForRepair() {
        return AppError.of(
            "Technical diagnosis must be filled before marking the order as repaired.",
            ErrorTypes.VALIDATION,
            "ORDER.DIAGNOSIS_REQUIRED"
        );
    }

    // --- ERRORES DE GARANTÍA ---
    public static AppError warrantyExpired(java.time.LocalDateTime expiryDate) {
        var metadata = new java.util.HashMap<String, String>();
        metadata.put("expiry_date", expiryDate.toString());

        return AppError.of(
            "The warranty for this device expired on " + expiryDate.toLocalDate() + ".",
            "ORDER.WARRANTY_EXPIRED",
            ErrorTypes.CONFLICT,
            metadata
        );
    }

    public static AppError invalidTechnician() {
        return AppError.of(
            "The assigned technician ID is invalid.",
            ErrorTypes.VALIDATION,
            "ORDER.INVALID_TECHNICIAN"
        );
    }

    public static AppError invalidFinalCost(double cost) {
        var metadata = new java.util.HashMap<String, String>();
        metadata.put("provided_final_cost", String.valueOf(cost));

        return AppError.of(
            "The final cost cannot be negative.",
            "ORDER.INVALID_FINAL_COST",
            ErrorTypes.BAD_REQUEST,
            metadata
        );
    }

    public static AppError diagnosisTooLong(int currentLength, int maxLength) {
        var metadata = new java.util.HashMap<String, String>();
        metadata.put("current_length", String.valueOf(currentLength));
        metadata.put("max_allowed_length", String.valueOf(maxLength));

        return AppError.of(
            "The technical diagnosis exceeds the maximum allowed length.",
            "ORDER.DIAGNOSIS_TOO_LONG",
            ErrorTypes.BAD_REQUEST,
            metadata
        );
    }

}