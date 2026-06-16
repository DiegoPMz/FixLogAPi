package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import org.springframework.data.domain.Pageable;

public record GetOrdersPageOverviewQuery(
    Pageable pageable
) {
}
