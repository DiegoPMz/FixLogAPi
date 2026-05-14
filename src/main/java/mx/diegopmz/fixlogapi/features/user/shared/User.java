package mx.diegopmz.fixlogapi.features.user.shared;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mx.diegopmz.fixlogapi.common.domain.result.Result;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class User {
    private final UUID id;
    private final String externalId;
    private final String email;
    private final LocalDateTime createdAt;
    private String name;
    private String lastName;
    private UserRoles role;
    private LocalDateTime updatedAt;

    private User(String externalId, String name, String lastName, String email, UserRoles role) {
        this.externalId = externalId;
        this.name = name;
        this.role = role;
        this.lastName = lastName;
        this.email = email;

        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Result<User> create(
        String externalId,
        String name,
        String lastName,
        String email,
        UserRoles role
    ) {
        if (externalId == null || externalId.isBlank()) {
            return Result.failure(UserErrors.externalIdRequired());
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return Result.failure(UserErrors.invalidEmail(email));
        }

        if (isInvalidString(name) || isInvalidString(lastName)) {
            return Result.failure(UserErrors.invalidName());
        }

        if (role == null) {
            return Result.failure(UserErrors.roleRequired());
        }

        if (role == UserRoles.ADMIN) {
            return Result.failure(UserErrors.adminCreationNotAllowed());
        }

        return Result.ok(new User(
            externalId,
            name.trim(),
            lastName.trim(),
            email.toLowerCase().trim(),
            role
        ));
    }

    private static boolean isInvalidString(String value) {
        return value == null || value.trim().length() < 2 || value.trim().length() > 100;
    }
}
