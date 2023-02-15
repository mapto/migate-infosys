package it.unimi.dllcm.migate.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.dllcm.migate.IntegrationTest;
import it.unimi.dllcm.migate.domain.RoleWork;
import it.unimi.dllcm.migate.repository.RoleWorkRepository;
import it.unimi.dllcm.migate.repository.search.RoleWorkSearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RoleWorkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RoleWorkResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/role-works";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/role-works";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RoleWorkRepository roleWorkRepository;

    @Autowired
    private RoleWorkSearchRepository roleWorkSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoleWorkMockMvc;

    private RoleWork roleWork;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoleWork createEntity(EntityManager em) {
        RoleWork roleWork = new RoleWork().name(DEFAULT_NAME).start(DEFAULT_START).end(DEFAULT_END);
        return roleWork;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoleWork createUpdatedEntity(EntityManager em) {
        RoleWork roleWork = new RoleWork().name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);
        return roleWork;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        roleWorkSearchRepository.deleteAll();
        assertThat(roleWorkSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        roleWork = createEntity(em);
    }

    @Test
    @Transactional
    void createRoleWork() throws Exception {
        int databaseSizeBeforeCreate = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        // Create the RoleWork
        restRoleWorkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleWork)))
            .andExpect(status().isCreated());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        RoleWork testRoleWork = roleWorkList.get(roleWorkList.size() - 1);
        assertThat(testRoleWork.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRoleWork.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testRoleWork.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    @Transactional
    void createRoleWorkWithExistingId() throws Exception {
        // Create the RoleWork with an existing ID
        roleWork.setId(1L);

        int databaseSizeBeforeCreate = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoleWorkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleWork)))
            .andExpect(status().isBadRequest());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRoleWorks() throws Exception {
        // Initialize the database
        roleWorkRepository.saveAndFlush(roleWork);

        // Get all the roleWorkList
        restRoleWorkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roleWork.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }

    @Test
    @Transactional
    void getRoleWork() throws Exception {
        // Initialize the database
        roleWorkRepository.saveAndFlush(roleWork);

        // Get the roleWork
        restRoleWorkMockMvc
            .perform(get(ENTITY_API_URL_ID, roleWork.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(roleWork.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRoleWork() throws Exception {
        // Get the roleWork
        restRoleWorkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRoleWork() throws Exception {
        // Initialize the database
        roleWorkRepository.saveAndFlush(roleWork);

        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();
        roleWorkSearchRepository.save(roleWork);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());

        // Update the roleWork
        RoleWork updatedRoleWork = roleWorkRepository.findById(roleWork.getId()).get();
        // Disconnect from session so that the updates on updatedRoleWork are not directly saved in db
        em.detach(updatedRoleWork);
        updatedRoleWork.name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);

        restRoleWorkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRoleWork.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRoleWork))
            )
            .andExpect(status().isOk());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        RoleWork testRoleWork = roleWorkList.get(roleWorkList.size() - 1);
        assertThat(testRoleWork.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRoleWork.getStart()).isEqualTo(UPDATED_START);
        assertThat(testRoleWork.getEnd()).isEqualTo(UPDATED_END);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RoleWork> roleWorkSearchList = IterableUtils.toList(roleWorkSearchRepository.findAll());
                RoleWork testRoleWorkSearch = roleWorkSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRoleWorkSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testRoleWorkSearch.getStart()).isEqualTo(UPDATED_START);
                assertThat(testRoleWorkSearch.getEnd()).isEqualTo(UPDATED_END);
            });
    }

    @Test
    @Transactional
    void putNonExistingRoleWork() throws Exception {
        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        roleWork.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleWorkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roleWork.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roleWork))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoleWork() throws Exception {
        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        roleWork.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleWorkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roleWork))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoleWork() throws Exception {
        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        roleWork.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleWorkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleWork)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRoleWorkWithPatch() throws Exception {
        // Initialize the database
        roleWorkRepository.saveAndFlush(roleWork);

        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();

        // Update the roleWork using partial update
        RoleWork partialUpdatedRoleWork = new RoleWork();
        partialUpdatedRoleWork.setId(roleWork.getId());

        partialUpdatedRoleWork.name(UPDATED_NAME).start(UPDATED_START);

        restRoleWorkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoleWork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRoleWork))
            )
            .andExpect(status().isOk());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        RoleWork testRoleWork = roleWorkList.get(roleWorkList.size() - 1);
        assertThat(testRoleWork.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRoleWork.getStart()).isEqualTo(UPDATED_START);
        assertThat(testRoleWork.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    @Transactional
    void fullUpdateRoleWorkWithPatch() throws Exception {
        // Initialize the database
        roleWorkRepository.saveAndFlush(roleWork);

        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();

        // Update the roleWork using partial update
        RoleWork partialUpdatedRoleWork = new RoleWork();
        partialUpdatedRoleWork.setId(roleWork.getId());

        partialUpdatedRoleWork.name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);

        restRoleWorkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoleWork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRoleWork))
            )
            .andExpect(status().isOk());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        RoleWork testRoleWork = roleWorkList.get(roleWorkList.size() - 1);
        assertThat(testRoleWork.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRoleWork.getStart()).isEqualTo(UPDATED_START);
        assertThat(testRoleWork.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void patchNonExistingRoleWork() throws Exception {
        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        roleWork.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleWorkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, roleWork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roleWork))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoleWork() throws Exception {
        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        roleWork.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleWorkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roleWork))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoleWork() throws Exception {
        int databaseSizeBeforeUpdate = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        roleWork.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleWorkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(roleWork)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoleWork in the database
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRoleWork() throws Exception {
        // Initialize the database
        roleWorkRepository.saveAndFlush(roleWork);
        roleWorkRepository.save(roleWork);
        roleWorkSearchRepository.save(roleWork);

        int databaseSizeBeforeDelete = roleWorkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the roleWork
        restRoleWorkMockMvc
            .perform(delete(ENTITY_API_URL_ID, roleWork.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RoleWork> roleWorkList = roleWorkRepository.findAll();
        assertThat(roleWorkList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleWorkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRoleWork() throws Exception {
        // Initialize the database
        roleWork = roleWorkRepository.saveAndFlush(roleWork);
        roleWorkSearchRepository.save(roleWork);

        // Search the roleWork
        restRoleWorkMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + roleWork.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roleWork.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }
}
