package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Institution;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class InstitutionRepositoryWithBagRelationshipsImpl implements InstitutionRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Institution> fetchBagRelationships(Optional<Institution> institution) {
        return institution.map(this::fetchRoleInstitutions);
    }

    @Override
    public Page<Institution> fetchBagRelationships(Page<Institution> institutions) {
        return new PageImpl<>(
            fetchBagRelationships(institutions.getContent()),
            institutions.getPageable(),
            institutions.getTotalElements()
        );
    }

    @Override
    public List<Institution> fetchBagRelationships(List<Institution> institutions) {
        return Optional.of(institutions).map(this::fetchRoleInstitutions).orElse(Collections.emptyList());
    }

    Institution fetchRoleInstitutions(Institution result) {
        return entityManager
            .createQuery(
                "select institution from Institution institution left join fetch institution.roleInstitutions where institution is :institution",
                Institution.class
            )
            .setParameter("institution", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Institution> fetchRoleInstitutions(List<Institution> institutions) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, institutions.size()).forEach(index -> order.put(institutions.get(index).getId(), index));
        List<Institution> result = entityManager
            .createQuery(
                "select distinct institution from Institution institution left join fetch institution.roleInstitutions where institution in :institutions",
                Institution.class
            )
            .setParameter("institutions", institutions)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
