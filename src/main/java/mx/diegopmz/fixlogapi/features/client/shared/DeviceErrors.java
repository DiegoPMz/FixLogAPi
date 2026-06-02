package mx.diegopmz.fixlogapi.features.client.shared;

import mx.diegopmz.fixlogapi.common.errors.AppError;
import mx.diegopmz.fixlogapi.common.errors.ErrorTypes;


public final class DeviceErrors {
    private DeviceErrors() {
    }

    public static AppError serialNumberRequired() {
        return AppError.of(
            "The device serial number is required.",
            ErrorTypes.VALIDATION,
            "device.serial_number_required"
        );
    }

    public static AppError brandRequired() {
        return AppError.of(
            "The device brand is required.",
            ErrorTypes.VALIDATION,
            "device.brand_required"
        );
    }

    public static AppError modelRequired() {
        return AppError.of(
            "The device model is required.",
            ErrorTypes.VALIDATION,
            "device.model_required"
        );
    }

    public static AppError serialNumberTooShort() {
        return AppError.of(
            "The serial number is too short. It must be at least 4 characters long.",
            ErrorTypes.VALIDATION,
            "device.serial_number_too_short"
        );
    }
}
