package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import org.springframework.data.domain.Page;

public interface IGetOrdersPageOverviewQueryHandler {
    Page<OrderSummaryReadModel> handle(GetOrdersPageOverviewQuery query);
}
