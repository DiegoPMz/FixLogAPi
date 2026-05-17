package mx.diegopmz.fixlogapi.features.user.shared;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserReadRepository implements IUserReadRepository {
    private final EntityManager entityManager;

    public JpaUserReadRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class)
            .setParameter("email", email)
            .setHint("org.hibernate.readOnly", true)
            .getSingleResult();

        return count > 0;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UserEntity entity = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
            .setParameter("email", email)
            .setHint("org.hibernate.readOnly", true)
            .getSingleResultOrNull();

        return Optional.ofNullable(entity).map(UserMapper::toDomain);
    }
}
