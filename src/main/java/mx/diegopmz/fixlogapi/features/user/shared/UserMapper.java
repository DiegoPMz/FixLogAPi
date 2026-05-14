package mx.diegopmz.fixlogapi.features.user.shared;

public final class UserMapper {

    private UserMapper() {
    }

    /**
     * Transform the Domain Model into a Persistence Entity (JPA).
     */
    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        var entity = new UserEntity();

        entity.setId(domain.getId());
        entity.setExternalId(domain.getExternalId());
        entity.setName(domain.getName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setRole(domain.getRole());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }

    /**
     * Transform the Persistence Entity (JPA) back into a Domain Model.
     */
    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
            entity.getId(),
            entity.getExternalId(),
            entity.getEmail(),
            entity.getCreatedAt(),
            entity.getName(),
            entity.getLastName(),
            entity.getRole(),
            entity.getUpdatedAt()
        );
    }
}