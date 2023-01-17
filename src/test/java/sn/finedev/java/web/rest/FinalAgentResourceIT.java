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
import sn.finedev.java.IntegrationTest;
import sn.finedev.java.domain.FinalAgent;
import sn.finedev.java.domain.Store;
import sn.finedev.java.domain.User;
import sn.finedev.java.domain.enumeration.AccountStatus;
import sn.finedev.java.repository.FinalAgentRepository;
import sn.finedev.java.repository.search.FinalAgentSearchRepository;
import sn.finedev.java.service.FinalAgentService;
import sn.finedev.java.service.criteria.FinalAgentCriteria;
import sn.finedev.java.service.dto.FinalAgentDTO;
import sn.finedev.java.service.mapper.FinalAgentMapper;

/**
 * Integration tests for the {@link FinalAgentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FinalAgentResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final AccountStatus DEFAULT_STATUS = AccountStatus.PENDING;
    private static final AccountStatus UPDATED_STATUS = AccountStatus.ACTIVE;

    private static final Double DEFAULT_COMMISSION_RATE = 1D;
    private static final Double UPDATED_COMMISSION_RATE = 2D;
    private static final Double SMALLER_COMMISSION_RATE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/final-agents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/final-agents";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FinalAgentRepository finalAgentRepository;

    @Mock
    private FinalAgentRepository finalAgentRepositoryMock;

    @Autowired
    private FinalAgentMapper finalAgentMapper;

    @Mock
    private FinalAgentService finalAgentServiceMock;

    @Autowired
    private FinalAgentSearchRepository finalAgentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFinalAgentMockMvc;

    private FinalAgent finalAgent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinalAgent createEntity(EntityManager em) {
        FinalAgent finalAgent = new FinalAgent()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .status(DEFAULT_STATUS)
            .commissionRate(DEFAULT_COMMISSION_RATE);
        return finalAgent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinalAgent createUpdatedEntity(EntityManager em) {
        FinalAgent finalAgent = new FinalAgent()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);
        return finalAgent;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        finalAgentSearchRepository.deleteAll();
        assertThat(finalAgentSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        finalAgent = createEntity(em);
    }

    @Test
    @Transactional
    void createFinalAgent() throws Exception {
        int databaseSizeBeforeCreate = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // Create the FinalAgent
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);
        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isCreated());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        FinalAgent testFinalAgent = finalAgentList.get(finalAgentList.size() - 1);
        assertThat(testFinalAgent.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testFinalAgent.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testFinalAgent.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testFinalAgent.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testFinalAgent.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testFinalAgent.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testFinalAgent.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testFinalAgent.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testFinalAgent.getCommissionRate()).isEqualTo(DEFAULT_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void createFinalAgentWithExistingId() throws Exception {
        // Create the FinalAgent with an existing ID
        finalAgent.setId(1L);
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        int databaseSizeBeforeCreate = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // set the field null
        finalAgent.setFirstName(null);

        // Create the FinalAgent, which fails.
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // set the field null
        finalAgent.setLastName(null);

        // Create the FinalAgent, which fails.
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // set the field null
        finalAgent.setEmail(null);

        // Create the FinalAgent, which fails.
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // set the field null
        finalAgent.setPhone(null);

        // Create the FinalAgent, which fails.
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // set the field null
        finalAgent.setAddressLine1(null);

        // Create the FinalAgent, which fails.
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // set the field null
        finalAgent.setCity(null);

        // Create the FinalAgent, which fails.
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // set the field null
        finalAgent.setStatus(null);

        // Create the FinalAgent, which fails.
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCommissionRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        // set the field null
        finalAgent.setCommissionRate(null);

        // Create the FinalAgent, which fails.
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        restFinalAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isBadRequest());

        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllFinalAgents() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList
        restFinalAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(finalAgent.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].commissionRate").value(hasItem(DEFAULT_COMMISSION_RATE.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFinalAgentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(finalAgentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFinalAgentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(finalAgentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFinalAgentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(finalAgentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFinalAgentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(finalAgentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFinalAgent() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get the finalAgent
        restFinalAgentMockMvc
            .perform(get(ENTITY_API_URL_ID, finalAgent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(finalAgent.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.addressLine1").value(DEFAULT_ADDRESS_LINE_1))
            .andExpect(jsonPath("$.addressLine2").value(DEFAULT_ADDRESS_LINE_2))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.commissionRate").value(DEFAULT_COMMISSION_RATE.doubleValue()));
    }

    @Test
    @Transactional
    void getFinalAgentsByIdFiltering() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        Long id = finalAgent.getId();

        defaultFinalAgentShouldBeFound("id.equals=" + id);
        defaultFinalAgentShouldNotBeFound("id.notEquals=" + id);

        defaultFinalAgentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFinalAgentShouldNotBeFound("id.greaterThan=" + id);

        defaultFinalAgentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFinalAgentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where firstName equals to DEFAULT_FIRST_NAME
        defaultFinalAgentShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the finalAgentList where firstName equals to UPDATED_FIRST_NAME
        defaultFinalAgentShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultFinalAgentShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the finalAgentList where firstName equals to UPDATED_FIRST_NAME
        defaultFinalAgentShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where firstName is not null
        defaultFinalAgentShouldBeFound("firstName.specified=true");

        // Get all the finalAgentList where firstName is null
        defaultFinalAgentShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where firstName contains DEFAULT_FIRST_NAME
        defaultFinalAgentShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the finalAgentList where firstName contains UPDATED_FIRST_NAME
        defaultFinalAgentShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where firstName does not contain DEFAULT_FIRST_NAME
        defaultFinalAgentShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the finalAgentList where firstName does not contain UPDATED_FIRST_NAME
        defaultFinalAgentShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where lastName equals to DEFAULT_LAST_NAME
        defaultFinalAgentShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the finalAgentList where lastName equals to UPDATED_LAST_NAME
        defaultFinalAgentShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultFinalAgentShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the finalAgentList where lastName equals to UPDATED_LAST_NAME
        defaultFinalAgentShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where lastName is not null
        defaultFinalAgentShouldBeFound("lastName.specified=true");

        // Get all the finalAgentList where lastName is null
        defaultFinalAgentShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where lastName contains DEFAULT_LAST_NAME
        defaultFinalAgentShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the finalAgentList where lastName contains UPDATED_LAST_NAME
        defaultFinalAgentShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where lastName does not contain DEFAULT_LAST_NAME
        defaultFinalAgentShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the finalAgentList where lastName does not contain UPDATED_LAST_NAME
        defaultFinalAgentShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where email equals to DEFAULT_EMAIL
        defaultFinalAgentShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the finalAgentList where email equals to UPDATED_EMAIL
        defaultFinalAgentShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultFinalAgentShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the finalAgentList where email equals to UPDATED_EMAIL
        defaultFinalAgentShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where email is not null
        defaultFinalAgentShouldBeFound("email.specified=true");

        // Get all the finalAgentList where email is null
        defaultFinalAgentShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByEmailContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where email contains DEFAULT_EMAIL
        defaultFinalAgentShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the finalAgentList where email contains UPDATED_EMAIL
        defaultFinalAgentShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where email does not contain DEFAULT_EMAIL
        defaultFinalAgentShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the finalAgentList where email does not contain UPDATED_EMAIL
        defaultFinalAgentShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where phone equals to DEFAULT_PHONE
        defaultFinalAgentShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the finalAgentList where phone equals to UPDATED_PHONE
        defaultFinalAgentShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultFinalAgentShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the finalAgentList where phone equals to UPDATED_PHONE
        defaultFinalAgentShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where phone is not null
        defaultFinalAgentShouldBeFound("phone.specified=true");

        // Get all the finalAgentList where phone is null
        defaultFinalAgentShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where phone contains DEFAULT_PHONE
        defaultFinalAgentShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the finalAgentList where phone contains UPDATED_PHONE
        defaultFinalAgentShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where phone does not contain DEFAULT_PHONE
        defaultFinalAgentShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the finalAgentList where phone does not contain UPDATED_PHONE
        defaultFinalAgentShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine1IsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine1 equals to DEFAULT_ADDRESS_LINE_1
        defaultFinalAgentShouldBeFound("addressLine1.equals=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the finalAgentList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultFinalAgentShouldNotBeFound("addressLine1.equals=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine1IsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine1 in DEFAULT_ADDRESS_LINE_1 or UPDATED_ADDRESS_LINE_1
        defaultFinalAgentShouldBeFound("addressLine1.in=" + DEFAULT_ADDRESS_LINE_1 + "," + UPDATED_ADDRESS_LINE_1);

        // Get all the finalAgentList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultFinalAgentShouldNotBeFound("addressLine1.in=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine1IsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine1 is not null
        defaultFinalAgentShouldBeFound("addressLine1.specified=true");

        // Get all the finalAgentList where addressLine1 is null
        defaultFinalAgentShouldNotBeFound("addressLine1.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine1ContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine1 contains DEFAULT_ADDRESS_LINE_1
        defaultFinalAgentShouldBeFound("addressLine1.contains=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the finalAgentList where addressLine1 contains UPDATED_ADDRESS_LINE_1
        defaultFinalAgentShouldNotBeFound("addressLine1.contains=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine1NotContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine1 does not contain DEFAULT_ADDRESS_LINE_1
        defaultFinalAgentShouldNotBeFound("addressLine1.doesNotContain=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the finalAgentList where addressLine1 does not contain UPDATED_ADDRESS_LINE_1
        defaultFinalAgentShouldBeFound("addressLine1.doesNotContain=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine2IsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine2 equals to DEFAULT_ADDRESS_LINE_2
        defaultFinalAgentShouldBeFound("addressLine2.equals=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the finalAgentList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultFinalAgentShouldNotBeFound("addressLine2.equals=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine2IsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine2 in DEFAULT_ADDRESS_LINE_2 or UPDATED_ADDRESS_LINE_2
        defaultFinalAgentShouldBeFound("addressLine2.in=" + DEFAULT_ADDRESS_LINE_2 + "," + UPDATED_ADDRESS_LINE_2);

        // Get all the finalAgentList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultFinalAgentShouldNotBeFound("addressLine2.in=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine2IsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine2 is not null
        defaultFinalAgentShouldBeFound("addressLine2.specified=true");

        // Get all the finalAgentList where addressLine2 is null
        defaultFinalAgentShouldNotBeFound("addressLine2.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine2ContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine2 contains DEFAULT_ADDRESS_LINE_2
        defaultFinalAgentShouldBeFound("addressLine2.contains=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the finalAgentList where addressLine2 contains UPDATED_ADDRESS_LINE_2
        defaultFinalAgentShouldNotBeFound("addressLine2.contains=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByAddressLine2NotContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where addressLine2 does not contain DEFAULT_ADDRESS_LINE_2
        defaultFinalAgentShouldNotBeFound("addressLine2.doesNotContain=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the finalAgentList where addressLine2 does not contain UPDATED_ADDRESS_LINE_2
        defaultFinalAgentShouldBeFound("addressLine2.doesNotContain=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where city equals to DEFAULT_CITY
        defaultFinalAgentShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the finalAgentList where city equals to UPDATED_CITY
        defaultFinalAgentShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where city in DEFAULT_CITY or UPDATED_CITY
        defaultFinalAgentShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the finalAgentList where city equals to UPDATED_CITY
        defaultFinalAgentShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where city is not null
        defaultFinalAgentShouldBeFound("city.specified=true");

        // Get all the finalAgentList where city is null
        defaultFinalAgentShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCityContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where city contains DEFAULT_CITY
        defaultFinalAgentShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the finalAgentList where city contains UPDATED_CITY
        defaultFinalAgentShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where city does not contain DEFAULT_CITY
        defaultFinalAgentShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the finalAgentList where city does not contain UPDATED_CITY
        defaultFinalAgentShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where status equals to DEFAULT_STATUS
        defaultFinalAgentShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the finalAgentList where status equals to UPDATED_STATUS
        defaultFinalAgentShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultFinalAgentShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the finalAgentList where status equals to UPDATED_STATUS
        defaultFinalAgentShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where status is not null
        defaultFinalAgentShouldBeFound("status.specified=true");

        // Get all the finalAgentList where status is null
        defaultFinalAgentShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCommissionRateIsEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where commissionRate equals to DEFAULT_COMMISSION_RATE
        defaultFinalAgentShouldBeFound("commissionRate.equals=" + DEFAULT_COMMISSION_RATE);

        // Get all the finalAgentList where commissionRate equals to UPDATED_COMMISSION_RATE
        defaultFinalAgentShouldNotBeFound("commissionRate.equals=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCommissionRateIsInShouldWork() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where commissionRate in DEFAULT_COMMISSION_RATE or UPDATED_COMMISSION_RATE
        defaultFinalAgentShouldBeFound("commissionRate.in=" + DEFAULT_COMMISSION_RATE + "," + UPDATED_COMMISSION_RATE);

        // Get all the finalAgentList where commissionRate equals to UPDATED_COMMISSION_RATE
        defaultFinalAgentShouldNotBeFound("commissionRate.in=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCommissionRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where commissionRate is not null
        defaultFinalAgentShouldBeFound("commissionRate.specified=true");

        // Get all the finalAgentList where commissionRate is null
        defaultFinalAgentShouldNotBeFound("commissionRate.specified=false");
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCommissionRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where commissionRate is greater than or equal to DEFAULT_COMMISSION_RATE
        defaultFinalAgentShouldBeFound("commissionRate.greaterThanOrEqual=" + DEFAULT_COMMISSION_RATE);

        // Get all the finalAgentList where commissionRate is greater than or equal to UPDATED_COMMISSION_RATE
        defaultFinalAgentShouldNotBeFound("commissionRate.greaterThanOrEqual=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCommissionRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where commissionRate is less than or equal to DEFAULT_COMMISSION_RATE
        defaultFinalAgentShouldBeFound("commissionRate.lessThanOrEqual=" + DEFAULT_COMMISSION_RATE);

        // Get all the finalAgentList where commissionRate is less than or equal to SMALLER_COMMISSION_RATE
        defaultFinalAgentShouldNotBeFound("commissionRate.lessThanOrEqual=" + SMALLER_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCommissionRateIsLessThanSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where commissionRate is less than DEFAULT_COMMISSION_RATE
        defaultFinalAgentShouldNotBeFound("commissionRate.lessThan=" + DEFAULT_COMMISSION_RATE);

        // Get all the finalAgentList where commissionRate is less than UPDATED_COMMISSION_RATE
        defaultFinalAgentShouldBeFound("commissionRate.lessThan=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByCommissionRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        // Get all the finalAgentList where commissionRate is greater than DEFAULT_COMMISSION_RATE
        defaultFinalAgentShouldNotBeFound("commissionRate.greaterThan=" + DEFAULT_COMMISSION_RATE);

        // Get all the finalAgentList where commissionRate is greater than SMALLER_COMMISSION_RATE
        defaultFinalAgentShouldBeFound("commissionRate.greaterThan=" + SMALLER_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllFinalAgentsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            finalAgentRepository.saveAndFlush(finalAgent);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        finalAgent.setUser(user);
        finalAgentRepository.saveAndFlush(finalAgent);
        Long userId = user.getId();

        // Get all the finalAgentList where user equals to userId
        defaultFinalAgentShouldBeFound("userId.equals=" + userId);

        // Get all the finalAgentList where user equals to (userId + 1)
        defaultFinalAgentShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllFinalAgentsByStoreIsEqualToSomething() throws Exception {
        Store store;
        if (TestUtil.findAll(em, Store.class).isEmpty()) {
            finalAgentRepository.saveAndFlush(finalAgent);
            store = StoreResourceIT.createEntity(em);
        } else {
            store = TestUtil.findAll(em, Store.class).get(0);
        }
        em.persist(store);
        em.flush();
        finalAgent.setStore(store);
        finalAgentRepository.saveAndFlush(finalAgent);
        Long storeId = store.getId();

        // Get all the finalAgentList where store equals to storeId
        defaultFinalAgentShouldBeFound("storeId.equals=" + storeId);

        // Get all the finalAgentList where store equals to (storeId + 1)
        defaultFinalAgentShouldNotBeFound("storeId.equals=" + (storeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFinalAgentShouldBeFound(String filter) throws Exception {
        restFinalAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(finalAgent.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].commissionRate").value(hasItem(DEFAULT_COMMISSION_RATE.doubleValue())));

        // Check, that the count call also returns 1
        restFinalAgentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFinalAgentShouldNotBeFound(String filter) throws Exception {
        restFinalAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFinalAgentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFinalAgent() throws Exception {
        // Get the finalAgent
        restFinalAgentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFinalAgent() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();
        finalAgentSearchRepository.save(finalAgent);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());

        // Update the finalAgent
        FinalAgent updatedFinalAgent = finalAgentRepository.findById(finalAgent.getId()).get();
        // Disconnect from session so that the updates on updatedFinalAgent are not directly saved in db
        em.detach(updatedFinalAgent);
        updatedFinalAgent
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(updatedFinalAgent);

        restFinalAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, finalAgentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(finalAgentDTO))
            )
            .andExpect(status().isOk());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        FinalAgent testFinalAgent = finalAgentList.get(finalAgentList.size() - 1);
        assertThat(testFinalAgent.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testFinalAgent.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testFinalAgent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testFinalAgent.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testFinalAgent.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testFinalAgent.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testFinalAgent.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testFinalAgent.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFinalAgent.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<FinalAgent> finalAgentSearchList = IterableUtils.toList(finalAgentSearchRepository.findAll());
                FinalAgent testFinalAgentSearch = finalAgentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFinalAgentSearch.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
                assertThat(testFinalAgentSearch.getLastName()).isEqualTo(UPDATED_LAST_NAME);
                assertThat(testFinalAgentSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testFinalAgentSearch.getPhone()).isEqualTo(UPDATED_PHONE);
                assertThat(testFinalAgentSearch.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
                assertThat(testFinalAgentSearch.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
                assertThat(testFinalAgentSearch.getCity()).isEqualTo(UPDATED_CITY);
                assertThat(testFinalAgentSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testFinalAgentSearch.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingFinalAgent() throws Exception {
        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        finalAgent.setId(count.incrementAndGet());

        // Create the FinalAgent
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinalAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, finalAgentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(finalAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFinalAgent() throws Exception {
        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        finalAgent.setId(count.incrementAndGet());

        // Create the FinalAgent
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinalAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(finalAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFinalAgent() throws Exception {
        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        finalAgent.setId(count.incrementAndGet());

        // Create the FinalAgent
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinalAgentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(finalAgentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateFinalAgentWithPatch() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();

        // Update the finalAgent using partial update
        FinalAgent partialUpdatedFinalAgent = new FinalAgent();
        partialUpdatedFinalAgent.setId(finalAgent.getId());

        partialUpdatedFinalAgent
            .firstName(UPDATED_FIRST_NAME)
            .email(UPDATED_EMAIL)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS);

        restFinalAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinalAgent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinalAgent))
            )
            .andExpect(status().isOk());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        FinalAgent testFinalAgent = finalAgentList.get(finalAgentList.size() - 1);
        assertThat(testFinalAgent.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testFinalAgent.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testFinalAgent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testFinalAgent.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testFinalAgent.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testFinalAgent.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testFinalAgent.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testFinalAgent.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFinalAgent.getCommissionRate()).isEqualTo(DEFAULT_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void fullUpdateFinalAgentWithPatch() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);

        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();

        // Update the finalAgent using partial update
        FinalAgent partialUpdatedFinalAgent = new FinalAgent();
        partialUpdatedFinalAgent.setId(finalAgent.getId());

        partialUpdatedFinalAgent
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);

        restFinalAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinalAgent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinalAgent))
            )
            .andExpect(status().isOk());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        FinalAgent testFinalAgent = finalAgentList.get(finalAgentList.size() - 1);
        assertThat(testFinalAgent.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testFinalAgent.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testFinalAgent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testFinalAgent.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testFinalAgent.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testFinalAgent.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testFinalAgent.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testFinalAgent.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFinalAgent.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void patchNonExistingFinalAgent() throws Exception {
        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        finalAgent.setId(count.incrementAndGet());

        // Create the FinalAgent
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinalAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, finalAgentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(finalAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFinalAgent() throws Exception {
        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        finalAgent.setId(count.incrementAndGet());

        // Create the FinalAgent
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinalAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(finalAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFinalAgent() throws Exception {
        int databaseSizeBeforeUpdate = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        finalAgent.setId(count.incrementAndGet());

        // Create the FinalAgent
        FinalAgentDTO finalAgentDTO = finalAgentMapper.toDto(finalAgent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinalAgentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(finalAgentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinalAgent in the database
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteFinalAgent() throws Exception {
        // Initialize the database
        finalAgentRepository.saveAndFlush(finalAgent);
        finalAgentRepository.save(finalAgent);
        finalAgentSearchRepository.save(finalAgent);

        int databaseSizeBeforeDelete = finalAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the finalAgent
        restFinalAgentMockMvc
            .perform(delete(ENTITY_API_URL_ID, finalAgent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FinalAgent> finalAgentList = finalAgentRepository.findAll();
        assertThat(finalAgentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(finalAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchFinalAgent() throws Exception {
        // Initialize the database
        finalAgent = finalAgentRepository.saveAndFlush(finalAgent);
        finalAgentSearchRepository.save(finalAgent);

        // Search the finalAgent
        restFinalAgentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + finalAgent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(finalAgent.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].commissionRate").value(hasItem(DEFAULT_COMMISSION_RATE.doubleValue())));
    }
}
