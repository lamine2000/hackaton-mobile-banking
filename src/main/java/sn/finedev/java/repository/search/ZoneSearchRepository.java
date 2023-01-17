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
import sn.finedev.java.domain.Zone;
import sn.finedev.java.repository.ZoneRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Zone} entity.
 */
public interface ZoneSearchRepository extends ElasticsearchRepository<Zone, Long>, ZoneSearchRepositoryInternal {}

interface ZoneSearchRepositoryInternal {
    Page<Zone> search(String query, Pageable pageable);

    Page<Zone> search(Query query);

    void index(Zone entity);
}

class ZoneSearchRepositoryInternalImpl implements ZoneSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ZoneRepository repository;

    ZoneSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ZoneRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Zone> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Zone> search(Query query) {
        SearchHits<Zone> searchHits = elasticsearchTemplate.search(query, Zone.class);
        List<Zone> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Zone entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
