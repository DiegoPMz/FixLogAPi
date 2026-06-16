package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders/dashboard")
@RequiredArgsConstructor
public class GetOrdersPageOverviewController {
    private final IGetOrdersPageOverviewQueryHandler queryHandler;

    @GetMapping
    public ResponseEntity<Page<OrderSummaryReadModel>> getPageOverview(
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        var query = new GetOrdersPageOverviewQuery(pageable);
        Page<OrderSummaryReadModel> result = queryHandler.handle(query);

        return ResponseEntity.ok(result);
    }
}
