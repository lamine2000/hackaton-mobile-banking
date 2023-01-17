package sn.finedev.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import sn.finedev.java.domain.MobileBankingActor;
import sn.finedev.java.domain.enumeration.MobileBankingActorStatus;
import sn.finedev.java.repository.MobileBankingActorRepository;
import sn.finedev.java.repository.search.MobileBankingActorSearchRepository;
import sn.finedev.java.service.MobileBankingActorService;
import sn.finedev.java.service.criteria.MobileBankingActorCriteria;
import sn.finedev.java.service.dto.MobileBankingActorDTO;
import sn.finedev.java.service.mapper.MobileBankingActorMapper;

/**
 * Integration tests for the {@link MobileBankingActorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MobileBankingActorResourceIT {

    private static final byte[] DEFAULT_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final MobileBankingActorStatus DEFAULT_STATUS = MobileBankingActorStatus.AVAILABLE;
    private static final MobileBankingActorStatus UPDATED_STATUS = MobileBankingActorStatus.UNAVAILABLE;

    private static final String ENTITY_API_URL = "/api/mobile-banking-actors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/mobile-banking-actors";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MobileBankingActorRepository mobileBankingActorRepository;

    @Mock
    private MobileBankingActorRepository mobileBankingActorRepositoryMock;

    @Autowired
    private MobileBankingActorMapper mobileBankingActorMapper;

    @Mock
    private MobileBankingActorService mobileBankingActorServiceMock;

    @Autowired
    private MobileBankingActorSearchRepository mobileBankingActorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMobileBankingActorMockMvc;

    private MobileBankingActor mobileBankingActor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MobileBankingActor createEntity(EntityManager em) {
        MobileBankingActor mobileBankingActor = new MobileBankingActor()
            .logo(DEFAULT_LOGO)
            .logoContentType(DEFAULT_LOGO_CONTENT_TYPE)
            .name(DEFAULT_NAME)
            .status(DEFAULT_STATUS);
        return mobileBankingActor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MobileBankingActor createUpdatedEntity(EntityManager em) {
        MobileBankingActor mobileBankingActor = new MobileBankingActor()
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS);
        return mobileBankingActor;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        mobileBankingActorSearchRepository.deleteAll();
        assertThat(mobileBankingActorSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        mobileBankingActor = createEntity(em);
    }

    @Test
    @Transactional
    void createMobileBankingActor() throws Exception {
        int databaseSizeBeforeCreate = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        // Create the MobileBankingActor
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);
        restMobileBankingActorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isCreated());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        MobileBankingActor testMobileBankingActor = mobileBankingActorList.get(mobileBankingActorList.size() - 1);
        assertThat(testMobileBankingActor.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testMobileBankingActor.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testMobileBankingActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMobileBankingActor.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createMobileBankingActorWithExistingId() throws Exception {
        // Create the MobileBankingActor with an existing ID
        mobileBankingActor.setId(1L);
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        int databaseSizeBeforeCreate = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMobileBankingActorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        // set the field null
        mobileBankingActor.setName(null);

        // Create the MobileBankingActor, which fails.
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        restMobileBankingActorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isBadRequest());

        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        // set the field null
        mobileBankingActor.setStatus(null);

        // Create the MobileBankingActor, which fails.
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        restMobileBankingActorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isBadRequest());

        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMobileBankingActors() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList
        restMobileBankingActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mobileBankingActor.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMobileBankingActorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(mobileBankingActorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMobileBankingActorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(mobileBankingActorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMobileBankingActorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(mobileBankingActorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMobileBankingActorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(mobileBankingActorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMobileBankingActor() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get the mobileBankingActor
        restMobileBankingActorMockMvc
            .perform(get(ENTITY_API_URL_ID, mobileBankingActor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mobileBankingActor.getId().intValue()))
            .andExpect(jsonPath("$.logoContentType").value(DEFAULT_LOGO_CONTENT_TYPE))
            .andExpect(jsonPath("$.logo").value(Base64Utils.encodeToString(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getMobileBankingActorsByIdFiltering() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        Long id = mobileBankingActor.getId();

        defaultMobileBankingActorShouldBeFound("id.equals=" + id);
        defaultMobileBankingActorShouldNotBeFound("id.notEquals=" + id);

        defaultMobileBankingActorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMobileBankingActorShouldNotBeFound("id.greaterThan=" + id);

        defaultMobileBankingActorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMobileBankingActorShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList where name equals to DEFAULT_NAME
        defaultMobileBankingActorShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the mobileBankingActorList where name equals to UPDATED_NAME
        defaultMobileBankingActorShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMobileBankingActorShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the mobileBankingActorList where name equals to UPDATED_NAME
        defaultMobileBankingActorShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList where name is not null
        defaultMobileBankingActorShouldBeFound("name.specified=true");

        // Get all the mobileBankingActorList where name is null
        defaultMobileBankingActorShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByNameContainsSomething() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList where name contains DEFAULT_NAME
        defaultMobileBankingActorShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the mobileBankingActorList where name contains UPDATED_NAME
        defaultMobileBankingActorShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList where name does not contain DEFAULT_NAME
        defaultMobileBankingActorShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the mobileBankingActorList where name does not contain UPDATED_NAME
        defaultMobileBankingActorShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList where status equals to DEFAULT_STATUS
        defaultMobileBankingActorShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the mobileBankingActorList where status equals to UPDATED_STATUS
        defaultMobileBankingActorShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultMobileBankingActorShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the mobileBankingActorList where status equals to UPDATED_STATUS
        defaultMobileBankingActorShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        // Get all the mobileBankingActorList where status is not null
        defaultMobileBankingActorShouldBeFound("status.specified=true");

        // Get all the mobileBankingActorList where status is null
        defaultMobileBankingActorShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllMobileBankingActorsByFunctionalityIsEqualToSomething() throws Exception {
        Functionality functionality;
        if (TestUtil.findAll(em, Functionality.class).isEmpty()) {
            mobileBankingActorRepository.saveAndFlush(mobileBankingActor);
            functionality = FunctionalityResourceIT.createEntity(em);
        } else {
            functionality = TestUtil.findAll(em, Functionality.class).get(0);
        }
        em.persist(functionality);
        em.flush();
        mobileBankingActor.addFunctionality(functionality);
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);
        Long functionalityId = functionality.getId();

        // Get all the mobileBankingActorList where functionality equals to functionalityId
        defaultMobileBankingActorShouldBeFound("functionalityId.equals=" + functionalityId);

        // Get all the mobileBankingActorList where functionality equals to (functionalityId + 1)
        defaultMobileBankingActorShouldNotBeFound("functionalityId.equals=" + (functionalityId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMobileBankingActorShouldBeFound(String filter) throws Exception {
        restMobileBankingActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mobileBankingActor.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restMobileBankingActorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMobileBankingActorShouldNotBeFound(String filter) throws Exception {
        restMobileBankingActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMobileBankingActorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMobileBankingActor() throws Exception {
        // Get the mobileBankingActor
        restMobileBankingActorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMobileBankingActor() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();
        mobileBankingActorSearchRepository.save(mobileBankingActor);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());

        // Update the mobileBankingActor
        MobileBankingActor updatedMobileBankingActor = mobileBankingActorRepository.findById(mobileBankingActor.getId()).get();
        // Disconnect from session so that the updates on updatedMobileBankingActor are not directly saved in db
        em.detach(updatedMobileBankingActor);
        updatedMobileBankingActor.logo(UPDATED_LOGO).logoContentType(UPDATED_LOGO_CONTENT_TYPE).name(UPDATED_NAME).status(UPDATED_STATUS);
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(updatedMobileBankingActor);

        restMobileBankingActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mobileBankingActorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isOk());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        MobileBankingActor testMobileBankingActor = mobileBankingActorList.get(mobileBankingActorList.size() - 1);
        assertThat(testMobileBankingActor.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testMobileBankingActor.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testMobileBankingActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMobileBankingActor.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MobileBankingActor> mobileBankingActorSearchList = IterableUtils.toList(mobileBankingActorSearchRepository.findAll());
                MobileBankingActor testMobileBankingActorSearch = mobileBankingActorSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testMobileBankingActorSearch.getLogo()).isEqualTo(UPDATED_LOGO);
                assertThat(testMobileBankingActorSearch.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
                assertThat(testMobileBankingActorSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testMobileBankingActorSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingMobileBankingActor() throws Exception {
        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        mobileBankingActor.setId(count.incrementAndGet());

        // Create the MobileBankingActor
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMobileBankingActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mobileBankingActorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMobileBankingActor() throws Exception {
        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        mobileBankingActor.setId(count.incrementAndGet());

        // Create the MobileBankingActor
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMobileBankingActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMobileBankingActor() throws Exception {
        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        mobileBankingActor.setId(count.incrementAndGet());

        // Create the MobileBankingActor
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMobileBankingActorMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMobileBankingActorWithPatch() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();

        // Update the mobileBankingActor using partial update
        MobileBankingActor partialUpdatedMobileBankingActor = new MobileBankingActor();
        partialUpdatedMobileBankingActor.setId(mobileBankingActor.getId());

        partialUpdatedMobileBankingActor.status(UPDATED_STATUS);

        restMobileBankingActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMobileBankingActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMobileBankingActor))
            )
            .andExpect(status().isOk());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        MobileBankingActor testMobileBankingActor = mobileBankingActorList.get(mobileBankingActorList.size() - 1);
        assertThat(testMobileBankingActor.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testMobileBankingActor.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testMobileBankingActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMobileBankingActor.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateMobileBankingActorWithPatch() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);

        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();

        // Update the mobileBankingActor using partial update
        MobileBankingActor partialUpdatedMobileBankingActor = new MobileBankingActor();
        partialUpdatedMobileBankingActor.setId(mobileBankingActor.getId());

        partialUpdatedMobileBankingActor
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS);

        restMobileBankingActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMobileBankingActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMobileBankingActor))
            )
            .andExpect(status().isOk());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        MobileBankingActor testMobileBankingActor = mobileBankingActorList.get(mobileBankingActorList.size() - 1);
        assertThat(testMobileBankingActor.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testMobileBankingActor.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testMobileBankingActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMobileBankingActor.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingMobileBankingActor() throws Exception {
        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        mobileBankingActor.setId(count.incrementAndGet());

        // Create the MobileBankingActor
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMobileBankingActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mobileBankingActorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMobileBankingActor() throws Exception {
        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        mobileBankingActor.setId(count.incrementAndGet());

        // Create the MobileBankingActor
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMobileBankingActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMobileBankingActor() throws Exception {
        int databaseSizeBeforeUpdate = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        mobileBankingActor.setId(count.incrementAndGet());

        // Create the MobileBankingActor
        MobileBankingActorDTO mobileBankingActorDTO = mobileBankingActorMapper.toDto(mobileBankingActor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMobileBankingActorMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mobileBankingActorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MobileBankingActor in the database
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMobileBankingActor() throws Exception {
        // Initialize the database
        mobileBankingActorRepository.saveAndFlush(mobileBankingActor);
        mobileBankingActorRepository.save(mobileBankingActor);
        mobileBankingActorSearchRepository.save(mobileBankingActor);

        int databaseSizeBeforeDelete = mobileBankingActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the mobileBankingActor
        restMobileBankingActorMockMvc
            .perform(delete(ENTITY_API_URL_ID, mobileBankingActor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MobileBankingActor> mobileBankingActorList = mobileBankingActorRepository.findAll();
        assertThat(mobileBankingActorList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(mobileBankingActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMobileBankingActor() throws Exception {
        // Initialize the database
        mobileBankingActor = mobileBankingActorRepository.saveAndFlush(mobileBankingActor);
        mobileBankingActorSearchRepository.save(mobileBankingActor);

        // Search the mobileBankingActor
        restMobileBankingActorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + mobileBankingActor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mobileBankingActor.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
