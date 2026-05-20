package mx.diegopmz.fixlogapi.features.client.shared;

import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.errors.ErrorTypes;

public final class ClientErrors {

    private ClientErrors() {
    }

    public static AppError nameRequired() {
        return AppError.of(
            "The client name is required and cannot be empty.",
            ErrorTypes.VALIDATION,
            "client.name_required"
        );
    }

    public static AppError phoneNumberRequired() {
        return AppError.of(
            "The client phone number is required.",
            ErrorTypes.VALIDATION,
            "client.phone_number_required"
        );
    }

    public static AppError invalidPhoneNumberLength(String phoneNumber) {
        String provided = phoneNumber != null ? String.valueOf(phoneNumber.length()) : "0";
        return AppError.of(
            "The phone number must be exactly 10 digits long. Provided length: " + provided + ".",
            ErrorTypes.VALIDATION,
            "client.invalid_phone_number_length"
        );
    }

    public static AppError invalidEmail(String email) {
        return AppError.of(
            "The email address '" + email + "' is not a valid format.",
            ErrorTypes.VALIDATION,
            "client.invalid_email"
        );
    }

    public static AppError deviceAlreadyRegistered(String serialNumber) {
        return AppError.of(
            "The device with serial number '" + serialNumber + "' is already registered to this client.",
            ErrorTypes.VALIDATION,
            "client.device_already_registered"
        );
    }
}
