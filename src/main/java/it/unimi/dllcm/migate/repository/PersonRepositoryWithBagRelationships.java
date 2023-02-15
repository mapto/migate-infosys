package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Person;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PersonRepositoryWithBagRelationships {
    Optional<Person> fetchBagRelationships(Optional<Person> person);

    List<Person> fetchBagRelationships(List<Person> people);

    Page<Person> fetchBagRelationships(Page<Person> people);
}
