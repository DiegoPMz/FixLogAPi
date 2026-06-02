package mx.diegopmz.fixlogapi.features.client.getPageOverview;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

@WebMvcTest(GetClientPageOverviewController.class)
@DisplayName("Get Client Page Overview Controller Unit Tests")
public class GetClientPageOverviewControllerTest {
    private final String API_URL = "/api/v1/clients/dashboard";

    private final ArgumentCaptor<GetClientPageOverviewQuery> queryCaptor =
        ArgumentCaptor.forClass(GetClientPageOverviewQuery.class);

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private IGetClientPageOverviewQueryHandler queryHandler;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessCases {

        @Test
        @DisplayName("shouldReturnPagedClientsAnd200OK_whenRequestParametersAreValid")
        void shouldReturnPagedClientsAnd200OK_whenRequestParametersAreValid() throws Exception {
            // Arrange
            var mockModel = new ClientSummaryReadModel(
                UUID.randomUUID(),
                "John Doe",
                "john.doe@example.com",
                "5551234567",
                5L,
                1500.00,
                LocalDateTime.now()
            );

            Page<ClientSummaryReadModel> mockPage = new PageImpl<>(
                List.of(mockModel),
                PageRequest.of(0, 10), 1
            );

            when(queryHandler.handle(any(GetClientPageOverviewQuery.class))).thenReturn(mockPage);

            // Act & Assert
            mockMvc.perform(get(API_URL)
                    .param("search", "John")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "name,asc")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.content[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

            verify(queryHandler).handle(queryCaptor.capture());
            GetClientPageOverviewQuery capturedQuery = queryCaptor.getValue();

            assertThat(capturedQuery.search()).isEqualTo("John");
            assertThat(capturedQuery.pageable().getPageNumber()).isEqualTo(0);
            assertThat(capturedQuery.pageable().getPageSize()).isEqualTo(10);
            assertThat(capturedQuery.pageable().getSort().getOrderFor("name").getDirection().isAscending()).isTrue();
        }

        @Test
        @DisplayName("shouldUseDefaultPagingParameters_whenNoParametersAreProvidedInUrl")
        void shouldUseDefaultPagingParameters_whenNoParametersAreProvidedInUrl() throws Exception {
            // Arrange
            Page<ClientSummaryReadModel> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            when(queryHandler.handle(any(GetClientPageOverviewQuery.class))).thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get(API_URL)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

            verify(queryHandler).handle(queryCaptor.capture());
            GetClientPageOverviewQuery capturedQuery = queryCaptor.getValue();

            assertThat(capturedQuery.search()).isNull();
            assertThat(capturedQuery.pageable()).isNotNull();
            assertThat(capturedQuery.pageable().getPageSize()).isEqualTo(10);
        }
    }
}