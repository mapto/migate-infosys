package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.repository.InstitutionRepository;
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
 * Spring Data Elasticsearch repository for the {@link Institution} entity.
 */
public interface InstitutionSearchRepository extends ElasticsearchRepository<Institution, Long>, InstitutionSearchRepositoryInternal {}

interface InstitutionSearchRepositoryInternal {
    Stream<Institution> search(String query);

    Stream<Institution> search(Query query);

    void index(Institution entity);
}

class InstitutionSearchRepositoryInternalImpl implements InstitutionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final InstitutionRepository repository;

    InstitutionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, InstitutionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Institution> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Institution> search(Query query) {
        return elasticsearchTemplate.search(query, Institution.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Institution entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
