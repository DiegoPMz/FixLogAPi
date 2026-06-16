package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IOrderOverviewRepository {
    Page<OrderSummaryReadModel> queryAllBy(Pageable pageable);
}


