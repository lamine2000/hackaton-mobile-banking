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
import sn.finedev.java.domain.Transac;
import sn.finedev.java.repository.TransacRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Transac} entity.
 */
public interface TransacSearchRepository extends ElasticsearchRepository<Transac, Long>, TransacSearchRepositoryInternal {}

interface TransacSearchRepositoryInternal {
    Page<Transac> search(String query, Pageable pageable);

    Page<Transac> search(Query query);

    void index(Transac entity);
}

class TransacSearchRepositoryInternalImpl implements TransacSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TransacRepository repository;

    TransacSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TransacRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Transac> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Transac> search(Query query) {
        SearchHits<Transac> searchHits = elasticsearchTemplate.search(query, Transac.class);
        List<Transac> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Transac entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
