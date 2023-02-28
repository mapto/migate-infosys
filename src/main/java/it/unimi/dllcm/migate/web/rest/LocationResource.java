package it.unimi.dllcm.migate.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.domain.Location;
import it.unimi.dllcm.migate.repository.LocationRepository;
import it.unimi.dllcm.migate.repository.search.LocationSearchRepository;
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
 * REST controller for managing {@link it.unimi.dllcm.migate.domain.Location}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LocationResource {

    private final Logger log = LoggerFactory.getLogger(LocationResource.class);

    private static final String ENTITY_NAME = "location";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LocationRepository locationRepository;

    private final LocationSearchRepository locationSearchRepository;

    public LocationResource(LocationRepository locationRepository, LocationSearchRepository locationSearchRepository) {
        this.locationRepository = locationRepository;
        this.locationSearchRepository = locationSearchRepository;
    }

    /**
     * {@code POST  /locations} : Create a new location.
     *
     * @param location the location to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new location, or with status {@code 400 (Bad Request)} if the location has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/locations")
    public ResponseEntity<Location> createLocation(@Valid @RequestBody Location location) throws URISyntaxException {
        log.debug("REST request to save Location : {}", location);
        if (location.getId() != null) {
            throw new BadRequestAlertException("A new location cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Location result = locationRepository.save(location);
        locationSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/locations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /locations/:id} : Updates an existing location.
     *
     * @param id the id of the location to save.
     * @param location the location to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated location,
     * or with status {@code 400 (Bad Request)} if the location is not valid,
     * or with status {@code 500 (Internal Server Error)} if the location couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/locations/{id}")
    public ResponseEntity<Location> updateLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Location location
    ) throws URISyntaxException {
        log.debug("REST request to update Location : {}, {}", id, location);
        if (location.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, location.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Location result = locationRepository.save(location);
        locationSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, location.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /locations/:id} : Partial updates given fields of an existing location, field will ignore if it is null
     *
     * @param id the id of the location to save.
     * @param location the location to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated location,
     * or with status {@code 400 (Bad Request)} if the location is not valid,
     * or with status {@code 404 (Not Found)} if the location is not found,
     * or with status {@code 500 (Internal Server Error)} if the location couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/locations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Location> partialUpdateLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Location location
    ) throws URISyntaxException {
        log.debug("REST request to partial update Location partially : {}, {}", id, location);
        if (location.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, location.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Location> result = locationRepository
            .findById(location.getId())
            .map(existingLocation -> {
                if (location.getAddress() != null) {
                    existingLocation.setAddress(location.getAddress());
                }
                if (location.getCountry() != null) {
                    existingLocation.setCountry(location.getCountry());
                }

                return existingLocation;
            })
            .map(locationRepository::save)
            .map(savedLocation -> {
                locationSearchRepository.save(savedLocation);

                return savedLocation;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, location.getId().toString())
        );
    }

    /**
     * {@code GET  /locations} : get all the locations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of locations in body.
     */
    @GetMapping("/locations")
    public List<Location> getAllLocations() {
        log.debug("REST request to get all Locations");
        return locationRepository.findAll();
    }

    /**
     * {@code GET  /locations/:id} : get the "id" location.
     *
     * @param id the id of the location to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the location, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/locations/{id}")
    public ResponseEntity<Location> getLocation(@PathVariable Long id) {
        log.debug("REST request to get Location : {}", id);
        Optional<Location> location = locationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(location);
    }

    /**
     * {@code DELETE  /locations/:id} : delete the "id" location.
     *
     * @param id the id of the location to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        log.debug("REST request to delete Location : {}", id);
        locationRepository.deleteById(id);
        locationSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/locations?query=:query} : search for the location corresponding
     * to the query.
     *
     * @param query the query of the location search.
     * @return the result of the search.
     */
    @GetMapping("/_search/locations")
    public List<Location> searchLocations(@RequestParam String query) {
        log.debug("REST request to search Locations for query {}", query);
        return StreamSupport.stream(locationSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
