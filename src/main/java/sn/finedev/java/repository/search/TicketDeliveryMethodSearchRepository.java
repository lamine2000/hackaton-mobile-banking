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
import sn.finedev.java.domain.TicketDeliveryMethod;
import sn.finedev.java.repository.TicketDeliveryMethodRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TicketDeliveryMethod} entity.
 */
public interface TicketDeliveryMethodSearchRepository
    extends ElasticsearchRepository<TicketDeliveryMethod, Long>, TicketDeliveryMethodSearchRepositoryInternal {}

interface TicketDeliveryMethodSearchRepositoryInternal {
    Page<TicketDeliveryMethod> search(String query, Pageable pageable);

    Page<TicketDeliveryMethod> search(Query query);

    void index(TicketDeliveryMethod entity);
}

class TicketDeliveryMethodSearchRepositoryInternalImpl implements TicketDeliveryMethodSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TicketDeliveryMethodRepository repository;

    TicketDeliveryMethodSearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        TicketDeliveryMethodRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TicketDeliveryMethod> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<TicketDeliveryMethod> search(Query query) {
        SearchHits<TicketDeliveryMethod> searchHits = elasticsearchTemplate.search(query, TicketDeliveryMethod.class);
        List<TicketDeliveryMethod> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TicketDeliveryMethod entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
