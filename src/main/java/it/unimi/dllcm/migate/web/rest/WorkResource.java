package it.unimi.dllcm.migate.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.repository.WorkRepository;
import it.unimi.dllcm.migate.repository.search.WorkSearchRepository;
import it.unimi.dllcm.migate.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.unimi.dllcm.migate.domain.Work}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WorkResource {

    private final Logger log = LoggerFactory.getLogger(WorkResource.class);

    private static final String ENTITY_NAME = "work";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkRepository workRepository;

    private final WorkSearchRepository workSearchRepository;

    public WorkResource(WorkRepository workRepository, WorkSearchRepository workSearchRepository) {
        this.workRepository = workRepository;
        this.workSearchRepository = workSearchRepository;
    }

    /**
     * {@code POST  /works} : Create a new work.
     *
     * @param work the work to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new work, or with status {@code 400 (Bad Request)} if the work has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/works")
    public ResponseEntity<Work> createWork(@Valid @RequestBody Work work) throws URISyntaxException {
        log.debug("REST request to save Work : {}", work);
        if (work.getId() != null) {
            throw new BadRequestAlertException("A new work cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Work result = workRepository.save(work);
        workSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/works/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /works/:id} : Updates an existing work.
     *
     * @param id the id of the work to save.
     * @param work the work to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated work,
     * or with status {@code 400 (Bad Request)} if the work is not valid,
     * or with status {@code 500 (Internal Server Error)} if the work couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/works/{id}")
    public ResponseEntity<Work> updateWork(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Work work)
        throws URISyntaxException {
        log.debug("REST request to update Work : {}, {}", id, work);
        if (work.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, work.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Work result = workRepository.save(work);
        workSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, work.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /works/:id} : Partial updates given fields of an existing work, field will ignore if it is null
     *
     * @param id the id of the work to save.
     * @param work the work to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated work,
     * or with status {@code 400 (Bad Request)} if the work is not valid,
     * or with status {@code 404 (Not Found)} if the work is not found,
     * or with status {@code 500 (Internal Server Error)} if the work couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/works/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Work> partialUpdateWork(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Work work
    ) throws URISyntaxException {
        log.debug("REST request to partial update Work partially : {}, {}", id, work);
        if (work.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, work.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Work> result = workRepository
            .findById(work.getId())
            .map(existingWork -> {
                if (work.getName() != null) {
                    existingWork.setName(work.getName());
                }
                if (work.getPublished() != null) {
                    existingWork.setPublished(work.getPublished());
                }

                return existingWork;
            })
            .map(workRepository::save)
            .map(savedWork -> {
                workSearchRepository.save(savedWork);

                return savedWork;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, work.getId().toString())
        );
    }

    /**
     * {@code GET  /works} : get all the works.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of works in body.
     */
    @GetMapping("/works")
    public List<Work> getAllWorks(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Works");
        if (eagerload) {
            return workRepository.findAllWithEagerRelationships();
        } else {
            return workRepository.findAll();
        }
    }

    /**
     * {@code GET  /works/:id} : get the "id" work.
     *
     * @param id the id of the work to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the work, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/works/{id}")
    public ResponseEntity<Work> getWork(@PathVariable Long id) {
        log.debug("REST request to get Work : {}", id);
        Optional<Work> work = workRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(work);
    }

    /**
     * {@code DELETE  /works/:id} : delete the "id" work.
     *
     * @param id the id of the work to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/works/{id}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        log.debug("REST request to delete Work : {}", id);
        workRepository.deleteById(id);
        workSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/works?query=:query} : search for the work corresponding
     * to the query.
     *
     * @param query the query of the work search.
     * @return the result of the search.
     */
    @GetMapping("/_search/works")
    public List<Work> searchWorks(@RequestParam String query) {
        log.debug("REST request to search Works for query {}", query);
        return StreamSupport.stream(workSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
