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
import sn.finedev.java.domain.PaymentMethod;
import sn.finedev.java.repository.PaymentMethodRepository;

/**
 * Spring Data Elasticsearch repository for the {@link PaymentMethod} entity.
 */
public interface PaymentMethodSearchRepository
    extends ElasticsearchRepository<PaymentMethod, Long>, PaymentMethodSearchRepositoryInternal {}

interface PaymentMethodSearchRepositoryInternal {
    Page<PaymentMethod> search(String query, Pageable pageable);

    Page<PaymentMethod> search(Query query);

    void index(PaymentMethod entity);
}

class PaymentMethodSearchRepositoryInternalImpl implements PaymentMethodSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final PaymentMethodRepository repository;

    PaymentMethodSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, PaymentMethodRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PaymentMethod> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<PaymentMethod> search(Query query) {
        SearchHits<PaymentMethod> searchHits = elasticsearchTemplate.search(query, PaymentMethod.class);
        List<PaymentMethod> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PaymentMethod entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
