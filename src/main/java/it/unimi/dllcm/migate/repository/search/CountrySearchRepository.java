package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.Country;
import it.unimi.dllcm.migate.repository.CountryRepository;
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
 * Spring Data Elasticsearch repository for the {@link Country} entity.
 */
public interface CountrySearchRepository extends ElasticsearchRepository<Country, Long>, CountrySearchRepositoryInternal {}

interface CountrySearchRepositoryInternal {
    Stream<Country> search(String query);

    Stream<Country> search(Query query);

    void index(Country entity);
}

class CountrySearchRepositoryInternalImpl implements CountrySearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CountryRepository repository;

    CountrySearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CountryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Country> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Country> search(Query query) {
        return elasticsearchTemplate.search(query, Country.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Country entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
