package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.RoleWork;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RoleWork entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleWorkRepository extends JpaRepository<RoleWork, Long> {}
