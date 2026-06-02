package mx.diegopmz.fixlogapi.features.client.getPageOverview;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clients/dashboard")
@RequiredArgsConstructor
public class GetClientPageOverviewController {
    private final IGetClientPageOverviewQueryHandler queryHandler;

    @GetMapping
    public ResponseEntity<Page<ClientSummaryReadModel>> getPageOverview(
        @RequestParam(required = false) String search,
        @PageableDefault(size = 10, sort = "totalSpent") Pageable pageable
    ) {
        var query = new GetClientPageOverviewQuery(search, pageable);
        Page<ClientSummaryReadModel> result = queryHandler.handle(query);

        return ResponseEntity.ok(result);
    }
}
