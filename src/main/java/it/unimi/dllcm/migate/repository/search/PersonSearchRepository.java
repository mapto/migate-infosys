package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.Person;
import it.unimi.dllcm.migate.repository.PersonRepository;
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
 * Spring Data Elasticsearch repository for the {@link Person} entity.
 */
public interface PersonSearchRepository extends ElasticsearchRepository<Person, Long>, PersonSearchRepositoryInternal {}

interface PersonSearchRepositoryInternal {
    Stream<Person> search(String query);

    Stream<Person> search(Query query);

    void index(Person entity);
}

class PersonSearchRepositoryInternalImpl implements PersonSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final PersonRepository repository;

    PersonSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, PersonRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Person> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Person> search(Query query) {
        return elasticsearchTemplate.search(query, Person.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Person entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
