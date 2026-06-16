package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import mx.diegopmz.fixlogapi.features.order.shared.OrderPriority;
import mx.diegopmz.fixlogapi.features.order.shared.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GetOrdersPageOverviewController.class)
@DisplayName("Get Orders Page Overview Controller Unit Tests")
public class GetOrdersPageOverviewControllerTest {
    private final String API_URL = "/api/v1/orders/dashboard";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IGetOrdersPageOverviewQueryHandler queryHandler;

    @Test
    @DisplayName("Should return 200 OK and paginated data when valid parameters are provided")
    void shouldReturnOrdersPage_whenValidParametersAreProvided() throws Exception {
        // Arrange
        var mockModel = new OrderSummaryReadModel(
            UUID.randomUUID(),
            "#001",
            OrderStatus.RECIBIDO,
            OrderPriority.BAJA,
            false,
            150.0,
            LocalDateTime.now(),
            new OrderSummaryReadModel.OrderOverviewDevice(
                UUID.randomUUID(),
                "iPhone 13 Pro",
                "iPhone",
                "23432156321g324",
                "María García"
            )
        );


        Page<OrderSummaryReadModel> mockPage = new PageImpl<>(List.of(mockModel));

        when(queryHandler.handle(any(GetOrdersPageOverviewQuery.class))).thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get(API_URL)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "model,asc")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].ticketNumber").value("#001"))
            .andExpect(jsonPath("$.content[0].device.model").value("iPhone 13 Pro"))
            .andExpect(jsonPath("$.totalElements").value(1));

        ArgumentCaptor<GetOrdersPageOverviewQuery> queryCaptor = ArgumentCaptor.forClass(GetOrdersPageOverviewQuery.class);
        verify(queryHandler).handle(queryCaptor.capture());

        Pageable capturedPageable = queryCaptor.getValue().pageable();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
        assertThat(capturedPageable.getSort().getOrderFor("model").isAscending()).isTrue();
    }

    @Test
    @DisplayName("Should pass default pagination parameters to handler when query params are missing")
    void shouldPassDefaultPaginationToHandler_whenNoParamsAreProvided() throws Exception {
        // Arrange
        Page<OrderSummaryReadModel> emptyPage = new PageImpl<>(List.of());
        when(queryHandler.handle(any(GetOrdersPageOverviewQuery.class))).thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get(API_URL)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // @PageableDefault
        ArgumentCaptor<GetOrdersPageOverviewQuery> queryCaptor = ArgumentCaptor.forClass(GetOrdersPageOverviewQuery.class);
        verify(queryHandler).handle(queryCaptor.capture());

        Pageable capturedPageable = queryCaptor.getValue().pageable();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
        assertThat(capturedPageable.getSort().stream().toList())
            .hasSize(1)
            .first()
            .satisfies(order -> {
                assertThat(order.getProperty()).isEqualTo("createdAt");
                assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
            });
    }

    @Test
    @DisplayName("Should forward malicious sort input to handler to let architecture handle security safely")
    void shouldForwardMaliciousSortInputToHandler_whenSqlInjectionIsAttempted() throws Exception {
        // Arrange
        Page<OrderSummaryReadModel> emptyPage = new PageImpl<>(List.of());
        when(queryHandler.handle(any(GetOrdersPageOverviewQuery.class))).thenReturn(emptyPage);

        String maliciousSort = "createdAt; DROP TABLE orders; --,asc";

        // Act & Assert
        mockMvc.perform(get(API_URL)
                .param("sort", maliciousSort)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        ArgumentCaptor<GetOrdersPageOverviewQuery> queryCaptor = ArgumentCaptor.forClass(GetOrdersPageOverviewQuery.class);
        verify(queryHandler).handle(queryCaptor.capture());

        Pageable capturedPageable = queryCaptor.getValue().pageable();
        assertThat(capturedPageable.getSort().toString()).contains("createdAt; DROP TABLE orders; --");
    }
}
