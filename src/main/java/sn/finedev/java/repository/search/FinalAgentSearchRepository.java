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
import sn.finedev.java.domain.FinalAgent;
import sn.finedev.java.repository.FinalAgentRepository;

/**
 * Spring Data Elasticsearch repository for the {@link FinalAgent} entity.
 */
public interface FinalAgentSearchRepository extends ElasticsearchRepository<FinalAgent, Long>, FinalAgentSearchRepositoryInternal {}

interface FinalAgentSearchRepositoryInternal {
    Page<FinalAgent> search(String query, Pageable pageable);

    Page<FinalAgent> search(Query query);

    void index(FinalAgent entity);
}

class FinalAgentSearchRepositoryInternalImpl implements FinalAgentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final FinalAgentRepository repository;

    FinalAgentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, FinalAgentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<FinalAgent> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<FinalAgent> search(Query query) {
        SearchHits<FinalAgent> searchHits = elasticsearchTemplate.search(query, FinalAgent.class);
        List<FinalAgent> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(FinalAgent entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
