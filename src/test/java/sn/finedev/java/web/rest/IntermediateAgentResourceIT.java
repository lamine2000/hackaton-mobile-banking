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
import sn.finedev.java.domain.IntermediateAgent;
import sn.finedev.java.domain.Store;
import sn.finedev.java.domain.User;
import sn.finedev.java.domain.enumeration.AccountStatus;
import sn.finedev.java.repository.IntermediateAgentRepository;
import sn.finedev.java.repository.search.IntermediateAgentSearchRepository;
import sn.finedev.java.service.IntermediateAgentService;
import sn.finedev.java.service.criteria.IntermediateAgentCriteria;
import sn.finedev.java.service.dto.IntermediateAgentDTO;
import sn.finedev.java.service.mapper.IntermediateAgentMapper;

/**
 * Integration tests for the {@link IntermediateAgentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IntermediateAgentResourceIT {

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

    private static final String ENTITY_API_URL = "/api/intermediate-agents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/intermediate-agents";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IntermediateAgentRepository intermediateAgentRepository;

    @Mock
    private IntermediateAgentRepository intermediateAgentRepositoryMock;

    @Autowired
    private IntermediateAgentMapper intermediateAgentMapper;

    @Mock
    private IntermediateAgentService intermediateAgentServiceMock;

    @Autowired
    private IntermediateAgentSearchRepository intermediateAgentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIntermediateAgentMockMvc;

    private IntermediateAgent intermediateAgent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntermediateAgent createEntity(EntityManager em) {
        IntermediateAgent intermediateAgent = new IntermediateAgent()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .status(DEFAULT_STATUS)
            .commissionRate(DEFAULT_COMMISSION_RATE);
        return intermediateAgent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntermediateAgent createUpdatedEntity(EntityManager em) {
        IntermediateAgent intermediateAgent = new IntermediateAgent()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);
        return intermediateAgent;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        intermediateAgentSearchRepository.deleteAll();
        assertThat(intermediateAgentSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        intermediateAgent = createEntity(em);
    }

    @Test
    @Transactional
    void createIntermediateAgent() throws Exception {
        int databaseSizeBeforeCreate = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // Create the IntermediateAgent
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);
        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isCreated());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        IntermediateAgent testIntermediateAgent = intermediateAgentList.get(intermediateAgentList.size() - 1);
        assertThat(testIntermediateAgent.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testIntermediateAgent.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testIntermediateAgent.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testIntermediateAgent.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testIntermediateAgent.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testIntermediateAgent.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testIntermediateAgent.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testIntermediateAgent.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testIntermediateAgent.getCommissionRate()).isEqualTo(DEFAULT_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void createIntermediateAgentWithExistingId() throws Exception {
        // Create the IntermediateAgent with an existing ID
        intermediateAgent.setId(1L);
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        int databaseSizeBeforeCreate = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // set the field null
        intermediateAgent.setFirstName(null);

        // Create the IntermediateAgent, which fails.
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // set the field null
        intermediateAgent.setLastName(null);

        // Create the IntermediateAgent, which fails.
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // set the field null
        intermediateAgent.setEmail(null);

        // Create the IntermediateAgent, which fails.
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // set the field null
        intermediateAgent.setPhone(null);

        // Create the IntermediateAgent, which fails.
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // set the field null
        intermediateAgent.setAddressLine1(null);

        // Create the IntermediateAgent, which fails.
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // set the field null
        intermediateAgent.setCity(null);

        // Create the IntermediateAgent, which fails.
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // set the field null
        intermediateAgent.setStatus(null);

        // Create the IntermediateAgent, which fails.
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCommissionRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        // set the field null
        intermediateAgent.setCommissionRate(null);

        // Create the IntermediateAgent, which fails.
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllIntermediateAgents() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList
        restIntermediateAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(intermediateAgent.getId().intValue())))
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
    void getAllIntermediateAgentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(intermediateAgentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIntermediateAgentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(intermediateAgentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIntermediateAgentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(intermediateAgentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIntermediateAgentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(intermediateAgentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getIntermediateAgent() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get the intermediateAgent
        restIntermediateAgentMockMvc
            .perform(get(ENTITY_API_URL_ID, intermediateAgent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(intermediateAgent.getId().intValue()))
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
    void getIntermediateAgentsByIdFiltering() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        Long id = intermediateAgent.getId();

        defaultIntermediateAgentShouldBeFound("id.equals=" + id);
        defaultIntermediateAgentShouldNotBeFound("id.notEquals=" + id);

        defaultIntermediateAgentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultIntermediateAgentShouldNotBeFound("id.greaterThan=" + id);

        defaultIntermediateAgentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultIntermediateAgentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where firstName equals to DEFAULT_FIRST_NAME
        defaultIntermediateAgentShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the intermediateAgentList where firstName equals to UPDATED_FIRST_NAME
        defaultIntermediateAgentShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultIntermediateAgentShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the intermediateAgentList where firstName equals to UPDATED_FIRST_NAME
        defaultIntermediateAgentShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where firstName is not null
        defaultIntermediateAgentShouldBeFound("firstName.specified=true");

        // Get all the intermediateAgentList where firstName is null
        defaultIntermediateAgentShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where firstName contains DEFAULT_FIRST_NAME
        defaultIntermediateAgentShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the intermediateAgentList where firstName contains UPDATED_FIRST_NAME
        defaultIntermediateAgentShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where firstName does not contain DEFAULT_FIRST_NAME
        defaultIntermediateAgentShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the intermediateAgentList where firstName does not contain UPDATED_FIRST_NAME
        defaultIntermediateAgentShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where lastName equals to DEFAULT_LAST_NAME
        defaultIntermediateAgentShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the intermediateAgentList where lastName equals to UPDATED_LAST_NAME
        defaultIntermediateAgentShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultIntermediateAgentShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the intermediateAgentList where lastName equals to UPDATED_LAST_NAME
        defaultIntermediateAgentShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where lastName is not null
        defaultIntermediateAgentShouldBeFound("lastName.specified=true");

        // Get all the intermediateAgentList where lastName is null
        defaultIntermediateAgentShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where lastName contains DEFAULT_LAST_NAME
        defaultIntermediateAgentShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the intermediateAgentList where lastName contains UPDATED_LAST_NAME
        defaultIntermediateAgentShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where lastName does not contain DEFAULT_LAST_NAME
        defaultIntermediateAgentShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the intermediateAgentList where lastName does not contain UPDATED_LAST_NAME
        defaultIntermediateAgentShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where email equals to DEFAULT_EMAIL
        defaultIntermediateAgentShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the intermediateAgentList where email equals to UPDATED_EMAIL
        defaultIntermediateAgentShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultIntermediateAgentShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the intermediateAgentList where email equals to UPDATED_EMAIL
        defaultIntermediateAgentShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where email is not null
        defaultIntermediateAgentShouldBeFound("email.specified=true");

        // Get all the intermediateAgentList where email is null
        defaultIntermediateAgentShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByEmailContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where email contains DEFAULT_EMAIL
        defaultIntermediateAgentShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the intermediateAgentList where email contains UPDATED_EMAIL
        defaultIntermediateAgentShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where email does not contain DEFAULT_EMAIL
        defaultIntermediateAgentShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the intermediateAgentList where email does not contain UPDATED_EMAIL
        defaultIntermediateAgentShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where phone equals to DEFAULT_PHONE
        defaultIntermediateAgentShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the intermediateAgentList where phone equals to UPDATED_PHONE
        defaultIntermediateAgentShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultIntermediateAgentShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the intermediateAgentList where phone equals to UPDATED_PHONE
        defaultIntermediateAgentShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where phone is not null
        defaultIntermediateAgentShouldBeFound("phone.specified=true");

        // Get all the intermediateAgentList where phone is null
        defaultIntermediateAgentShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where phone contains DEFAULT_PHONE
        defaultIntermediateAgentShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the intermediateAgentList where phone contains UPDATED_PHONE
        defaultIntermediateAgentShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where phone does not contain DEFAULT_PHONE
        defaultIntermediateAgentShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the intermediateAgentList where phone does not contain UPDATED_PHONE
        defaultIntermediateAgentShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine1IsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine1 equals to DEFAULT_ADDRESS_LINE_1
        defaultIntermediateAgentShouldBeFound("addressLine1.equals=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the intermediateAgentList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultIntermediateAgentShouldNotBeFound("addressLine1.equals=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine1IsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine1 in DEFAULT_ADDRESS_LINE_1 or UPDATED_ADDRESS_LINE_1
        defaultIntermediateAgentShouldBeFound("addressLine1.in=" + DEFAULT_ADDRESS_LINE_1 + "," + UPDATED_ADDRESS_LINE_1);

        // Get all the intermediateAgentList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultIntermediateAgentShouldNotBeFound("addressLine1.in=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine1IsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine1 is not null
        defaultIntermediateAgentShouldBeFound("addressLine1.specified=true");

        // Get all the intermediateAgentList where addressLine1 is null
        defaultIntermediateAgentShouldNotBeFound("addressLine1.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine1ContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine1 contains DEFAULT_ADDRESS_LINE_1
        defaultIntermediateAgentShouldBeFound("addressLine1.contains=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the intermediateAgentList where addressLine1 contains UPDATED_ADDRESS_LINE_1
        defaultIntermediateAgentShouldNotBeFound("addressLine1.contains=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine1NotContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine1 does not contain DEFAULT_ADDRESS_LINE_1
        defaultIntermediateAgentShouldNotBeFound("addressLine1.doesNotContain=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the intermediateAgentList where addressLine1 does not contain UPDATED_ADDRESS_LINE_1
        defaultIntermediateAgentShouldBeFound("addressLine1.doesNotContain=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine2IsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine2 equals to DEFAULT_ADDRESS_LINE_2
        defaultIntermediateAgentShouldBeFound("addressLine2.equals=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the intermediateAgentList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultIntermediateAgentShouldNotBeFound("addressLine2.equals=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine2IsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine2 in DEFAULT_ADDRESS_LINE_2 or UPDATED_ADDRESS_LINE_2
        defaultIntermediateAgentShouldBeFound("addressLine2.in=" + DEFAULT_ADDRESS_LINE_2 + "," + UPDATED_ADDRESS_LINE_2);

        // Get all the intermediateAgentList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultIntermediateAgentShouldNotBeFound("addressLine2.in=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine2IsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine2 is not null
        defaultIntermediateAgentShouldBeFound("addressLine2.specified=true");

        // Get all the intermediateAgentList where addressLine2 is null
        defaultIntermediateAgentShouldNotBeFound("addressLine2.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine2ContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine2 contains DEFAULT_ADDRESS_LINE_2
        defaultIntermediateAgentShouldBeFound("addressLine2.contains=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the intermediateAgentList where addressLine2 contains UPDATED_ADDRESS_LINE_2
        defaultIntermediateAgentShouldNotBeFound("addressLine2.contains=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByAddressLine2NotContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where addressLine2 does not contain DEFAULT_ADDRESS_LINE_2
        defaultIntermediateAgentShouldNotBeFound("addressLine2.doesNotContain=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the intermediateAgentList where addressLine2 does not contain UPDATED_ADDRESS_LINE_2
        defaultIntermediateAgentShouldBeFound("addressLine2.doesNotContain=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where city equals to DEFAULT_CITY
        defaultIntermediateAgentShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the intermediateAgentList where city equals to UPDATED_CITY
        defaultIntermediateAgentShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCityIsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where city in DEFAULT_CITY or UPDATED_CITY
        defaultIntermediateAgentShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the intermediateAgentList where city equals to UPDATED_CITY
        defaultIntermediateAgentShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where city is not null
        defaultIntermediateAgentShouldBeFound("city.specified=true");

        // Get all the intermediateAgentList where city is null
        defaultIntermediateAgentShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCityContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where city contains DEFAULT_CITY
        defaultIntermediateAgentShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the intermediateAgentList where city contains UPDATED_CITY
        defaultIntermediateAgentShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCityNotContainsSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where city does not contain DEFAULT_CITY
        defaultIntermediateAgentShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the intermediateAgentList where city does not contain UPDATED_CITY
        defaultIntermediateAgentShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where status equals to DEFAULT_STATUS
        defaultIntermediateAgentShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the intermediateAgentList where status equals to UPDATED_STATUS
        defaultIntermediateAgentShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultIntermediateAgentShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the intermediateAgentList where status equals to UPDATED_STATUS
        defaultIntermediateAgentShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where status is not null
        defaultIntermediateAgentShouldBeFound("status.specified=true");

        // Get all the intermediateAgentList where status is null
        defaultIntermediateAgentShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCommissionRateIsEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where commissionRate equals to DEFAULT_COMMISSION_RATE
        defaultIntermediateAgentShouldBeFound("commissionRate.equals=" + DEFAULT_COMMISSION_RATE);

        // Get all the intermediateAgentList where commissionRate equals to UPDATED_COMMISSION_RATE
        defaultIntermediateAgentShouldNotBeFound("commissionRate.equals=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCommissionRateIsInShouldWork() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where commissionRate in DEFAULT_COMMISSION_RATE or UPDATED_COMMISSION_RATE
        defaultIntermediateAgentShouldBeFound("commissionRate.in=" + DEFAULT_COMMISSION_RATE + "," + UPDATED_COMMISSION_RATE);

        // Get all the intermediateAgentList where commissionRate equals to UPDATED_COMMISSION_RATE
        defaultIntermediateAgentShouldNotBeFound("commissionRate.in=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCommissionRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where commissionRate is not null
        defaultIntermediateAgentShouldBeFound("commissionRate.specified=true");

        // Get all the intermediateAgentList where commissionRate is null
        defaultIntermediateAgentShouldNotBeFound("commissionRate.specified=false");
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCommissionRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where commissionRate is greater than or equal to DEFAULT_COMMISSION_RATE
        defaultIntermediateAgentShouldBeFound("commissionRate.greaterThanOrEqual=" + DEFAULT_COMMISSION_RATE);

        // Get all the intermediateAgentList where commissionRate is greater than or equal to UPDATED_COMMISSION_RATE
        defaultIntermediateAgentShouldNotBeFound("commissionRate.greaterThanOrEqual=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCommissionRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where commissionRate is less than or equal to DEFAULT_COMMISSION_RATE
        defaultIntermediateAgentShouldBeFound("commissionRate.lessThanOrEqual=" + DEFAULT_COMMISSION_RATE);

        // Get all the intermediateAgentList where commissionRate is less than or equal to SMALLER_COMMISSION_RATE
        defaultIntermediateAgentShouldNotBeFound("commissionRate.lessThanOrEqual=" + SMALLER_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCommissionRateIsLessThanSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where commissionRate is less than DEFAULT_COMMISSION_RATE
        defaultIntermediateAgentShouldNotBeFound("commissionRate.lessThan=" + DEFAULT_COMMISSION_RATE);

        // Get all the intermediateAgentList where commissionRate is less than UPDATED_COMMISSION_RATE
        defaultIntermediateAgentShouldBeFound("commissionRate.lessThan=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByCommissionRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        // Get all the intermediateAgentList where commissionRate is greater than DEFAULT_COMMISSION_RATE
        defaultIntermediateAgentShouldNotBeFound("commissionRate.greaterThan=" + DEFAULT_COMMISSION_RATE);

        // Get all the intermediateAgentList where commissionRate is greater than SMALLER_COMMISSION_RATE
        defaultIntermediateAgentShouldBeFound("commissionRate.greaterThan=" + SMALLER_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            intermediateAgentRepository.saveAndFlush(intermediateAgent);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        intermediateAgent.setUser(user);
        intermediateAgentRepository.saveAndFlush(intermediateAgent);
        Long userId = user.getId();

        // Get all the intermediateAgentList where user equals to userId
        defaultIntermediateAgentShouldBeFound("userId.equals=" + userId);

        // Get all the intermediateAgentList where user equals to (userId + 1)
        defaultIntermediateAgentShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllIntermediateAgentsByStoreIsEqualToSomething() throws Exception {
        Store store;
        if (TestUtil.findAll(em, Store.class).isEmpty()) {
            intermediateAgentRepository.saveAndFlush(intermediateAgent);
            store = StoreResourceIT.createEntity(em);
        } else {
            store = TestUtil.findAll(em, Store.class).get(0);
        }
        em.persist(store);
        em.flush();
        intermediateAgent.setStore(store);
        intermediateAgentRepository.saveAndFlush(intermediateAgent);
        Long storeId = store.getId();

        // Get all the intermediateAgentList where store equals to storeId
        defaultIntermediateAgentShouldBeFound("storeId.equals=" + storeId);

        // Get all the intermediateAgentList where store equals to (storeId + 1)
        defaultIntermediateAgentShouldNotBeFound("storeId.equals=" + (storeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultIntermediateAgentShouldBeFound(String filter) throws Exception {
        restIntermediateAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(intermediateAgent.getId().intValue())))
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
        restIntermediateAgentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultIntermediateAgentShouldNotBeFound(String filter) throws Exception {
        restIntermediateAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restIntermediateAgentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingIntermediateAgent() throws Exception {
        // Get the intermediateAgent
        restIntermediateAgentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIntermediateAgent() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();
        intermediateAgentSearchRepository.save(intermediateAgent);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());

        // Update the intermediateAgent
        IntermediateAgent updatedIntermediateAgent = intermediateAgentRepository.findById(intermediateAgent.getId()).get();
        // Disconnect from session so that the updates on updatedIntermediateAgent are not directly saved in db
        em.detach(updatedIntermediateAgent);
        updatedIntermediateAgent
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(updatedIntermediateAgent);

        restIntermediateAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intermediateAgentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isOk());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        IntermediateAgent testIntermediateAgent = intermediateAgentList.get(intermediateAgentList.size() - 1);
        assertThat(testIntermediateAgent.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testIntermediateAgent.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testIntermediateAgent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testIntermediateAgent.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testIntermediateAgent.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testIntermediateAgent.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testIntermediateAgent.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testIntermediateAgent.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testIntermediateAgent.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<IntermediateAgent> intermediateAgentSearchList = IterableUtils.toList(intermediateAgentSearchRepository.findAll());
                IntermediateAgent testIntermediateAgentSearch = intermediateAgentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testIntermediateAgentSearch.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
                assertThat(testIntermediateAgentSearch.getLastName()).isEqualTo(UPDATED_LAST_NAME);
                assertThat(testIntermediateAgentSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testIntermediateAgentSearch.getPhone()).isEqualTo(UPDATED_PHONE);
                assertThat(testIntermediateAgentSearch.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
                assertThat(testIntermediateAgentSearch.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
                assertThat(testIntermediateAgentSearch.getCity()).isEqualTo(UPDATED_CITY);
                assertThat(testIntermediateAgentSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testIntermediateAgentSearch.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingIntermediateAgent() throws Exception {
        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        intermediateAgent.setId(count.incrementAndGet());

        // Create the IntermediateAgent
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntermediateAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intermediateAgentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchIntermediateAgent() throws Exception {
        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        intermediateAgent.setId(count.incrementAndGet());

        // Create the IntermediateAgent
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntermediateAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIntermediateAgent() throws Exception {
        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        intermediateAgent.setId(count.incrementAndGet());

        // Create the IntermediateAgent
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntermediateAgentMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateIntermediateAgentWithPatch() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();

        // Update the intermediateAgent using partial update
        IntermediateAgent partialUpdatedIntermediateAgent = new IntermediateAgent();
        partialUpdatedIntermediateAgent.setId(intermediateAgent.getId());

        partialUpdatedIntermediateAgent.lastName(UPDATED_LAST_NAME).email(UPDATED_EMAIL).phone(UPDATED_PHONE);

        restIntermediateAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntermediateAgent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIntermediateAgent))
            )
            .andExpect(status().isOk());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        IntermediateAgent testIntermediateAgent = intermediateAgentList.get(intermediateAgentList.size() - 1);
        assertThat(testIntermediateAgent.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testIntermediateAgent.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testIntermediateAgent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testIntermediateAgent.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testIntermediateAgent.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testIntermediateAgent.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testIntermediateAgent.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testIntermediateAgent.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testIntermediateAgent.getCommissionRate()).isEqualTo(DEFAULT_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void fullUpdateIntermediateAgentWithPatch() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);

        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();

        // Update the intermediateAgent using partial update
        IntermediateAgent partialUpdatedIntermediateAgent = new IntermediateAgent();
        partialUpdatedIntermediateAgent.setId(intermediateAgent.getId());

        partialUpdatedIntermediateAgent
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);

        restIntermediateAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntermediateAgent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIntermediateAgent))
            )
            .andExpect(status().isOk());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        IntermediateAgent testIntermediateAgent = intermediateAgentList.get(intermediateAgentList.size() - 1);
        assertThat(testIntermediateAgent.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testIntermediateAgent.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testIntermediateAgent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testIntermediateAgent.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testIntermediateAgent.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testIntermediateAgent.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testIntermediateAgent.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testIntermediateAgent.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testIntermediateAgent.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void patchNonExistingIntermediateAgent() throws Exception {
        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        intermediateAgent.setId(count.incrementAndGet());

        // Create the IntermediateAgent
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntermediateAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, intermediateAgentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIntermediateAgent() throws Exception {
        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        intermediateAgent.setId(count.incrementAndGet());

        // Create the IntermediateAgent
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntermediateAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIntermediateAgent() throws Exception {
        int databaseSizeBeforeUpdate = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        intermediateAgent.setId(count.incrementAndGet());

        // Create the IntermediateAgent
        IntermediateAgentDTO intermediateAgentDTO = intermediateAgentMapper.toDto(intermediateAgent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntermediateAgentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(intermediateAgentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntermediateAgent in the database
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteIntermediateAgent() throws Exception {
        // Initialize the database
        intermediateAgentRepository.saveAndFlush(intermediateAgent);
        intermediateAgentRepository.save(intermediateAgent);
        intermediateAgentSearchRepository.save(intermediateAgent);

        int databaseSizeBeforeDelete = intermediateAgentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the intermediateAgent
        restIntermediateAgentMockMvc
            .perform(delete(ENTITY_API_URL_ID, intermediateAgent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<IntermediateAgent> intermediateAgentList = intermediateAgentRepository.findAll();
        assertThat(intermediateAgentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(intermediateAgentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchIntermediateAgent() throws Exception {
        // Initialize the database
        intermediateAgent = intermediateAgentRepository.saveAndFlush(intermediateAgent);
        intermediateAgentSearchRepository.save(intermediateAgent);

        // Search the intermediateAgent
        restIntermediateAgentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + intermediateAgent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(intermediateAgent.getId().intValue())))
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
