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
import sn.finedev.java.domain.Functionality;
import sn.finedev.java.domain.FunctionalityCategory;
import sn.finedev.java.domain.MobileBankingActor;
import sn.finedev.java.domain.enumeration.FunctionalityStatus;
import sn.finedev.java.repository.FunctionalityRepository;
import sn.finedev.java.repository.search.FunctionalitySearchRepository;
import sn.finedev.java.service.criteria.FunctionalityCriteria;
import sn.finedev.java.service.dto.FunctionalityDTO;
import sn.finedev.java.service.mapper.FunctionalityMapper;

/**
 * Integration tests for the {@link FunctionalityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FunctionalityResourceIT {

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final FunctionalityStatus DEFAULT_STATUS = FunctionalityStatus.UNAVAILABLE;
    private static final FunctionalityStatus UPDATED_STATUS = FunctionalityStatus.AVAILABLE;

    private static final String ENTITY_API_URL = "/api/functionalities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/functionalities";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Autowired
    private FunctionalityMapper functionalityMapper;

    @Autowired
    private FunctionalitySearchRepository functionalitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFunctionalityMockMvc;

    private Functionality functionality;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Functionality createEntity(EntityManager em) {
        Functionality functionality = new Functionality()
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .status(DEFAULT_STATUS);
        return functionality;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Functionality createUpdatedEntity(EntityManager em) {
        Functionality functionality = new Functionality()
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .status(UPDATED_STATUS);
        return functionality;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        functionalitySearchRepository.deleteAll();
        assertThat(functionalitySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        functionality = createEntity(em);
    }

    @Test
    @Transactional
    void createFunctionality() throws Exception {
        int databaseSizeBeforeCreate = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        // Create the Functionality
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);
        restFunctionalityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Functionality testFunctionality = functionalityList.get(functionalityList.size() - 1);
        assertThat(testFunctionality.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testFunctionality.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testFunctionality.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createFunctionalityWithExistingId() throws Exception {
        // Create the Functionality with an existing ID
        functionality.setId(1L);
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);

        int databaseSizeBeforeCreate = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFunctionalityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        // set the field null
        functionality.setStatus(null);

        // Create the Functionality, which fails.
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);

        restFunctionalityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isBadRequest());

        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllFunctionalities() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get all the functionalityList
        restFunctionalityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionality.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getFunctionality() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get the functionality
        restFunctionalityMockMvc
            .perform(get(ENTITY_API_URL_ID, functionality.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(functionality.getId().intValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getFunctionalitiesByIdFiltering() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        Long id = functionality.getId();

        defaultFunctionalityShouldBeFound("id.equals=" + id);
        defaultFunctionalityShouldNotBeFound("id.notEquals=" + id);

        defaultFunctionalityShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFunctionalityShouldNotBeFound("id.greaterThan=" + id);

        defaultFunctionalityShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFunctionalityShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFunctionalitiesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get all the functionalityList where status equals to DEFAULT_STATUS
        defaultFunctionalityShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the functionalityList where status equals to UPDATED_STATUS
        defaultFunctionalityShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllFunctionalitiesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get all the functionalityList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultFunctionalityShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the functionalityList where status equals to UPDATED_STATUS
        defaultFunctionalityShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllFunctionalitiesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get all the functionalityList where status is not null
        defaultFunctionalityShouldBeFound("status.specified=true");

        // Get all the functionalityList where status is null
        defaultFunctionalityShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllFunctionalitiesByFunctionalityCategoryIsEqualToSomething() throws Exception {
        FunctionalityCategory functionalityCategory;
        if (TestUtil.findAll(em, FunctionalityCategory.class).isEmpty()) {
            functionalityRepository.saveAndFlush(functionality);
            functionalityCategory = FunctionalityCategoryResourceIT.createEntity(em);
        } else {
            functionalityCategory = TestUtil.findAll(em, FunctionalityCategory.class).get(0);
        }
        em.persist(functionalityCategory);
        em.flush();
        functionality.setFunctionalityCategory(functionalityCategory);
        functionalityRepository.saveAndFlush(functionality);
        Long functionalityCategoryId = functionalityCategory.getId();

        // Get all the functionalityList where functionalityCategory equals to functionalityCategoryId
        defaultFunctionalityShouldBeFound("functionalityCategoryId.equals=" + functionalityCategoryId);

        // Get all the functionalityList where functionalityCategory equals to (functionalityCategoryId + 1)
        defaultFunctionalityShouldNotBeFound("functionalityCategoryId.equals=" + (functionalityCategoryId + 1));
    }

    @Test
    @Transactional
    void getAllFunctionalitiesByMobileBankingActorIsEqualToSomething() throws Exception {
        MobileBankingActor mobileBankingActor;
        if (TestUtil.findAll(em, MobileBankingActor.class).isEmpty()) {
            functionalityRepository.saveAndFlush(functionality);
            mobileBankingActor = MobileBankingActorResourceIT.createEntity(em);
        } else {
            mobileBankingActor = TestUtil.findAll(em, MobileBankingActor.class).get(0);
        }
        em.persist(mobileBankingActor);
        em.flush();
        functionality.addMobileBankingActor(mobileBankingActor);
        functionalityRepository.saveAndFlush(functionality);
        Long mobileBankingActorId = mobileBankingActor.getId();

        // Get all the functionalityList where mobileBankingActor equals to mobileBankingActorId
        defaultFunctionalityShouldBeFound("mobileBankingActorId.equals=" + mobileBankingActorId);

        // Get all the functionalityList where mobileBankingActor equals to (mobileBankingActorId + 1)
        defaultFunctionalityShouldNotBeFound("mobileBankingActorId.equals=" + (mobileBankingActorId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFunctionalityShouldBeFound(String filter) throws Exception {
        restFunctionalityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionality.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restFunctionalityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFunctionalityShouldNotBeFound(String filter) throws Exception {
        restFunctionalityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFunctionalityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFunctionality() throws Exception {
        // Get the functionality
        restFunctionalityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFunctionality() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();
        functionalitySearchRepository.save(functionality);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());

        // Update the functionality
        Functionality updatedFunctionality = functionalityRepository.findById(functionality.getId()).get();
        // Disconnect from session so that the updates on updatedFunctionality are not directly saved in db
        em.detach(updatedFunctionality);
        updatedFunctionality.image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE).status(UPDATED_STATUS);
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(updatedFunctionality);

        restFunctionalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, functionalityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        Functionality testFunctionality = functionalityList.get(functionalityList.size() - 1);
        assertThat(testFunctionality.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testFunctionality.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testFunctionality.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Functionality> functionalitySearchList = IterableUtils.toList(functionalitySearchRepository.findAll());
                Functionality testFunctionalitySearch = functionalitySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFunctionalitySearch.getImage()).isEqualTo(UPDATED_IMAGE);
                assertThat(testFunctionalitySearch.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
                assertThat(testFunctionalitySearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingFunctionality() throws Exception {
        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        functionality.setId(count.incrementAndGet());

        // Create the Functionality
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFunctionalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, functionalityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFunctionality() throws Exception {
        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        functionality.setId(count.incrementAndGet());

        // Create the Functionality
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFunctionalityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFunctionality() throws Exception {
        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        functionality.setId(count.incrementAndGet());

        // Create the Functionality
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFunctionalityMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateFunctionalityWithPatch() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();

        // Update the functionality using partial update
        Functionality partialUpdatedFunctionality = new Functionality();
        partialUpdatedFunctionality.setId(functionality.getId());

        restFunctionalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFunctionality.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFunctionality))
            )
            .andExpect(status().isOk());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        Functionality testFunctionality = functionalityList.get(functionalityList.size() - 1);
        assertThat(testFunctionality.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testFunctionality.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testFunctionality.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateFunctionalityWithPatch() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();

        // Update the functionality using partial update
        Functionality partialUpdatedFunctionality = new Functionality();
        partialUpdatedFunctionality.setId(functionality.getId());

        partialUpdatedFunctionality.image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE).status(UPDATED_STATUS);

        restFunctionalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFunctionality.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFunctionality))
            )
            .andExpect(status().isOk());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        Functionality testFunctionality = functionalityList.get(functionalityList.size() - 1);
        assertThat(testFunctionality.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testFunctionality.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testFunctionality.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingFunctionality() throws Exception {
        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        functionality.setId(count.incrementAndGet());

        // Create the Functionality
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFunctionalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, functionalityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFunctionality() throws Exception {
        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        functionality.setId(count.incrementAndGet());

        // Create the Functionality
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFunctionalityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFunctionality() throws Exception {
        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        functionality.setId(count.incrementAndGet());

        // Create the Functionality
        FunctionalityDTO functionalityDTO = functionalityMapper.toDto(functionality);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFunctionalityMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(functionalityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteFunctionality() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);
        functionalityRepository.save(functionality);
        functionalitySearchRepository.save(functionality);

        int databaseSizeBeforeDelete = functionalityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the functionality
        restFunctionalityMockMvc
            .perform(delete(ENTITY_API_URL_ID, functionality.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(functionalitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchFunctionality() throws Exception {
        // Initialize the database
        functionality = functionalityRepository.saveAndFlush(functionality);
        functionalitySearchRepository.save(functionality);

        // Search the functionality
        restFunctionalityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + functionality.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionality.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
