package mx.diegopmz.fixlogapi.features.client.getPageOverview;

import org.springframework.data.domain.Pageable;

public record GetClientPageOverviewQuery(
    String search,
    Pageable pageable
) {
}
