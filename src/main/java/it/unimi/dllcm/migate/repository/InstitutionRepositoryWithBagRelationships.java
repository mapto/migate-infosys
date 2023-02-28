package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Institution;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface InstitutionRepositoryWithBagRelationships {
    Optional<Institution> fetchBagRelationships(Optional<Institution> institution);

    List<Institution> fetchBagRelationships(List<Institution> institutions);

    Page<Institution> fetchBagRelationships(Page<Institution> institutions);
}
