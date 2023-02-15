package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.RoleInstitution;
import it.unimi.dllcm.migate.repository.RoleInstitutionRepository;
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
 * Spring Data Elasticsearch repository for the {@link RoleInstitution} entity.
 */
public interface RoleInstitutionSearchRepository
    extends ElasticsearchRepository<RoleInstitution, Long>, RoleInstitutionSearchRepositoryInternal {}

interface RoleInstitutionSearchRepositoryInternal {
    Stream<RoleInstitution> search(String query);

    Stream<RoleInstitution> search(Query query);

    void index(RoleInstitution entity);
}

class RoleInstitutionSearchRepositoryInternalImpl implements RoleInstitutionSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final RoleInstitutionRepository repository;

    RoleInstitutionSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, RoleInstitutionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<RoleInstitution> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<RoleInstitution> search(Query query) {
        return elasticsearchTemplate.search(query, RoleInstitution.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(RoleInstitution entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
