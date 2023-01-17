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
import sn.finedev.java.domain.InsuranceAndMicroCreditsActor;
import sn.finedev.java.repository.InsuranceAndMicroCreditsActorRepository;
import sn.finedev.java.repository.search.InsuranceAndMicroCreditsActorSearchRepository;
import sn.finedev.java.service.criteria.InsuranceAndMicroCreditsActorCriteria;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsActorDTO;
import sn.finedev.java.service.mapper.InsuranceAndMicroCreditsActorMapper;

/**
 * Integration tests for the {@link InsuranceAndMicroCreditsActorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InsuranceAndMicroCreditsActorResourceIT {

    private static final byte[] DEFAULT_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ACRONYM = "AAAAAAAAAA";
    private static final String UPDATED_ACRONYM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/insurance-and-micro-credits-actors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/insurance-and-micro-credits-actors";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InsuranceAndMicroCreditsActorRepository insuranceAndMicroCreditsActorRepository;

    @Autowired
    private InsuranceAndMicroCreditsActorMapper insuranceAndMicroCreditsActorMapper;

    @Autowired
    private InsuranceAndMicroCreditsActorSearchRepository insuranceAndMicroCreditsActorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInsuranceAndMicroCreditsActorMockMvc;

    private InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InsuranceAndMicroCreditsActor createEntity(EntityManager em) {
        InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor = new InsuranceAndMicroCreditsActor()
            .logo(DEFAULT_LOGO)
            .logoContentType(DEFAULT_LOGO_CONTENT_TYPE)
            .name(DEFAULT_NAME)
            .acronym(DEFAULT_ACRONYM)
            .description(DEFAULT_DESCRIPTION);
        return insuranceAndMicroCreditsActor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InsuranceAndMicroCreditsActor createUpdatedEntity(EntityManager em) {
        InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor = new InsuranceAndMicroCreditsActor()
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .name(UPDATED_NAME)
            .acronym(UPDATED_ACRONYM)
            .description(UPDATED_DESCRIPTION);
        return insuranceAndMicroCreditsActor;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        insuranceAndMicroCreditsActorSearchRepository.deleteAll();
        assertThat(insuranceAndMicroCreditsActorSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        insuranceAndMicroCreditsActor = createEntity(em);
    }

    @Test
    @Transactional
    void createInsuranceAndMicroCreditsActor() throws Exception {
        int databaseSizeBeforeCreate = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        // Create the InsuranceAndMicroCreditsActor
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isCreated());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        InsuranceAndMicroCreditsActor testInsuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorList.get(
            insuranceAndMicroCreditsActorList.size() - 1
        );
        assertThat(testInsuranceAndMicroCreditsActor.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testInsuranceAndMicroCreditsActor.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testInsuranceAndMicroCreditsActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInsuranceAndMicroCreditsActor.getAcronym()).isEqualTo(DEFAULT_ACRONYM);
        assertThat(testInsuranceAndMicroCreditsActor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createInsuranceAndMicroCreditsActorWithExistingId() throws Exception {
        // Create the InsuranceAndMicroCreditsActor with an existing ID
        insuranceAndMicroCreditsActor.setId(1L);
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );

        int databaseSizeBeforeCreate = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        // set the field null
        insuranceAndMicroCreditsActor.setName(null);

        // Create the InsuranceAndMicroCreditsActor, which fails.
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );

        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isBadRequest());

        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActors() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insuranceAndMicroCreditsActor.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].acronym").value(hasItem(DEFAULT_ACRONYM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getInsuranceAndMicroCreditsActor() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get the insuranceAndMicroCreditsActor
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(get(ENTITY_API_URL_ID, insuranceAndMicroCreditsActor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(insuranceAndMicroCreditsActor.getId().intValue()))
            .andExpect(jsonPath("$.logoContentType").value(DEFAULT_LOGO_CONTENT_TYPE))
            .andExpect(jsonPath("$.logo").value(Base64Utils.encodeToString(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.acronym").value(DEFAULT_ACRONYM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getInsuranceAndMicroCreditsActorsByIdFiltering() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        Long id = insuranceAndMicroCreditsActor.getId();

        defaultInsuranceAndMicroCreditsActorShouldBeFound("id.equals=" + id);
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("id.notEquals=" + id);

        defaultInsuranceAndMicroCreditsActorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("id.greaterThan=" + id);

        defaultInsuranceAndMicroCreditsActorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where name equals to DEFAULT_NAME
        defaultInsuranceAndMicroCreditsActorShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the insuranceAndMicroCreditsActorList where name equals to UPDATED_NAME
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where name in DEFAULT_NAME or UPDATED_NAME
        defaultInsuranceAndMicroCreditsActorShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the insuranceAndMicroCreditsActorList where name equals to UPDATED_NAME
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where name is not null
        defaultInsuranceAndMicroCreditsActorShouldBeFound("name.specified=true");

        // Get all the insuranceAndMicroCreditsActorList where name is null
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByNameContainsSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where name contains DEFAULT_NAME
        defaultInsuranceAndMicroCreditsActorShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the insuranceAndMicroCreditsActorList where name contains UPDATED_NAME
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where name does not contain DEFAULT_NAME
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the insuranceAndMicroCreditsActorList where name does not contain UPDATED_NAME
        defaultInsuranceAndMicroCreditsActorShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByAcronymIsEqualToSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where acronym equals to DEFAULT_ACRONYM
        defaultInsuranceAndMicroCreditsActorShouldBeFound("acronym.equals=" + DEFAULT_ACRONYM);

        // Get all the insuranceAndMicroCreditsActorList where acronym equals to UPDATED_ACRONYM
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("acronym.equals=" + UPDATED_ACRONYM);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByAcronymIsInShouldWork() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where acronym in DEFAULT_ACRONYM or UPDATED_ACRONYM
        defaultInsuranceAndMicroCreditsActorShouldBeFound("acronym.in=" + DEFAULT_ACRONYM + "," + UPDATED_ACRONYM);

        // Get all the insuranceAndMicroCreditsActorList where acronym equals to UPDATED_ACRONYM
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("acronym.in=" + UPDATED_ACRONYM);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByAcronymIsNullOrNotNull() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where acronym is not null
        defaultInsuranceAndMicroCreditsActorShouldBeFound("acronym.specified=true");

        // Get all the insuranceAndMicroCreditsActorList where acronym is null
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("acronym.specified=false");
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByAcronymContainsSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where acronym contains DEFAULT_ACRONYM
        defaultInsuranceAndMicroCreditsActorShouldBeFound("acronym.contains=" + DEFAULT_ACRONYM);

        // Get all the insuranceAndMicroCreditsActorList where acronym contains UPDATED_ACRONYM
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("acronym.contains=" + UPDATED_ACRONYM);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsActorsByAcronymNotContainsSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        // Get all the insuranceAndMicroCreditsActorList where acronym does not contain DEFAULT_ACRONYM
        defaultInsuranceAndMicroCreditsActorShouldNotBeFound("acronym.doesNotContain=" + DEFAULT_ACRONYM);

        // Get all the insuranceAndMicroCreditsActorList where acronym does not contain UPDATED_ACRONYM
        defaultInsuranceAndMicroCreditsActorShouldBeFound("acronym.doesNotContain=" + UPDATED_ACRONYM);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInsuranceAndMicroCreditsActorShouldBeFound(String filter) throws Exception {
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insuranceAndMicroCreditsActor.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].acronym").value(hasItem(DEFAULT_ACRONYM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));

        // Check, that the count call also returns 1
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInsuranceAndMicroCreditsActorShouldNotBeFound(String filter) throws Exception {
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInsuranceAndMicroCreditsActor() throws Exception {
        // Get the insuranceAndMicroCreditsActor
        restInsuranceAndMicroCreditsActorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInsuranceAndMicroCreditsActor() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();
        insuranceAndMicroCreditsActorSearchRepository.save(insuranceAndMicroCreditsActor);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());

        // Update the insuranceAndMicroCreditsActor
        InsuranceAndMicroCreditsActor updatedInsuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorRepository
            .findById(insuranceAndMicroCreditsActor.getId())
            .get();
        // Disconnect from session so that the updates on updatedInsuranceAndMicroCreditsActor are not directly saved in db
        em.detach(updatedInsuranceAndMicroCreditsActor);
        updatedInsuranceAndMicroCreditsActor
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .name(UPDATED_NAME)
            .acronym(UPDATED_ACRONYM)
            .description(UPDATED_DESCRIPTION);
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            updatedInsuranceAndMicroCreditsActor
        );

        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, insuranceAndMicroCreditsActorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isOk());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        InsuranceAndMicroCreditsActor testInsuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorList.get(
            insuranceAndMicroCreditsActorList.size() - 1
        );
        assertThat(testInsuranceAndMicroCreditsActor.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testInsuranceAndMicroCreditsActor.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testInsuranceAndMicroCreditsActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInsuranceAndMicroCreditsActor.getAcronym()).isEqualTo(UPDATED_ACRONYM);
        assertThat(testInsuranceAndMicroCreditsActor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorSearchList = IterableUtils.toList(
                    insuranceAndMicroCreditsActorSearchRepository.findAll()
                );
                InsuranceAndMicroCreditsActor testInsuranceAndMicroCreditsActorSearch = insuranceAndMicroCreditsActorSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testInsuranceAndMicroCreditsActorSearch.getLogo()).isEqualTo(UPDATED_LOGO);
                assertThat(testInsuranceAndMicroCreditsActorSearch.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
                assertThat(testInsuranceAndMicroCreditsActorSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testInsuranceAndMicroCreditsActorSearch.getAcronym()).isEqualTo(UPDATED_ACRONYM);
                assertThat(testInsuranceAndMicroCreditsActorSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingInsuranceAndMicroCreditsActor() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        insuranceAndMicroCreditsActor.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsActor
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, insuranceAndMicroCreditsActorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInsuranceAndMicroCreditsActor() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        insuranceAndMicroCreditsActor.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsActor
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInsuranceAndMicroCreditsActor() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        insuranceAndMicroCreditsActor.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsActor
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInsuranceAndMicroCreditsActorWithPatch() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();

        // Update the insuranceAndMicroCreditsActor using partial update
        InsuranceAndMicroCreditsActor partialUpdatedInsuranceAndMicroCreditsActor = new InsuranceAndMicroCreditsActor();
        partialUpdatedInsuranceAndMicroCreditsActor.setId(insuranceAndMicroCreditsActor.getId());

        partialUpdatedInsuranceAndMicroCreditsActor
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .acronym(UPDATED_ACRONYM)
            .description(UPDATED_DESCRIPTION);

        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInsuranceAndMicroCreditsActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInsuranceAndMicroCreditsActor))
            )
            .andExpect(status().isOk());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        InsuranceAndMicroCreditsActor testInsuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorList.get(
            insuranceAndMicroCreditsActorList.size() - 1
        );
        assertThat(testInsuranceAndMicroCreditsActor.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testInsuranceAndMicroCreditsActor.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testInsuranceAndMicroCreditsActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInsuranceAndMicroCreditsActor.getAcronym()).isEqualTo(UPDATED_ACRONYM);
        assertThat(testInsuranceAndMicroCreditsActor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateInsuranceAndMicroCreditsActorWithPatch() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);

        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();

        // Update the insuranceAndMicroCreditsActor using partial update
        InsuranceAndMicroCreditsActor partialUpdatedInsuranceAndMicroCreditsActor = new InsuranceAndMicroCreditsActor();
        partialUpdatedInsuranceAndMicroCreditsActor.setId(insuranceAndMicroCreditsActor.getId());

        partialUpdatedInsuranceAndMicroCreditsActor
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .name(UPDATED_NAME)
            .acronym(UPDATED_ACRONYM)
            .description(UPDATED_DESCRIPTION);

        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInsuranceAndMicroCreditsActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInsuranceAndMicroCreditsActor))
            )
            .andExpect(status().isOk());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        InsuranceAndMicroCreditsActor testInsuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorList.get(
            insuranceAndMicroCreditsActorList.size() - 1
        );
        assertThat(testInsuranceAndMicroCreditsActor.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testInsuranceAndMicroCreditsActor.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testInsuranceAndMicroCreditsActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInsuranceAndMicroCreditsActor.getAcronym()).isEqualTo(UPDATED_ACRONYM);
        assertThat(testInsuranceAndMicroCreditsActor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingInsuranceAndMicroCreditsActor() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        insuranceAndMicroCreditsActor.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsActor
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, insuranceAndMicroCreditsActorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInsuranceAndMicroCreditsActor() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        insuranceAndMicroCreditsActor.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsActor
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInsuranceAndMicroCreditsActor() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        insuranceAndMicroCreditsActor.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsActor
        InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO = insuranceAndMicroCreditsActorMapper.toDto(
            insuranceAndMicroCreditsActor
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsActorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InsuranceAndMicroCreditsActor in the database
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInsuranceAndMicroCreditsActor() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);
        insuranceAndMicroCreditsActorRepository.save(insuranceAndMicroCreditsActor);
        insuranceAndMicroCreditsActorSearchRepository.save(insuranceAndMicroCreditsActor);

        int databaseSizeBeforeDelete = insuranceAndMicroCreditsActorRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the insuranceAndMicroCreditsActor
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(delete(ENTITY_API_URL_ID, insuranceAndMicroCreditsActor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InsuranceAndMicroCreditsActor> insuranceAndMicroCreditsActorList = insuranceAndMicroCreditsActorRepository.findAll();
        assertThat(insuranceAndMicroCreditsActorList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsActorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInsuranceAndMicroCreditsActor() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorRepository.saveAndFlush(insuranceAndMicroCreditsActor);
        insuranceAndMicroCreditsActorSearchRepository.save(insuranceAndMicroCreditsActor);

        // Search the insuranceAndMicroCreditsActor
        restInsuranceAndMicroCreditsActorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + insuranceAndMicroCreditsActor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insuranceAndMicroCreditsActor.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].acronym").value(hasItem(DEFAULT_ACRONYM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
