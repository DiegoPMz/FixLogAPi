package mx.diegopmz.fixlogapi.unit.features.user.shared;

import mx.diegopmz.fixlogapi.features.user.shared.User;
import mx.diegopmz.fixlogapi.features.user.shared.UserEntity;
import mx.diegopmz.fixlogapi.features.user.shared.UserMapper;
import mx.diegopmz.fixlogapi.features.user.shared.UserRoles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTests {
    @Nested
    @DisplayName("Domain Model to Entity Mapping")
    class ModelToEntityTests {

        @Test
        @DisplayName("shouldMapAllFieldsToEntity_whenDomainModelIsValid")
        void shouldMapAllFieldsToEntity_whenDomainModelIsValid() {
            // Arrange
            User domain = User.create(
                "auth0|abc", "John", "Doe", "john@fixlog.com",
                UserRoles.TECHNICIAN
            ).getValue();

            // Act
            UserEntity entity = UserMapper.toEntity(domain);

            // Assert
            assertAll(
                () -> assertNotNull(entity),
                () -> assertEquals(domain.getId(), entity.getId()),
                () -> assertEquals("auth0|abc", entity.getExternalId()),
                () -> assertEquals("john@fixlog.com", entity.getEmail()),
                () -> assertEquals(UserRoles.TECHNICIAN, entity.getRole()),
                () -> assertEquals(domain.getCreatedAt(), entity.getCreatedAt())
            );
        }

        @Test
        @DisplayName("shouldReturnNull_whenDomainModelIsNull")
        void shouldReturnNull_whenDomainModelIsNull() {
            assertNull(UserMapper.toEntity(null));
        }
    }

    @Nested
    @DisplayName("Entity to Domain Model Mapping")
    class EntityToModelTests {

        @Test
        @DisplayName("shouldRehydrateDomainModel_whenEntityContainsData")
        void shouldRehydrateDomainModel_whenEntityContainsData() {
            // Arrange
            UUID userId = UUID.randomUUID();
            LocalDateTime created = LocalDateTime.now().minusDays(1);

            UserEntity entity = new UserEntity();
            entity.setId(userId);
            entity.setExternalId("auth0|123");
            entity.setName("Jane");
            entity.setLastName("Smith");
            entity.setEmail("jane@fixlog.com");
            entity.setRole(UserRoles.ADMIN);
            entity.setCreatedAt(created);

            // Act
            User domain = UserMapper.toDomain(entity);

            // Assert
            assertAll(
                () -> assertNotNull(domain),
                () -> assertEquals(userId, domain.getId()),
                () -> assertEquals("Jane", domain.getName()),
                () -> assertEquals(UserRoles.ADMIN, domain.getRole()),
                () -> assertEquals(created, domain.getCreatedAt())
            );
        }

        @Test
        @DisplayName("shouldReturnNull_whenEntityIsNull")
        void shouldReturnNull_whenEntityIsNull() {
            assertNull(UserMapper.toDomain(null));
        }
    }
}
