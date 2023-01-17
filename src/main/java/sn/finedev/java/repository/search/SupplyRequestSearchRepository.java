package sn.finedev.java.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.SupplyRequest;
import sn.finedev.java.repository.SupplyRequestRepository;

/**
 * Spring Data Elasticsearch repository for the {@link SupplyRequest} entity.
 */
public interface SupplyRequestSearchRepository
    extends ElasticsearchRepository<SupplyRequest, Long>, SupplyRequestSearchRepositoryInternal {}

interface SupplyRequestSearchRepositoryInternal {
    Page<SupplyRequest> search(String query, Pageable pageable);

    Page<SupplyRequest> search(Query query);

    void index(SupplyRequest entity);
}

class SupplyRequestSearchRepositoryInternalImpl implements SupplyRequestSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final SupplyRequestRepository repository;

    SupplyRequestSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, SupplyRequestRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<SupplyRequest> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<SupplyRequest> search(Query query) {
        SearchHits<SupplyRequest> searchHits = elasticsearchTemplate.search(query, SupplyRequest.class);
        List<SupplyRequest> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(SupplyRequest entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
