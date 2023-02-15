package it.unimi.dllcm.migate.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.domain.RoleInstitution;
import it.unimi.dllcm.migate.repository.RoleInstitutionRepository;
import it.unimi.dllcm.migate.repository.search.RoleInstitutionSearchRepository;
import it.unimi.dllcm.migate.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.unimi.dllcm.migate.domain.RoleInstitution}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RoleInstitutionResource {

    private final Logger log = LoggerFactory.getLogger(RoleInstitutionResource.class);

    private static final String ENTITY_NAME = "roleInstitution";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoleInstitutionRepository roleInstitutionRepository;

    private final RoleInstitutionSearchRepository roleInstitutionSearchRepository;

    public RoleInstitutionResource(
        RoleInstitutionRepository roleInstitutionRepository,
        RoleInstitutionSearchRepository roleInstitutionSearchRepository
    ) {
        this.roleInstitutionRepository = roleInstitutionRepository;
        this.roleInstitutionSearchRepository = roleInstitutionSearchRepository;
    }

    /**
     * {@code POST  /role-institutions} : Create a new roleInstitution.
     *
     * @param roleInstitution the roleInstitution to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roleInstitution, or with status {@code 400 (Bad Request)} if the roleInstitution has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/role-institutions")
    public ResponseEntity<RoleInstitution> createRoleInstitution(@RequestBody RoleInstitution roleInstitution) throws URISyntaxException {
        log.debug("REST request to save RoleInstitution : {}", roleInstitution);
        if (roleInstitution.getId() != null) {
            throw new BadRequestAlertException("A new roleInstitution cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RoleInstitution result = roleInstitutionRepository.save(roleInstitution);
        roleInstitutionSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/role-institutions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /role-institutions/:id} : Updates an existing roleInstitution.
     *
     * @param id the id of the roleInstitution to save.
     * @param roleInstitution the roleInstitution to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleInstitution,
     * or with status {@code 400 (Bad Request)} if the roleInstitution is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roleInstitution couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/role-institutions/{id}")
    public ResponseEntity<RoleInstitution> updateRoleInstitution(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RoleInstitution roleInstitution
    ) throws URISyntaxException {
        log.debug("REST request to update RoleInstitution : {}, {}", id, roleInstitution);
        if (roleInstitution.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roleInstitution.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roleInstitutionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RoleInstitution result = roleInstitutionRepository.save(roleInstitution);
        roleInstitutionSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, roleInstitution.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /role-institutions/:id} : Partial updates given fields of an existing roleInstitution, field will ignore if it is null
     *
     * @param id the id of the roleInstitution to save.
     * @param roleInstitution the roleInstitution to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleInstitution,
     * or with status {@code 400 (Bad Request)} if the roleInstitution is not valid,
     * or with status {@code 404 (Not Found)} if the roleInstitution is not found,
     * or with status {@code 500 (Internal Server Error)} if the roleInstitution couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/role-institutions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RoleInstitution> partialUpdateRoleInstitution(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RoleInstitution roleInstitution
    ) throws URISyntaxException {
        log.debug("REST request to partial update RoleInstitution partially : {}, {}", id, roleInstitution);
        if (roleInstitution.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roleInstitution.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roleInstitutionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoleInstitution> result = roleInstitutionRepository
            .findById(roleInstitution.getId())
            .map(existingRoleInstitution -> {
                if (roleInstitution.getName() != null) {
                    existingRoleInstitution.setName(roleInstitution.getName());
                }
                if (roleInstitution.getStart() != null) {
                    existingRoleInstitution.setStart(roleInstitution.getStart());
                }
                if (roleInstitution.getEnd() != null) {
                    existingRoleInstitution.setEnd(roleInstitution.getEnd());
                }

                return existingRoleInstitution;
            })
            .map(roleInstitutionRepository::save)
            .map(savedRoleInstitution -> {
                roleInstitutionSearchRepository.save(savedRoleInstitution);

                return savedRoleInstitution;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, roleInstitution.getId().toString())
        );
    }

    /**
     * {@code GET  /role-institutions} : get all the roleInstitutions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roleInstitutions in body.
     */
    @GetMapping("/role-institutions")
    public List<RoleInstitution> getAllRoleInstitutions() {
        log.debug("REST request to get all RoleInstitutions");
        return roleInstitutionRepository.findAll();
    }

    /**
     * {@code GET  /role-institutions/:id} : get the "id" roleInstitution.
     *
     * @param id the id of the roleInstitution to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roleInstitution, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/role-institutions/{id}")
    public ResponseEntity<RoleInstitution> getRoleInstitution(@PathVariable Long id) {
        log.debug("REST request to get RoleInstitution : {}", id);
        Optional<RoleInstitution> roleInstitution = roleInstitutionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(roleInstitution);
    }

    /**
     * {@code DELETE  /role-institutions/:id} : delete the "id" roleInstitution.
     *
     * @param id the id of the roleInstitution to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/role-institutions/{id}")
    public ResponseEntity<Void> deleteRoleInstitution(@PathVariable Long id) {
        log.debug("REST request to delete RoleInstitution : {}", id);
        roleInstitutionRepository.deleteById(id);
        roleInstitutionSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/role-institutions?query=:query} : search for the roleInstitution corresponding
     * to the query.
     *
     * @param query the query of the roleInstitution search.
     * @return the result of the search.
     */
    @GetMapping("/_search/role-institutions")
    public List<RoleInstitution> searchRoleInstitutions(@RequestParam String query) {
        log.debug("REST request to search RoleInstitutions for query {}", query);
        return StreamSupport.stream(roleInstitutionSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
