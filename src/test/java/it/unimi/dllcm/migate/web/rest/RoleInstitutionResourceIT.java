package it.unimi.dllcm.migate.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.dllcm.migate.IntegrationTest;
import it.unimi.dllcm.migate.domain.RoleInstitution;
import it.unimi.dllcm.migate.repository.RoleInstitutionRepository;
import it.unimi.dllcm.migate.repository.search.RoleInstitutionSearchRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link RoleInstitutionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RoleInstitutionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/role-institutions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/role-institutions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RoleInstitutionRepository roleInstitutionRepository;

    @Autowired
    private RoleInstitutionSearchRepository roleInstitutionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoleInstitutionMockMvc;

    private RoleInstitution roleInstitution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoleInstitution createEntity(EntityManager em) {
        RoleInstitution roleInstitution = new RoleInstitution().name(DEFAULT_NAME).start(DEFAULT_START).end(DEFAULT_END);
        return roleInstitution;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoleInstitution createUpdatedEntity(EntityManager em) {
        RoleInstitution roleInstitution = new RoleInstitution().name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);
        return roleInstitution;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        roleInstitutionSearchRepository.deleteAll();
        assertThat(roleInstitutionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        roleInstitution = createEntity(em);
    }

    @Test
    @Transactional
    void createRoleInstitution() throws Exception {
        int databaseSizeBeforeCreate = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        // Create the RoleInstitution
        restRoleInstitutionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isCreated());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        RoleInstitution testRoleInstitution = roleInstitutionList.get(roleInstitutionList.size() - 1);
        assertThat(testRoleInstitution.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRoleInstitution.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testRoleInstitution.getEnd()).isEqualTo(DEFAULT_END);
    }

    @Test
    @Transactional
    void createRoleInstitutionWithExistingId() throws Exception {
        // Create the RoleInstitution with an existing ID
        roleInstitution.setId(1L);

        int databaseSizeBeforeCreate = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoleInstitutionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        // set the field null
        roleInstitution.setName(null);

        // Create the RoleInstitution, which fails.

        restRoleInstitutionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isBadRequest());

        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRoleInstitutions() throws Exception {
        // Initialize the database
        roleInstitutionRepository.saveAndFlush(roleInstitution);

        // Get all the roleInstitutionList
        restRoleInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roleInstitution.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }

    @Test
    @Transactional
    void getRoleInstitution() throws Exception {
        // Initialize the database
        roleInstitutionRepository.saveAndFlush(roleInstitution);

        // Get the roleInstitution
        restRoleInstitutionMockMvc
            .perform(get(ENTITY_API_URL_ID, roleInstitution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(roleInstitution.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()))
            .andExpect(jsonPath("$.end").value(DEFAULT_END.toString()));
    }

    @Test
    @Transactional
    void getNonExistingRoleInstitution() throws Exception {
        // Get the roleInstitution
        restRoleInstitutionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRoleInstitution() throws Exception {
        // Initialize the database
        roleInstitutionRepository.saveAndFlush(roleInstitution);

        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();
        roleInstitutionSearchRepository.save(roleInstitution);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());

        // Update the roleInstitution
        RoleInstitution updatedRoleInstitution = roleInstitutionRepository.findById(roleInstitution.getId()).get();
        // Disconnect from session so that the updates on updatedRoleInstitution are not directly saved in db
        em.detach(updatedRoleInstitution);
        updatedRoleInstitution.name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);

        restRoleInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRoleInstitution.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRoleInstitution))
            )
            .andExpect(status().isOk());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        RoleInstitution testRoleInstitution = roleInstitutionList.get(roleInstitutionList.size() - 1);
        assertThat(testRoleInstitution.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRoleInstitution.getStart()).isEqualTo(UPDATED_START);
        assertThat(testRoleInstitution.getEnd()).isEqualTo(UPDATED_END);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RoleInstitution> roleInstitutionSearchList = IterableUtils.toList(roleInstitutionSearchRepository.findAll());
                RoleInstitution testRoleInstitutionSearch = roleInstitutionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRoleInstitutionSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testRoleInstitutionSearch.getStart()).isEqualTo(UPDATED_START);
                assertThat(testRoleInstitutionSearch.getEnd()).isEqualTo(UPDATED_END);
            });
    }

    @Test
    @Transactional
    void putNonExistingRoleInstitution() throws Exception {
        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        roleInstitution.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roleInstitution.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoleInstitution() throws Exception {
        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        roleInstitution.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoleInstitution() throws Exception {
        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        roleInstitution.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRoleInstitutionWithPatch() throws Exception {
        // Initialize the database
        roleInstitutionRepository.saveAndFlush(roleInstitution);

        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();

        // Update the roleInstitution using partial update
        RoleInstitution partialUpdatedRoleInstitution = new RoleInstitution();
        partialUpdatedRoleInstitution.setId(roleInstitution.getId());

        partialUpdatedRoleInstitution.end(UPDATED_END);

        restRoleInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoleInstitution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRoleInstitution))
            )
            .andExpect(status().isOk());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        RoleInstitution testRoleInstitution = roleInstitutionList.get(roleInstitutionList.size() - 1);
        assertThat(testRoleInstitution.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRoleInstitution.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testRoleInstitution.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void fullUpdateRoleInstitutionWithPatch() throws Exception {
        // Initialize the database
        roleInstitutionRepository.saveAndFlush(roleInstitution);

        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();

        // Update the roleInstitution using partial update
        RoleInstitution partialUpdatedRoleInstitution = new RoleInstitution();
        partialUpdatedRoleInstitution.setId(roleInstitution.getId());

        partialUpdatedRoleInstitution.name(UPDATED_NAME).start(UPDATED_START).end(UPDATED_END);

        restRoleInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoleInstitution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRoleInstitution))
            )
            .andExpect(status().isOk());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        RoleInstitution testRoleInstitution = roleInstitutionList.get(roleInstitutionList.size() - 1);
        assertThat(testRoleInstitution.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRoleInstitution.getStart()).isEqualTo(UPDATED_START);
        assertThat(testRoleInstitution.getEnd()).isEqualTo(UPDATED_END);
    }

    @Test
    @Transactional
    void patchNonExistingRoleInstitution() throws Exception {
        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        roleInstitution.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, roleInstitution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoleInstitution() throws Exception {
        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        roleInstitution.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isBadRequest());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoleInstitution() throws Exception {
        int databaseSizeBeforeUpdate = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        roleInstitution.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roleInstitution))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RoleInstitution in the database
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRoleInstitution() throws Exception {
        // Initialize the database
        roleInstitutionRepository.saveAndFlush(roleInstitution);
        roleInstitutionRepository.save(roleInstitution);
        roleInstitutionSearchRepository.save(roleInstitution);

        int databaseSizeBeforeDelete = roleInstitutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the roleInstitution
        restRoleInstitutionMockMvc
            .perform(delete(ENTITY_API_URL_ID, roleInstitution.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RoleInstitution> roleInstitutionList = roleInstitutionRepository.findAll();
        assertThat(roleInstitutionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(roleInstitutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRoleInstitution() throws Exception {
        // Initialize the database
        roleInstitution = roleInstitutionRepository.saveAndFlush(roleInstitution);
        roleInstitutionSearchRepository.save(roleInstitution);

        // Search the roleInstitution
        restRoleInstitutionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + roleInstitution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roleInstitution.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())))
            .andExpect(jsonPath("$.[*].end").value(hasItem(DEFAULT_END.toString())));
    }
}
