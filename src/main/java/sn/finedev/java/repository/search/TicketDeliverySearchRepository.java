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
import sn.finedev.java.domain.TicketDelivery;
import sn.finedev.java.repository.TicketDeliveryRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TicketDelivery} entity.
 */
public interface TicketDeliverySearchRepository
    extends ElasticsearchRepository<TicketDelivery, Long>, TicketDeliverySearchRepositoryInternal {}

interface TicketDeliverySearchRepositoryInternal {
    Page<TicketDelivery> search(String query, Pageable pageable);

    Page<TicketDelivery> search(Query query);

    void index(TicketDelivery entity);
}

class TicketDeliverySearchRepositoryInternalImpl implements TicketDeliverySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TicketDeliveryRepository repository;

    TicketDeliverySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TicketDeliveryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TicketDelivery> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<TicketDelivery> search(Query query) {
        SearchHits<TicketDelivery> searchHits = elasticsearchTemplate.search(query, TicketDelivery.class);
        List<TicketDelivery> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TicketDelivery entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
