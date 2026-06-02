package mx.diegopmz.fixlogapi.features.client.getPageOverview;

import org.springframework.data.domain.Page;

public interface IGetClientPageOverviewQueryHandler {
    Page<ClientSummaryReadModel> handle(GetClientPageOverviewQuery query);
};

