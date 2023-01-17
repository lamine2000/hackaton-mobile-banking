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
import sn.finedev.java.domain.FunctionalityCategory;
import sn.finedev.java.repository.FunctionalityCategoryRepository;

/**
 * Spring Data Elasticsearch repository for the {@link FunctionalityCategory} entity.
 */
public interface FunctionalityCategorySearchRepository
    extends ElasticsearchRepository<FunctionalityCategory, Long>, FunctionalityCategorySearchRepositoryInternal {}

interface FunctionalityCategorySearchRepositoryInternal {
    Page<FunctionalityCategory> search(String query, Pageable pageable);

    Page<FunctionalityCategory> search(Query query);

    void index(FunctionalityCategory entity);
}

class FunctionalityCategorySearchRepositoryInternalImpl implements FunctionalityCategorySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final FunctionalityCategoryRepository repository;

    FunctionalityCategorySearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        FunctionalityCategoryRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<FunctionalityCategory> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<FunctionalityCategory> search(Query query) {
        SearchHits<FunctionalityCategory> searchHits = elasticsearchTemplate.search(query, FunctionalityCategory.class);
        List<FunctionalityCategory> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(FunctionalityCategory entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
