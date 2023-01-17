package sn.finedev.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import sn.finedev.java.IntegrationTest;
import sn.finedev.java.domain.FunctionalityCategory;
import sn.finedev.java.domain.enumeration.FunctionalityCategoryStatus;
import sn.finedev.java.repository.FunctionalityCategoryRepository;
import sn.finedev.java.repository.search.FunctionalityCategorySearchRepository;
import sn.finedev.java.service.criteria.FunctionalityCategoryCriteria;
import sn.finedev.java.service.dto.FunctionalityCategoryDTO;
import sn.finedev.java.service.mapper.FunctionalityCategoryMapper;

/**
 * Integration tests for the {@link FunctionalityCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FunctionalityCategoryResourceIT {

    private static final byte[] DEFAULT_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_CONTENT_TYPE = "image/png";

    private static final FunctionalityCategoryStatus DEFAULT_STATUS = FunctionalityCategoryStatus.UNAVAILABLE;
    private static final FunctionalityCategoryStatus UPDATED_STATUS = FunctionalityCategoryStatus.AVAILABLE;

    private static final String ENTITY_API_URL = "/api/functionality-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/functionality-categories";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FunctionalityCategoryRepository functionalityCategoryRepository;

    @Autowired
    private FunctionalityCategoryMapper functionalityCategoryMapper;

    @Autowired
    private FunctionalityCategorySearchRepository functionalityCategorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFunctionalityCategoryMockMvc;

    private FunctionalityCategory functionalityCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FunctionalityCategory createEntity(EntityManager em) {
        FunctionalityCategory functionalityCategory = new FunctionalityCategory()
            .logo(DEFAULT_LOGO)
            .logoContentType(DEFAULT_LOGO_CONTENT_TYPE)
            .status(DEFAULT_STATUS);
        return functionalityCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FunctionalityCategory createUpdatedEntity(EntityManager em) {
        FunctionalityCategory functionalityCategory = new FunctionalityCategory()
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .status(UPDATED_STATUS);
        return functionalityCategory;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        functionalityCategorySearchRepository.deleteAll();
        assertThat(functionalityCategorySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        functionalityCategory = createEntity(em);
    }

    @Test
    @Transactional
    void createFunctionalityCategory() throws Exception {
        int databaseSizeBeforeCreate = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        // Create the FunctionalityCategory
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);
        restFunctionalityCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        FunctionalityCategory testFunctionalityCategory = functionalityCategoryList.get(functionalityCategoryList.size() - 1);
        assertThat(testFunctionalityCategory.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testFunctionalityCategory.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testFunctionalityCategory.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createFunctionalityCategoryWithExistingId() throws Exception {
        // Create the FunctionalityCategory with an existing ID
        functionalityCategory.setId(1L);
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);

        int databaseSizeBeforeCreate = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFunctionalityCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        // set the field null
        functionalityCategory.setStatus(null);

        // Create the FunctionalityCategory, which fails.
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);

        restFunctionalityCategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllFunctionalityCategories() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        // Get all the functionalityCategoryList
        restFunctionalityCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionalityCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getFunctionalityCategory() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        // Get the functionalityCategory
        restFunctionalityCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, functionalityCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(functionalityCategory.getId().intValue()))
            .andExpect(jsonPath("$.logoContentType").value(DEFAULT_LOGO_CONTENT_TYPE))
            .andExpect(jsonPath("$.logo").value(Base64Utils.encodeToString(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getFunctionalityCategoriesByIdFiltering() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        Long id = functionalityCategory.getId();

        defaultFunctionalityCategoryShouldBeFound("id.equals=" + id);
        defaultFunctionalityCategoryShouldNotBeFound("id.notEquals=" + id);

        defaultFunctionalityCategoryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFunctionalityCategoryShouldNotBeFound("id.greaterThan=" + id);

        defaultFunctionalityCategoryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFunctionalityCategoryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFunctionalityCategoriesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        // Get all the functionalityCategoryList where status equals to DEFAULT_STATUS
        defaultFunctionalityCategoryShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the functionalityCategoryList where status equals to UPDATED_STATUS
        defaultFunctionalityCategoryShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllFunctionalityCategoriesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        // Get all the functionalityCategoryList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultFunctionalityCategoryShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the functionalityCategoryList where status equals to UPDATED_STATUS
        defaultFunctionalityCategoryShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllFunctionalityCategoriesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        // Get all the functionalityCategoryList where status is not null
        defaultFunctionalityCategoryShouldBeFound("status.specified=true");

        // Get all the functionalityCategoryList where status is null
        defaultFunctionalityCategoryShouldNotBeFound("status.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFunctionalityCategoryShouldBeFound(String filter) throws Exception {
        restFunctionalityCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionalityCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restFunctionalityCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFunctionalityCategoryShouldNotBeFound(String filter) throws Exception {
        restFunctionalityCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFunctionalityCategoryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFunctionalityCategory() throws Exception {
        // Get the functionalityCategory
        restFunctionalityCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFunctionalityCategory() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();
        functionalityCategorySearchRepository.save(functionalityCategory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());

        // Update the functionalityCategory
        FunctionalityCategory updatedFunctionalityCategory = functionalityCategoryRepository.findById(functionalityCategory.getId()).get();
        // Disconnect from session so that the updates on updatedFunctionalityCategory are not directly saved in db
        em.detach(updatedFunctionalityCategory);
        updatedFunctionalityCategory.logo(UPDATED_LOGO).logoContentType(UPDATED_LOGO_CONTENT_TYPE).status(UPDATED_STATUS);
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(updatedFunctionalityCategory);

        restFunctionalityCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, functionalityCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        FunctionalityCategory testFunctionalityCategory = functionalityCategoryList.get(functionalityCategoryList.size() - 1);
        assertThat(testFunctionalityCategory.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testFunctionalityCategory.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testFunctionalityCategory.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<FunctionalityCategory> functionalityCategorySearchList = IterableUtils.toList(
                    functionalityCategorySearchRepository.findAll()
                );
                FunctionalityCategory testFunctionalityCategorySearch = functionalityCategorySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFunctionalityCategorySearch.getLogo()).isEqualTo(UPDATED_LOGO);
                assertThat(testFunctionalityCategorySearch.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
                assertThat(testFunctionalityCategorySearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingFunctionalityCategory() throws Exception {
        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        functionalityCategory.setId(count.incrementAndGet());

        // Create the FunctionalityCategory
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFunctionalityCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, functionalityCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFunctionalityCategory() throws Exception {
        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        functionalityCategory.setId(count.incrementAndGet());

        // Create the FunctionalityCategory
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFunctionalityCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFunctionalityCategory() throws Exception {
        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        functionalityCategory.setId(count.incrementAndGet());

        // Create the FunctionalityCategory
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFunctionalityCategoryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateFunctionalityCategoryWithPatch() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();

        // Update the functionalityCategory using partial update
        FunctionalityCategory partialUpdatedFunctionalityCategory = new FunctionalityCategory();
        partialUpdatedFunctionalityCategory.setId(functionalityCategory.getId());

        restFunctionalityCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFunctionalityCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFunctionalityCategory))
            )
            .andExpect(status().isOk());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        FunctionalityCategory testFunctionalityCategory = functionalityCategoryList.get(functionalityCategoryList.size() - 1);
        assertThat(testFunctionalityCategory.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testFunctionalityCategory.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testFunctionalityCategory.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateFunctionalityCategoryWithPatch() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);

        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();

        // Update the functionalityCategory using partial update
        FunctionalityCategory partialUpdatedFunctionalityCategory = new FunctionalityCategory();
        partialUpdatedFunctionalityCategory.setId(functionalityCategory.getId());

        partialUpdatedFunctionalityCategory.logo(UPDATED_LOGO).logoContentType(UPDATED_LOGO_CONTENT_TYPE).status(UPDATED_STATUS);

        restFunctionalityCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFunctionalityCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFunctionalityCategory))
            )
            .andExpect(status().isOk());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        FunctionalityCategory testFunctionalityCategory = functionalityCategoryList.get(functionalityCategoryList.size() - 1);
        assertThat(testFunctionalityCategory.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testFunctionalityCategory.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testFunctionalityCategory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingFunctionalityCategory() throws Exception {
        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        functionalityCategory.setId(count.incrementAndGet());

        // Create the FunctionalityCategory
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFunctionalityCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, functionalityCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFunctionalityCategory() throws Exception {
        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        functionalityCategory.setId(count.incrementAndGet());

        // Create the FunctionalityCategory
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFunctionalityCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFunctionalityCategory() throws Exception {
        int databaseSizeBeforeUpdate = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        functionalityCategory.setId(count.incrementAndGet());

        // Create the FunctionalityCategory
        FunctionalityCategoryDTO functionalityCategoryDTO = functionalityCategoryMapper.toDto(functionalityCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFunctionalityCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(functionalityCategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FunctionalityCategory in the database
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteFunctionalityCategory() throws Exception {
        // Initialize the database
        functionalityCategoryRepository.saveAndFlush(functionalityCategory);
        functionalityCategoryRepository.save(functionalityCategory);
        functionalityCategorySearchRepository.save(functionalityCategory);

        int databaseSizeBeforeDelete = functionalityCategoryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the functionalityCategory
        restFunctionalityCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, functionalityCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FunctionalityCategory> functionalityCategoryList = functionalityCategoryRepository.findAll();
        assertThat(functionalityCategoryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalityCategorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchFunctionalityCategory() throws Exception {
        // Initialize the database
        functionalityCategory = functionalityCategoryRepository.saveAndFlush(functionalityCategory);
        functionalityCategorySearchRepository.save(functionalityCategory);

        // Search the functionalityCategory
        restFunctionalityCategoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + functionalityCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionalityCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
