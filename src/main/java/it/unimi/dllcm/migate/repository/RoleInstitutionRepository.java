package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.RoleInstitution;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RoleInstitution entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleInstitutionRepository extends JpaRepository<RoleInstitution, Long> {}
