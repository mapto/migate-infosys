package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Work;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface WorkRepositoryWithBagRelationships {
    Optional<Work> fetchBagRelationships(Optional<Work> work);

    List<Work> fetchBagRelationships(List<Work> works);

    Page<Work> fetchBagRelationships(Page<Work> works);
}
