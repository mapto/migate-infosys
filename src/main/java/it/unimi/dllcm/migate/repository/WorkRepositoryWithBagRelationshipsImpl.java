package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Work;
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
public class WorkRepositoryWithBagRelationshipsImpl implements WorkRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Work> fetchBagRelationships(Optional<Work> work) {
        return work.map(this::fetchPeople).map(this::fetchInstitutions).map(this::fetchRoleWorks);
    }

    @Override
    public Page<Work> fetchBagRelationships(Page<Work> works) {
        return new PageImpl<>(fetchBagRelationships(works.getContent()), works.getPageable(), works.getTotalElements());
    }

    @Override
    public List<Work> fetchBagRelationships(List<Work> works) {
        return Optional
            .of(works)
            .map(this::fetchPeople)
            .map(this::fetchInstitutions)
            .map(this::fetchRoleWorks)
            .orElse(Collections.emptyList());
    }

    Work fetchPeople(Work result) {
        return entityManager
            .createQuery("select work from Work work left join fetch work.people where work is :work", Work.class)
            .setParameter("work", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Work> fetchPeople(List<Work> works) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, works.size()).forEach(index -> order.put(works.get(index).getId(), index));
        List<Work> result = entityManager
            .createQuery("select distinct work from Work work left join fetch work.people where work in :works", Work.class)
            .setParameter("works", works)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Work fetchInstitutions(Work result) {
        return entityManager
            .createQuery("select work from Work work left join fetch work.institutions where work is :work", Work.class)
            .setParameter("work", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Work> fetchInstitutions(List<Work> works) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, works.size()).forEach(index -> order.put(works.get(index).getId(), index));
        List<Work> result = entityManager
            .createQuery("select distinct work from Work work left join fetch work.institutions where work in :works", Work.class)
            .setParameter("works", works)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Work fetchRoleWorks(Work result) {
        return entityManager
            .createQuery("select work from Work work left join fetch work.roleWorks where work is :work", Work.class)
            .setParameter("work", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Work> fetchRoleWorks(List<Work> works) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, works.size()).forEach(index -> order.put(works.get(index).getId(), index));
        List<Work> result = entityManager
            .createQuery("select distinct work from Work work left join fetch work.roleWorks where work in :works", Work.class)
            .setParameter("works", works)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
