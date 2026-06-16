package mx.diegopmz.fixlogapi.features.order.getOrdersPageOverview;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetOrdersPageOverviewQueryHandler implements IGetOrdersPageOverviewQueryHandler {
    private static final int MAX_PAGE_SIZE = 30;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
        "createdAt", "model", "status", "priority", "price", "ownerName"
    );

    private final IOrderOverviewRepository orderOverviewRepository;

    @Override
    public Page<OrderSummaryReadModel> handle(GetOrdersPageOverviewQuery query) {
        Pageable incomingPageable = query.pageable();

        int page = Math.max(0, incomingPageable.getPageNumber());

        int size = (incomingPageable.getPageSize() <= 0 || incomingPageable.getPageSize() > MAX_PAGE_SIZE)
            ? DEFAULT_PAGE_SIZE
            : incomingPageable.getPageSize();

        Sort sort = incomingPageable.getSort().isSorted()
            ? incomingPageable.getSort()
            : Sort.by(Sort.Direction.DESC, "createdAt");

        boolean isValidSort = sort.stream()
            .allMatch(order -> ALLOWED_SORT_PROPERTIES.contains(order.getProperty()));

        if (!isValidSort) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable sanitizedPageable = PageRequest.of(page, size, sort);

        return orderOverviewRepository.queryAllBy(sanitizedPageable);
    }
}
