package mx.diegopmz.fixlogapi.features.user.shared;

import mx.diegopmz.fixlogapi.common.domain.errors.AppError;
import mx.diegopmz.fixlogapi.common.domain.errors.ErrorTypes;

import java.util.Map;

public final class UserErrors {

    public static AppError invalidEmail(String email) {
        return AppError.of(
            "The email address '" + email + "' is not valid.",
            ErrorTypes.VALIDATION,
            "user.invalid_email"
        );
    }

    public static AppError externalIdRequired() {
        return AppError.of(
            "The external identity provider ID (Auth0 sub) is required.",
            ErrorTypes.VALIDATION,
            "user.external_id_required"
        );
    }

    public static AppError invalidName() {
        Map<String, String> metadata = Map.of(
            "minLength", "2",
            "maxLength", "100",
            "field", "name/lastName"
        );
        return AppError.of(
            "First name and last name must be between 2 and 100 characters.",
            "user.invalid_name_length",
            ErrorTypes.VALIDATION,
            metadata
        );
    }

    public static AppError roleRequired() {
        return AppError.of(
            "A valid user role must be assigned to the new user.",
            ErrorTypes.VALIDATION,
            "user.role_required"
        );
    }

    public static AppError adminCreationNotAllowed() {
        return AppError.of(
            "Creating an administrator requires special privileges.",
            ErrorTypes.FORBIDDEN,
            "user.admin_creation_forbidden"
        );
    }
}