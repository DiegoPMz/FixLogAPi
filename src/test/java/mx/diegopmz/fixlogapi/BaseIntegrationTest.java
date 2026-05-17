package mx.diegopmz.fixlogapi;

import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
public abstract class BaseIntegrationTest {
    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:18-alpine");
}
