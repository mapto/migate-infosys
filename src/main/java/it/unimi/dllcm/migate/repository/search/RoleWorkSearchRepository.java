package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.RoleWork;
import it.unimi.dllcm.migate.repository.RoleWorkRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link RoleWork} entity.
 */
public interface RoleWorkSearchRepository extends ElasticsearchRepository<RoleWork, Long>, RoleWorkSearchRepositoryInternal {}

interface RoleWorkSearchRepositoryInternal {
    Stream<RoleWork> search(String query);

    Stream<RoleWork> search(Query query);

    void index(RoleWork entity);
}

class RoleWorkSearchRepositoryInternalImpl implements RoleWorkSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final RoleWorkRepository repository;

    RoleWorkSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, RoleWorkRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<RoleWork> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<RoleWork> search(Query query) {
        return elasticsearchTemplate.search(query, RoleWork.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(RoleWork entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
