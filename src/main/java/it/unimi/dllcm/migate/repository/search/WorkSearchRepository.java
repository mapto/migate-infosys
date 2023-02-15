package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.repository.WorkRepository;
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
 * Spring Data Elasticsearch repository for the {@link Work} entity.
 */
public interface WorkSearchRepository extends ElasticsearchRepository<Work, Long>, WorkSearchRepositoryInternal {}

interface WorkSearchRepositoryInternal {
    Stream<Work> search(String query);

    Stream<Work> search(Query query);

    void index(Work entity);
}

class WorkSearchRepositoryInternalImpl implements WorkSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final WorkRepository repository;

    WorkSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, WorkRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Work> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Work> search(Query query) {
        return elasticsearchTemplate.search(query, Work.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Work entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
