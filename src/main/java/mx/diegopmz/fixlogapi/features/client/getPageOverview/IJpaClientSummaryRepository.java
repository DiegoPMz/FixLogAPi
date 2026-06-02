package mx.diegopmz.fixlogapi.features.client.getPageOverview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IJpaClientSummaryRepository extends JpaRepository<ClientSummaryViewEntity, UUID> {
    Page<ClientSummaryReadModel> queryAllBy(Pageable pageable);
}
