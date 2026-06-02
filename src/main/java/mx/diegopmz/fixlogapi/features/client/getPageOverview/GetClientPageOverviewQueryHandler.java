package mx.diegopmz.fixlogapi.features.client.getPageOverview;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetClientPageOverviewQueryHandler implements IGetClientPageOverviewQueryHandler {
    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final IJpaClientSummaryRepository clientSummaryRepository;

    @Override
    public Page<ClientSummaryReadModel> handle(GetClientPageOverviewQuery query) {
        var queryPageable = query.pageable();

        int validatedPage = Math.max(0, queryPageable.getPageNumber());

        int validatedSize = (queryPageable.getPageSize() <= 0 || queryPageable.getPageSize() > MAX_PAGE_SIZE)
            ? DEFAULT_PAGE_SIZE
            : queryPageable.getPageSize();

        Sort sortByField = !queryPageable.getSort().isEmpty()
            ? queryPageable.getSort()
            : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageable = PageRequest.of(validatedPage, validatedSize, sortByField);

        return clientSummaryRepository.queryAllBy(pageable);
    }
}
