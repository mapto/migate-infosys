package it.unimi.dllcm.migate.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import it.unimi.dllcm.migate.domain.Language;
import it.unimi.dllcm.migate.repository.LanguageRepository;
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
 * Spring Data Elasticsearch repository for the {@link Language} entity.
 */
public interface LanguageSearchRepository extends ElasticsearchRepository<Language, Long>, LanguageSearchRepositoryInternal {}

interface LanguageSearchRepositoryInternal {
    Stream<Language> search(String query);

    Stream<Language> search(Query query);

    void index(Language entity);
}

class LanguageSearchRepositoryInternalImpl implements LanguageSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final LanguageRepository repository;

    LanguageSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, LanguageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Language> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Language> search(Query query) {
        return elasticsearchTemplate.search(query, Language.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Language entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
