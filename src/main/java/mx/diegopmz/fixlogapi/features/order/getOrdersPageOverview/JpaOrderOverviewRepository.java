package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaOrderOverviewRepository implements IOrderOverviewRepository {
    private static final Map<String, SortTarget> SORT_TRANSLATION_MAP = Map.of(
        "createdAt", new SortTarget("createdAt", false),
        "price", new SortTarget("price", false),
        "ownerName", new SortTarget("deviceOwnerName", true),
        "model", new SortTarget("model", true),
        "status", new SortTarget("status", true),
        "priority", new SortTarget("priority", true)
    );
    private final EntityManager entityManager;

    @Override
    public Page<OrderSummaryReadModel> queryAllBy(Pageable pageable) {
        String jpql = "SELECT o FROM OrderSummaryViewEntity o";
        jpql += convertSortToJpql(pageable.getSort());

        List<OrderSummaryViewEntity> entities = entityManager.createQuery(
                jpql, OrderSummaryViewEntity.class
            )
            .setHint("org.hibernate.readOnly", true)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        List<OrderSummaryReadModel> content = entities.stream()
            .map(OrderSummaryReadModel::fromEntity)
            .toList();

        Long total = entityManager.createQuery("SELECT COUNT(o) FROM OrderSummaryViewEntity o", Long.class)
            .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * Helper method to translate Sort object from Spring to JPQL syntax (e.g., "o.status ASC, o.createdAt DESC")
     * "o" = ...FROM OrderSummaryViewEntity o
     */
    private String convertSortToJpql(Sort sort) {
        if (sort.isUnsorted()) {
            return " ORDER BY o.createdAt DESC";
        }

        String orderByClause = sort.stream()
            .map(order -> {
                String frontendProperty = order.getProperty();


                // TODO: logging
                SortTarget target = SORT_TRANSLATION_MAP.getOrDefault(
                    frontendProperty,
                    new SortTarget("createdAt", false)
                );

                String direction = order.getDirection().name();

                if (target.isTextField()) {
                    return String.format("LOWER(o.%s) %s", target.dbField(), direction);
                } else {
                    return String.format("o.%s %s", target.dbField(), direction);
                }
            })
            .collect(Collectors.joining(", "));

        return " ORDER BY " + orderByClause;
    }

    private record SortTarget(String dbField, boolean isTextField) {
    }
}
