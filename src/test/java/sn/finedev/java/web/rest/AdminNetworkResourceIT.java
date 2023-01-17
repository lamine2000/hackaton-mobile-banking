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
import sn.finedev.java.domain.AdminNetwork;
import sn.finedev.java.domain.User;
import sn.finedev.java.domain.enumeration.AccountStatus;
import sn.finedev.java.repository.AdminNetworkRepository;
import sn.finedev.java.repository.search.AdminNetworkSearchRepository;
import sn.finedev.java.service.AdminNetworkService;
import sn.finedev.java.service.criteria.AdminNetworkCriteria;
import sn.finedev.java.service.dto.AdminNetworkDTO;
import sn.finedev.java.service.mapper.AdminNetworkMapper;

/**
 * Integration tests for the {@link AdminNetworkResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AdminNetworkResourceIT {

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

    private static final String ENTITY_API_URL = "/api/admin-networks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/admin-networks";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AdminNetworkRepository adminNetworkRepository;

    @Mock
    private AdminNetworkRepository adminNetworkRepositoryMock;

    @Autowired
    private AdminNetworkMapper adminNetworkMapper;

    @Mock
    private AdminNetworkService adminNetworkServiceMock;

    @Autowired
    private AdminNetworkSearchRepository adminNetworkSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdminNetworkMockMvc;

    private AdminNetwork adminNetwork;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AdminNetwork createEntity(EntityManager em) {
        AdminNetwork adminNetwork = new AdminNetwork()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .status(DEFAULT_STATUS)
            .commissionRate(DEFAULT_COMMISSION_RATE);
        return adminNetwork;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AdminNetwork createUpdatedEntity(EntityManager em) {
        AdminNetwork adminNetwork = new AdminNetwork()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);
        return adminNetwork;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        adminNetworkSearchRepository.deleteAll();
        assertThat(adminNetworkSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        adminNetwork = createEntity(em);
    }

    @Test
    @Transactional
    void createAdminNetwork() throws Exception {
        int databaseSizeBeforeCreate = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // Create the AdminNetwork
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);
        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        AdminNetwork testAdminNetwork = adminNetworkList.get(adminNetworkList.size() - 1);
        assertThat(testAdminNetwork.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testAdminNetwork.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testAdminNetwork.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAdminNetwork.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testAdminNetwork.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testAdminNetwork.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testAdminNetwork.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testAdminNetwork.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAdminNetwork.getCommissionRate()).isEqualTo(DEFAULT_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void createAdminNetworkWithExistingId() throws Exception {
        // Create the AdminNetwork with an existing ID
        adminNetwork.setId(1L);
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        int databaseSizeBeforeCreate = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // set the field null
        adminNetwork.setFirstName(null);

        // Create the AdminNetwork, which fails.
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // set the field null
        adminNetwork.setLastName(null);

        // Create the AdminNetwork, which fails.
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // set the field null
        adminNetwork.setEmail(null);

        // Create the AdminNetwork, which fails.
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // set the field null
        adminNetwork.setPhone(null);

        // Create the AdminNetwork, which fails.
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // set the field null
        adminNetwork.setAddressLine1(null);

        // Create the AdminNetwork, which fails.
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // set the field null
        adminNetwork.setCity(null);

        // Create the AdminNetwork, which fails.
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // set the field null
        adminNetwork.setStatus(null);

        // Create the AdminNetwork, which fails.
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCommissionRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        // set the field null
        adminNetwork.setCommissionRate(null);

        // Create the AdminNetwork, which fails.
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        restAdminNetworkMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAdminNetworks() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList
        restAdminNetworkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(adminNetwork.getId().intValue())))
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
    void getAllAdminNetworksWithEagerRelationshipsIsEnabled() throws Exception {
        when(adminNetworkServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAdminNetworkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(adminNetworkServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAdminNetworksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(adminNetworkServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAdminNetworkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(adminNetworkRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAdminNetwork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get the adminNetwork
        restAdminNetworkMockMvc
            .perform(get(ENTITY_API_URL_ID, adminNetwork.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(adminNetwork.getId().intValue()))
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
    void getAdminNetworksByIdFiltering() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        Long id = adminNetwork.getId();

        defaultAdminNetworkShouldBeFound("id.equals=" + id);
        defaultAdminNetworkShouldNotBeFound("id.notEquals=" + id);

        defaultAdminNetworkShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAdminNetworkShouldNotBeFound("id.greaterThan=" + id);

        defaultAdminNetworkShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAdminNetworkShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where firstName equals to DEFAULT_FIRST_NAME
        defaultAdminNetworkShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the adminNetworkList where firstName equals to UPDATED_FIRST_NAME
        defaultAdminNetworkShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultAdminNetworkShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the adminNetworkList where firstName equals to UPDATED_FIRST_NAME
        defaultAdminNetworkShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where firstName is not null
        defaultAdminNetworkShouldBeFound("firstName.specified=true");

        // Get all the adminNetworkList where firstName is null
        defaultAdminNetworkShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where firstName contains DEFAULT_FIRST_NAME
        defaultAdminNetworkShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the adminNetworkList where firstName contains UPDATED_FIRST_NAME
        defaultAdminNetworkShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where firstName does not contain DEFAULT_FIRST_NAME
        defaultAdminNetworkShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the adminNetworkList where firstName does not contain UPDATED_FIRST_NAME
        defaultAdminNetworkShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where lastName equals to DEFAULT_LAST_NAME
        defaultAdminNetworkShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the adminNetworkList where lastName equals to UPDATED_LAST_NAME
        defaultAdminNetworkShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultAdminNetworkShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the adminNetworkList where lastName equals to UPDATED_LAST_NAME
        defaultAdminNetworkShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where lastName is not null
        defaultAdminNetworkShouldBeFound("lastName.specified=true");

        // Get all the adminNetworkList where lastName is null
        defaultAdminNetworkShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByLastNameContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where lastName contains DEFAULT_LAST_NAME
        defaultAdminNetworkShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the adminNetworkList where lastName contains UPDATED_LAST_NAME
        defaultAdminNetworkShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where lastName does not contain DEFAULT_LAST_NAME
        defaultAdminNetworkShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the adminNetworkList where lastName does not contain UPDATED_LAST_NAME
        defaultAdminNetworkShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where email equals to DEFAULT_EMAIL
        defaultAdminNetworkShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the adminNetworkList where email equals to UPDATED_EMAIL
        defaultAdminNetworkShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultAdminNetworkShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the adminNetworkList where email equals to UPDATED_EMAIL
        defaultAdminNetworkShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where email is not null
        defaultAdminNetworkShouldBeFound("email.specified=true");

        // Get all the adminNetworkList where email is null
        defaultAdminNetworkShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByEmailContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where email contains DEFAULT_EMAIL
        defaultAdminNetworkShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the adminNetworkList where email contains UPDATED_EMAIL
        defaultAdminNetworkShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where email does not contain DEFAULT_EMAIL
        defaultAdminNetworkShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the adminNetworkList where email does not contain UPDATED_EMAIL
        defaultAdminNetworkShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where phone equals to DEFAULT_PHONE
        defaultAdminNetworkShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the adminNetworkList where phone equals to UPDATED_PHONE
        defaultAdminNetworkShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultAdminNetworkShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the adminNetworkList where phone equals to UPDATED_PHONE
        defaultAdminNetworkShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where phone is not null
        defaultAdminNetworkShouldBeFound("phone.specified=true");

        // Get all the adminNetworkList where phone is null
        defaultAdminNetworkShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByPhoneContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where phone contains DEFAULT_PHONE
        defaultAdminNetworkShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the adminNetworkList where phone contains UPDATED_PHONE
        defaultAdminNetworkShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where phone does not contain DEFAULT_PHONE
        defaultAdminNetworkShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the adminNetworkList where phone does not contain UPDATED_PHONE
        defaultAdminNetworkShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine1IsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine1 equals to DEFAULT_ADDRESS_LINE_1
        defaultAdminNetworkShouldBeFound("addressLine1.equals=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the adminNetworkList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultAdminNetworkShouldNotBeFound("addressLine1.equals=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine1IsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine1 in DEFAULT_ADDRESS_LINE_1 or UPDATED_ADDRESS_LINE_1
        defaultAdminNetworkShouldBeFound("addressLine1.in=" + DEFAULT_ADDRESS_LINE_1 + "," + UPDATED_ADDRESS_LINE_1);

        // Get all the adminNetworkList where addressLine1 equals to UPDATED_ADDRESS_LINE_1
        defaultAdminNetworkShouldNotBeFound("addressLine1.in=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine1IsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine1 is not null
        defaultAdminNetworkShouldBeFound("addressLine1.specified=true");

        // Get all the adminNetworkList where addressLine1 is null
        defaultAdminNetworkShouldNotBeFound("addressLine1.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine1ContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine1 contains DEFAULT_ADDRESS_LINE_1
        defaultAdminNetworkShouldBeFound("addressLine1.contains=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the adminNetworkList where addressLine1 contains UPDATED_ADDRESS_LINE_1
        defaultAdminNetworkShouldNotBeFound("addressLine1.contains=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine1NotContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine1 does not contain DEFAULT_ADDRESS_LINE_1
        defaultAdminNetworkShouldNotBeFound("addressLine1.doesNotContain=" + DEFAULT_ADDRESS_LINE_1);

        // Get all the adminNetworkList where addressLine1 does not contain UPDATED_ADDRESS_LINE_1
        defaultAdminNetworkShouldBeFound("addressLine1.doesNotContain=" + UPDATED_ADDRESS_LINE_1);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine2IsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine2 equals to DEFAULT_ADDRESS_LINE_2
        defaultAdminNetworkShouldBeFound("addressLine2.equals=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the adminNetworkList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultAdminNetworkShouldNotBeFound("addressLine2.equals=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine2IsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine2 in DEFAULT_ADDRESS_LINE_2 or UPDATED_ADDRESS_LINE_2
        defaultAdminNetworkShouldBeFound("addressLine2.in=" + DEFAULT_ADDRESS_LINE_2 + "," + UPDATED_ADDRESS_LINE_2);

        // Get all the adminNetworkList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultAdminNetworkShouldNotBeFound("addressLine2.in=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine2IsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine2 is not null
        defaultAdminNetworkShouldBeFound("addressLine2.specified=true");

        // Get all the adminNetworkList where addressLine2 is null
        defaultAdminNetworkShouldNotBeFound("addressLine2.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine2ContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine2 contains DEFAULT_ADDRESS_LINE_2
        defaultAdminNetworkShouldBeFound("addressLine2.contains=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the adminNetworkList where addressLine2 contains UPDATED_ADDRESS_LINE_2
        defaultAdminNetworkShouldNotBeFound("addressLine2.contains=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByAddressLine2NotContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where addressLine2 does not contain DEFAULT_ADDRESS_LINE_2
        defaultAdminNetworkShouldNotBeFound("addressLine2.doesNotContain=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the adminNetworkList where addressLine2 does not contain UPDATED_ADDRESS_LINE_2
        defaultAdminNetworkShouldBeFound("addressLine2.doesNotContain=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where city equals to DEFAULT_CITY
        defaultAdminNetworkShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the adminNetworkList where city equals to UPDATED_CITY
        defaultAdminNetworkShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCityIsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where city in DEFAULT_CITY or UPDATED_CITY
        defaultAdminNetworkShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the adminNetworkList where city equals to UPDATED_CITY
        defaultAdminNetworkShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where city is not null
        defaultAdminNetworkShouldBeFound("city.specified=true");

        // Get all the adminNetworkList where city is null
        defaultAdminNetworkShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCityContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where city contains DEFAULT_CITY
        defaultAdminNetworkShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the adminNetworkList where city contains UPDATED_CITY
        defaultAdminNetworkShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCityNotContainsSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where city does not contain DEFAULT_CITY
        defaultAdminNetworkShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the adminNetworkList where city does not contain UPDATED_CITY
        defaultAdminNetworkShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where status equals to DEFAULT_STATUS
        defaultAdminNetworkShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the adminNetworkList where status equals to UPDATED_STATUS
        defaultAdminNetworkShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultAdminNetworkShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the adminNetworkList where status equals to UPDATED_STATUS
        defaultAdminNetworkShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where status is not null
        defaultAdminNetworkShouldBeFound("status.specified=true");

        // Get all the adminNetworkList where status is null
        defaultAdminNetworkShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCommissionRateIsEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where commissionRate equals to DEFAULT_COMMISSION_RATE
        defaultAdminNetworkShouldBeFound("commissionRate.equals=" + DEFAULT_COMMISSION_RATE);

        // Get all the adminNetworkList where commissionRate equals to UPDATED_COMMISSION_RATE
        defaultAdminNetworkShouldNotBeFound("commissionRate.equals=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCommissionRateIsInShouldWork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where commissionRate in DEFAULT_COMMISSION_RATE or UPDATED_COMMISSION_RATE
        defaultAdminNetworkShouldBeFound("commissionRate.in=" + DEFAULT_COMMISSION_RATE + "," + UPDATED_COMMISSION_RATE);

        // Get all the adminNetworkList where commissionRate equals to UPDATED_COMMISSION_RATE
        defaultAdminNetworkShouldNotBeFound("commissionRate.in=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCommissionRateIsNullOrNotNull() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where commissionRate is not null
        defaultAdminNetworkShouldBeFound("commissionRate.specified=true");

        // Get all the adminNetworkList where commissionRate is null
        defaultAdminNetworkShouldNotBeFound("commissionRate.specified=false");
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCommissionRateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where commissionRate is greater than or equal to DEFAULT_COMMISSION_RATE
        defaultAdminNetworkShouldBeFound("commissionRate.greaterThanOrEqual=" + DEFAULT_COMMISSION_RATE);

        // Get all the adminNetworkList where commissionRate is greater than or equal to UPDATED_COMMISSION_RATE
        defaultAdminNetworkShouldNotBeFound("commissionRate.greaterThanOrEqual=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCommissionRateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where commissionRate is less than or equal to DEFAULT_COMMISSION_RATE
        defaultAdminNetworkShouldBeFound("commissionRate.lessThanOrEqual=" + DEFAULT_COMMISSION_RATE);

        // Get all the adminNetworkList where commissionRate is less than or equal to SMALLER_COMMISSION_RATE
        defaultAdminNetworkShouldNotBeFound("commissionRate.lessThanOrEqual=" + SMALLER_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCommissionRateIsLessThanSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where commissionRate is less than DEFAULT_COMMISSION_RATE
        defaultAdminNetworkShouldNotBeFound("commissionRate.lessThan=" + DEFAULT_COMMISSION_RATE);

        // Get all the adminNetworkList where commissionRate is less than UPDATED_COMMISSION_RATE
        defaultAdminNetworkShouldBeFound("commissionRate.lessThan=" + UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByCommissionRateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        // Get all the adminNetworkList where commissionRate is greater than DEFAULT_COMMISSION_RATE
        defaultAdminNetworkShouldNotBeFound("commissionRate.greaterThan=" + DEFAULT_COMMISSION_RATE);

        // Get all the adminNetworkList where commissionRate is greater than SMALLER_COMMISSION_RATE
        defaultAdminNetworkShouldBeFound("commissionRate.greaterThan=" + SMALLER_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void getAllAdminNetworksByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            adminNetworkRepository.saveAndFlush(adminNetwork);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        adminNetwork.setUser(user);
        adminNetworkRepository.saveAndFlush(adminNetwork);
        Long userId = user.getId();

        // Get all the adminNetworkList where user equals to userId
        defaultAdminNetworkShouldBeFound("userId.equals=" + userId);

        // Get all the adminNetworkList where user equals to (userId + 1)
        defaultAdminNetworkShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAdminNetworkShouldBeFound(String filter) throws Exception {
        restAdminNetworkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(adminNetwork.getId().intValue())))
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
        restAdminNetworkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAdminNetworkShouldNotBeFound(String filter) throws Exception {
        restAdminNetworkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAdminNetworkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAdminNetwork() throws Exception {
        // Get the adminNetwork
        restAdminNetworkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAdminNetwork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();
        adminNetworkSearchRepository.save(adminNetwork);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());

        // Update the adminNetwork
        AdminNetwork updatedAdminNetwork = adminNetworkRepository.findById(adminNetwork.getId()).get();
        // Disconnect from session so that the updates on updatedAdminNetwork are not directly saved in db
        em.detach(updatedAdminNetwork);
        updatedAdminNetwork
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(updatedAdminNetwork);

        restAdminNetworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, adminNetworkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isOk());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        AdminNetwork testAdminNetwork = adminNetworkList.get(adminNetworkList.size() - 1);
        assertThat(testAdminNetwork.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAdminNetwork.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAdminNetwork.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAdminNetwork.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testAdminNetwork.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testAdminNetwork.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testAdminNetwork.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAdminNetwork.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAdminNetwork.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AdminNetwork> adminNetworkSearchList = IterableUtils.toList(adminNetworkSearchRepository.findAll());
                AdminNetwork testAdminNetworkSearch = adminNetworkSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testAdminNetworkSearch.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
                assertThat(testAdminNetworkSearch.getLastName()).isEqualTo(UPDATED_LAST_NAME);
                assertThat(testAdminNetworkSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testAdminNetworkSearch.getPhone()).isEqualTo(UPDATED_PHONE);
                assertThat(testAdminNetworkSearch.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
                assertThat(testAdminNetworkSearch.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
                assertThat(testAdminNetworkSearch.getCity()).isEqualTo(UPDATED_CITY);
                assertThat(testAdminNetworkSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testAdminNetworkSearch.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingAdminNetwork() throws Exception {
        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        adminNetwork.setId(count.incrementAndGet());

        // Create the AdminNetwork
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdminNetworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, adminNetworkDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAdminNetwork() throws Exception {
        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        adminNetwork.setId(count.incrementAndGet());

        // Create the AdminNetwork
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdminNetworkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAdminNetwork() throws Exception {
        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        adminNetwork.setId(count.incrementAndGet());

        // Create the AdminNetwork
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdminNetworkMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAdminNetworkWithPatch() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();

        // Update the adminNetwork using partial update
        AdminNetwork partialUpdatedAdminNetwork = new AdminNetwork();
        partialUpdatedAdminNetwork.setId(adminNetwork.getId());

        partialUpdatedAdminNetwork
            .lastName(UPDATED_LAST_NAME)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);

        restAdminNetworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdminNetwork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdminNetwork))
            )
            .andExpect(status().isOk());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        AdminNetwork testAdminNetwork = adminNetworkList.get(adminNetworkList.size() - 1);
        assertThat(testAdminNetwork.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testAdminNetwork.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAdminNetwork.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAdminNetwork.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testAdminNetwork.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testAdminNetwork.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testAdminNetwork.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAdminNetwork.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAdminNetwork.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void fullUpdateAdminNetworkWithPatch() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);

        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();

        // Update the adminNetwork using partial update
        AdminNetwork partialUpdatedAdminNetwork = new AdminNetwork();
        partialUpdatedAdminNetwork.setId(adminNetwork.getId());

        partialUpdatedAdminNetwork
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .status(UPDATED_STATUS)
            .commissionRate(UPDATED_COMMISSION_RATE);

        restAdminNetworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdminNetwork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdminNetwork))
            )
            .andExpect(status().isOk());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        AdminNetwork testAdminNetwork = adminNetworkList.get(adminNetworkList.size() - 1);
        assertThat(testAdminNetwork.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAdminNetwork.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testAdminNetwork.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAdminNetwork.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testAdminNetwork.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testAdminNetwork.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testAdminNetwork.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAdminNetwork.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAdminNetwork.getCommissionRate()).isEqualTo(UPDATED_COMMISSION_RATE);
    }

    @Test
    @Transactional
    void patchNonExistingAdminNetwork() throws Exception {
        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        adminNetwork.setId(count.incrementAndGet());

        // Create the AdminNetwork
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdminNetworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, adminNetworkDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAdminNetwork() throws Exception {
        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        adminNetwork.setId(count.incrementAndGet());

        // Create the AdminNetwork
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdminNetworkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAdminNetwork() throws Exception {
        int databaseSizeBeforeUpdate = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        adminNetwork.setId(count.incrementAndGet());

        // Create the AdminNetwork
        AdminNetworkDTO adminNetworkDTO = adminNetworkMapper.toDto(adminNetwork);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdminNetworkMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(adminNetworkDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AdminNetwork in the database
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAdminNetwork() throws Exception {
        // Initialize the database
        adminNetworkRepository.saveAndFlush(adminNetwork);
        adminNetworkRepository.save(adminNetwork);
        adminNetworkSearchRepository.save(adminNetwork);

        int databaseSizeBeforeDelete = adminNetworkRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the adminNetwork
        restAdminNetworkMockMvc
            .perform(delete(ENTITY_API_URL_ID, adminNetwork.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AdminNetwork> adminNetworkList = adminNetworkRepository.findAll();
        assertThat(adminNetworkList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(adminNetworkSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAdminNetwork() throws Exception {
        // Initialize the database
        adminNetwork = adminNetworkRepository.saveAndFlush(adminNetwork);
        adminNetworkSearchRepository.save(adminNetwork);

        // Search the adminNetwork
        restAdminNetworkMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + adminNetwork.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(adminNetwork.getId().intValue())))
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
