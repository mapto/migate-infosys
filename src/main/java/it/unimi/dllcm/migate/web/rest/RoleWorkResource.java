package it.unimi.dllcm.migate.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.domain.RoleWork;
import it.unimi.dllcm.migate.repository.RoleWorkRepository;
import it.unimi.dllcm.migate.repository.search.RoleWorkSearchRepository;
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
 * REST controller for managing {@link it.unimi.dllcm.migate.domain.RoleWork}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RoleWorkResource {

    private final Logger log = LoggerFactory.getLogger(RoleWorkResource.class);

    private static final String ENTITY_NAME = "roleWork";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoleWorkRepository roleWorkRepository;

    private final RoleWorkSearchRepository roleWorkSearchRepository;

    public RoleWorkResource(RoleWorkRepository roleWorkRepository, RoleWorkSearchRepository roleWorkSearchRepository) {
        this.roleWorkRepository = roleWorkRepository;
        this.roleWorkSearchRepository = roleWorkSearchRepository;
    }

    /**
     * {@code POST  /role-works} : Create a new roleWork.
     *
     * @param roleWork the roleWork to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roleWork, or with status {@code 400 (Bad Request)} if the roleWork has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/role-works")
    public ResponseEntity<RoleWork> createRoleWork(@RequestBody RoleWork roleWork) throws URISyntaxException {
        log.debug("REST request to save RoleWork : {}", roleWork);
        if (roleWork.getId() != null) {
            throw new BadRequestAlertException("A new roleWork cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RoleWork result = roleWorkRepository.save(roleWork);
        roleWorkSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/role-works/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /role-works/:id} : Updates an existing roleWork.
     *
     * @param id the id of the roleWork to save.
     * @param roleWork the roleWork to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleWork,
     * or with status {@code 400 (Bad Request)} if the roleWork is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roleWork couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/role-works/{id}")
    public ResponseEntity<RoleWork> updateRoleWork(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RoleWork roleWork
    ) throws URISyntaxException {
        log.debug("REST request to update RoleWork : {}, {}", id, roleWork);
        if (roleWork.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roleWork.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roleWorkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RoleWork result = roleWorkRepository.save(roleWork);
        roleWorkSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, roleWork.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /role-works/:id} : Partial updates given fields of an existing roleWork, field will ignore if it is null
     *
     * @param id the id of the roleWork to save.
     * @param roleWork the roleWork to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleWork,
     * or with status {@code 400 (Bad Request)} if the roleWork is not valid,
     * or with status {@code 404 (Not Found)} if the roleWork is not found,
     * or with status {@code 500 (Internal Server Error)} if the roleWork couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/role-works/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RoleWork> partialUpdateRoleWork(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RoleWork roleWork
    ) throws URISyntaxException {
        log.debug("REST request to partial update RoleWork partially : {}, {}", id, roleWork);
        if (roleWork.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roleWork.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roleWorkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoleWork> result = roleWorkRepository
            .findById(roleWork.getId())
            .map(existingRoleWork -> {
                if (roleWork.getName() != null) {
                    existingRoleWork.setName(roleWork.getName());
                }
                if (roleWork.getStart() != null) {
                    existingRoleWork.setStart(roleWork.getStart());
                }
                if (roleWork.getEnd() != null) {
                    existingRoleWork.setEnd(roleWork.getEnd());
                }

                return existingRoleWork;
            })
            .map(roleWorkRepository::save)
            .map(savedRoleWork -> {
                roleWorkSearchRepository.save(savedRoleWork);

                return savedRoleWork;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, roleWork.getId().toString())
        );
    }

    /**
     * {@code GET  /role-works} : get all the roleWorks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roleWorks in body.
     */
    @GetMapping("/role-works")
    public List<RoleWork> getAllRoleWorks() {
        log.debug("REST request to get all RoleWorks");
        return roleWorkRepository.findAll();
    }

    /**
     * {@code GET  /role-works/:id} : get the "id" roleWork.
     *
     * @param id the id of the roleWork to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roleWork, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/role-works/{id}")
    public ResponseEntity<RoleWork> getRoleWork(@PathVariable Long id) {
        log.debug("REST request to get RoleWork : {}", id);
        Optional<RoleWork> roleWork = roleWorkRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(roleWork);
    }

    /**
     * {@code DELETE  /role-works/:id} : delete the "id" roleWork.
     *
     * @param id the id of the roleWork to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/role-works/{id}")
    public ResponseEntity<Void> deleteRoleWork(@PathVariable Long id) {
        log.debug("REST request to delete RoleWork : {}", id);
        roleWorkRepository.deleteById(id);
        roleWorkSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/role-works?query=:query} : search for the roleWork corresponding
     * to the query.
     *
     * @param query the query of the roleWork search.
     * @return the result of the search.
     */
    @GetMapping("/_search/role-works")
    public List<RoleWork> searchRoleWorks(@RequestParam String query) {
        log.debug("REST request to search RoleWorks for query {}", query);
        return StreamSupport.stream(roleWorkSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
