package mx.diegopmz.fixlogapi.features.client.getPageOverview;

import jakarta.persistence.EntityManager;
import mx.diegopmz.fixlogapi.BaseIntegrationTest;
import mx.diegopmz.fixlogapi.features.client.shared.Client;
import mx.diegopmz.fixlogapi.features.client.shared.IClientWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Get Client Page Overview Query Handler Integration Tests")
public class GetClientPageOverviewQueryHandlerIT extends BaseIntegrationTest {

    @Autowired
    private GetClientPageOverviewQueryHandler queryHandler;

    @Autowired
    private IClientWriteRepository writeRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        refreshMaterializedView();
    }

    /**
     * Helper method needed to synchronized PostgreSQL's materialized view
     * with the new inserted data in the 'arrange' block
     */
    @Transactional
    protected void refreshMaterializedView() {
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW client_summary_mview").executeUpdate();
    }

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldReturnPagedClients_whenQueryContainsValidPagingParameters")
        void shouldReturnPagedClients_whenQueryContainsValidPagingParameters() {
            // Arrange
            Client client1 = Client.create("Ana Rodríguez", "5551112222", "ana@example.com").getValue();
            Client client2 = Client.create("Carlos López", "5553334444", "carlos@example.com").getValue();
            Client client3 = Client.create("María García", "5555556666", "maria@example.com").getValue();

            writeRepository.save(client1);
            writeRepository.save(client2);
            writeRepository.save(client3);

            entityManager.flush();
            refreshMaterializedView();

            var query = new GetClientPageOverviewQuery(
                null,
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"))
            );

            // Act
            Page<ClientSummaryReadModel> result = queryHandler.handle(query);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(2);

            // ascendant order
            assertThat(result.getContent().get(0).name()).isEqualTo("Ana Rodríguez");
            assertThat(result.getContent().get(1).name()).isEqualTo("Carlos López");
        }

        @Test
        @DisplayName("shouldReturnEmptyPage_whenPageOffsetExceedsTotalRecords")
        void shouldReturnEmptyPage_whenPageOffsetExceedsTotalRecords() {
            // Arrange
            Client client = Client.create("Pedro Sánchez", "5557778888", "pedro@example.com").getValue();
            writeRepository.save(client);

            entityManager.flush();
            refreshMaterializedView();

            var query = new GetClientPageOverviewQuery(
                null,
                PageRequest.of(5, 10, Sort.by(Sort.Direction.ASC, "name"))
            );

            // Act
            Page<ClientSummaryReadModel> result = queryHandler.handle(query);

            // Assert
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("shouldFallbackToDefaultSize_whenQuerySizeExceedsMaximumAllowed")
        void shouldFallbackToDefaultSize_whenQuerySizeExceedsMaximumAllowed() {
            // Arrange
            for (int i = 1; i <= 15; i++) {
                Client client = Client.create("Cliente " + i, "55500000" + (i < 10 ? "0" + i : i), "cliente" + i + "@example.com").getValue();
                writeRepository.save(client);
            }

            entityManager.flush();
            refreshMaterializedView();

            var query = new GetClientPageOverviewQuery(
                null,
                PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "name"))
            );

            // Act
            Page<ClientSummaryReadModel> result = queryHandler.handle(query);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(15);

            assertThat(result.getContent()).hasSize(10);
        }

        @Test
        @DisplayName("shouldOrderByCreatedAtDescending_whenNoSortByParameterIsProvided")
        void shouldOrderByCreatedAtDescending_whenNoSortByParameterIsProvided() throws InterruptedException {
            // Arrange
            Client oldestClient = Client.create("Ana Rodríguez", "5551112222", "ana@example.com").getValue();
            writeRepository.save(oldestClient);

            Thread.sleep(10);
            Client intermediateClient = Client.create("Carlos López", "5553334444", "carlos@example.com").getValue();
            writeRepository.save(intermediateClient);

            Thread.sleep(10);
            Client newestClient = Client.create("María García", "5555556666", "maria@example.com").getValue();
            writeRepository.save(newestClient);

            entityManager.flush();
            refreshMaterializedView();

            var query = new GetClientPageOverviewQuery(
                null,
                PageRequest.of(0, 10)
            );

            // Act
            Page<ClientSummaryReadModel> result = queryHandler.handle(query);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);

            assertThat(result.getContent().get(0).name())
                .isEqualTo("María García");

            assertThat(result.getContent().get(1).name())
                .isEqualTo("Carlos López");

            assertThat(result.getContent().get(2).name())
                .isEqualTo("Ana Rodríguez");
        }
    }

}