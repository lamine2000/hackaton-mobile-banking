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
import sn.finedev.java.domain.Functionality;
import sn.finedev.java.repository.FunctionalityRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Functionality} entity.
 */
public interface FunctionalitySearchRepository
    extends ElasticsearchRepository<Functionality, Long>, FunctionalitySearchRepositoryInternal {}

interface FunctionalitySearchRepositoryInternal {
    Page<Functionality> search(String query, Pageable pageable);

    Page<Functionality> search(Query query);

    void index(Functionality entity);
}

class FunctionalitySearchRepositoryInternalImpl implements FunctionalitySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final FunctionalityRepository repository;

    FunctionalitySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, FunctionalityRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Functionality> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Functionality> search(Query query) {
        SearchHits<Functionality> searchHits = elasticsearchTemplate.search(query, Functionality.class);
        List<Functionality> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Functionality entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
