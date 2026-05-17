package mx.diegopmz.fixlogapi.features.user.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JpaUserWriteRepository implements IUserWriteRepository {
    private final EntityManager entityManager;

    public JpaUserWriteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(User user) {
        var entity = UserMapper.toEntity(user);
        entityManager.persist(entity);
    }

    @Override
    public void update(User user) {
        var entity = UserMapper.toEntity(user);
        entityManager.merge(entity);
    }

    @Override
    public void delete(UUID id) {
        UserEntity entity = entityManager.find(UserEntity.class, id);
        if (entity == null) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        entityManager.remove(entity);
    }
}
