package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import mx.diegopmz.fixlogapi.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Get Orders Page Overview Query Handler Integration Tests")
@Sql(
    scripts = "/seeds/orders-overview-seed.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
    config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
public class GetOrdersPageOverviewQueryHandlerIT extends BaseIntegrationTest {
    @Autowired
    private GetOrdersPageOverviewQueryHandler queryHandler;

    @Test
    @DisplayName("Should return paginated orders sorted by creation date descending by default")
    void shouldReturnPaginatedOrdersSortedByCreationDateDescending_whenDefaultSortIsApplied() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        var query = new GetOrdersPageOverviewQuery(pageable);

        // Act
        Page<OrderSummaryReadModel> result = queryHandler.handle(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(3);

        assertThat(result.getContent().get(0).ticketNumber()).isEqualTo("#001");
        assertThat(result.getContent().get(1).ticketNumber()).isEqualTo("#002");
        assertThat(result.getContent().get(2).ticketNumber()).isEqualTo("#004");
    }

    @Test
    @DisplayName("Should prevent SQL Injection attack by falling back to default sort when malicious payload is provided")
    void shouldPreventSqlInjection_whenMaliciousSortPropertyIsRequested() {
        String maliciousPayload = "createdAt; DROP TABLE orders; --";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(maliciousPayload).ascending());
        var query = new GetOrdersPageOverviewQuery(pageable);

        // Act
        Page<OrderSummaryReadModel> result = queryHandler.handle(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);

        assertThat(result.getContent().get(0).ticketNumber()).isEqualTo("#001");
        assertThat(result.getContent().get(1).ticketNumber()).isEqualTo("#002");
        assertThat(result.getContent().get(2).ticketNumber()).isEqualTo("#004");
    }

    @Test
    @DisplayName("Should respect pagination size parameters strictly")
    void shouldRestrictContentSizeToPageSizeParameter_whenQueryIsExecuted() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        var query = new GetOrdersPageOverviewQuery(pageable);

        // Act
        Page<OrderSummaryReadModel> result = queryHandler.handle(query);

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Should navigate to second page correctly")
    void shouldNavigateToTheSecondPageCorrectly_whenPageIndexIsOne() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 2);
        var query = new GetOrdersPageOverviewQuery(pageable);

        // Act
        Page<OrderSummaryReadModel> result = queryHandler.handle(query);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).ticketNumber()).isEqualTo("#004");
        assertThat(result.hasPrevious()).isTrue();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Should sort dynamically by price ascending when requested")
    void shouldSortOrdersByPriceAscending_whenSortByPriceAscendingIsRequested() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("price").ascending());
        var query = new GetOrdersPageOverviewQuery(pageable);

        // Act
        Page<OrderSummaryReadModel> result = queryHandler.handle(query);

        // Assert
        // $0 -> $150 -> $280
        assertThat(result.getContent().get(0).ticketNumber()).isEqualTo("#004");
        assertThat(result.getContent().get(1).ticketNumber()).isEqualTo("#001");
        assertThat(result.getContent().get(2).ticketNumber()).isEqualTo("#002");
    }

    @Test
    @DisplayName("Should return empty content with total elements count when page index is out of bounds")
    void shouldReturnEmptyContentWithTotalCount_whenPageIndexIsOutOfBounds() {
        // Arrange
        Pageable pageable = PageRequest.of(5, 10);
        var query = new GetOrdersPageOverviewQuery(pageable);

        // Act
        Page<OrderSummaryReadModel> result = queryHandler.handle(query);

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}
