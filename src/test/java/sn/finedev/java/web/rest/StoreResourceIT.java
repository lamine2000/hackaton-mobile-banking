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
import sn.finedev.java.domain.Country;
import sn.finedev.java.domain.Department;
import sn.finedev.java.domain.Region;
import sn.finedev.java.domain.Store;
import sn.finedev.java.domain.Town;
import sn.finedev.java.domain.Zone;
import sn.finedev.java.domain.enumeration.CurrencyCode;
import sn.finedev.java.domain.enumeration.StoreStatus;
import sn.finedev.java.repository.StoreRepository;
import sn.finedev.java.repository.search.StoreSearchRepository;
import sn.finedev.java.service.StoreService;
import sn.finedev.java.service.criteria.StoreCriteria;
import sn.finedev.java.service.dto.StoreDTO;
import sn.finedev.java.service.mapper.StoreMapper;

/**
 * Integration tests for the {@link StoreResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StoreResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_LOCATION = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOCATION = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOCATION_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOCATION_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final CurrencyCode DEFAULT_CURRENCY = CurrencyCode.XOF;
    private static final CurrencyCode UPDATED_CURRENCY = CurrencyCode.USD;

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTIFICATION_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_NOTIFICATION_EMAIL = "BBBBBBBBBB";

    private static final StoreStatus DEFAULT_STATUS = StoreStatus.OPENED;
    private static final StoreStatus UPDATED_STATUS = StoreStatus.CLOSED;

    private static final String DEFAULT_ABOUT_US = "AAAAAAAAAA";
    private static final String UPDATED_ABOUT_US = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/stores";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StoreRepository storeRepository;

    @Mock
    private StoreRepository storeRepositoryMock;

    @Autowired
    private StoreMapper storeMapper;

    @Mock
    private StoreService storeServiceMock;

    @Autowired
    private StoreSearchRepository storeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStoreMockMvc;

    private Store store;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Store createEntity(EntityManager em) {
        Store store = new Store()
            .code(DEFAULT_CODE)
            .location(DEFAULT_LOCATION)
            .locationContentType(DEFAULT_LOCATION_CONTENT_TYPE)
            .address(DEFAULT_ADDRESS)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .currency(DEFAULT_CURRENCY)
            .phone(DEFAULT_PHONE)
            .notificationEmail(DEFAULT_NOTIFICATION_EMAIL)
            .status(DEFAULT_STATUS)
            .aboutUs(DEFAULT_ABOUT_US);
        return store;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Store createUpdatedEntity(EntityManager em) {
        Store store = new Store()
            .code(UPDATED_CODE)
            .location(UPDATED_LOCATION)
            .locationContentType(UPDATED_LOCATION_CONTENT_TYPE)
            .address(UPDATED_ADDRESS)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .currency(UPDATED_CURRENCY)
            .phone(UPDATED_PHONE)
            .notificationEmail(UPDATED_NOTIFICATION_EMAIL)
            .status(UPDATED_STATUS)
            .aboutUs(UPDATED_ABOUT_US);
        return store;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        storeSearchRepository.deleteAll();
        assertThat(storeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        store = createEntity(em);
    }

    @Test
    @Transactional
    void createStore() throws Exception {
        int databaseSizeBeforeCreate = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);
        restStoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(storeDTO)))
            .andExpect(status().isCreated());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testStore.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testStore.getLocationContentType()).isEqualTo(DEFAULT_LOCATION_CONTENT_TYPE);
        assertThat(testStore.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testStore.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStore.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStore.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testStore.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testStore.getNotificationEmail()).isEqualTo(DEFAULT_NOTIFICATION_EMAIL);
        assertThat(testStore.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testStore.getAboutUs()).isEqualTo(DEFAULT_ABOUT_US);
    }

    @Test
    @Transactional
    void createStoreWithExistingId() throws Exception {
        // Create the Store with an existing ID
        store.setId(1L);
        StoreDTO storeDTO = storeMapper.toDto(store);

        int databaseSizeBeforeCreate = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(storeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        // set the field null
        store.setCode(null);

        // Create the Store, which fails.
        StoreDTO storeDTO = storeMapper.toDto(store);

        restStoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(storeDTO)))
            .andExpect(status().isBadRequest());

        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        // set the field null
        store.setName(null);

        // Create the Store, which fails.
        StoreDTO storeDTO = storeMapper.toDto(store);

        restStoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(storeDTO)))
            .andExpect(status().isBadRequest());

        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        // set the field null
        store.setPhone(null);

        // Create the Store, which fails.
        StoreDTO storeDTO = storeMapper.toDto(store);

        restStoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(storeDTO)))
            .andExpect(status().isBadRequest());

        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNotificationEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        // set the field null
        store.setNotificationEmail(null);

        // Create the Store, which fails.
        StoreDTO storeDTO = storeMapper.toDto(store);

        restStoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(storeDTO)))
            .andExpect(status().isBadRequest());

        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllStores() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList
        restStoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(store.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].locationContentType").value(hasItem(DEFAULT_LOCATION_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOCATION))))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].notificationEmail").value(hasItem(DEFAULT_NOTIFICATION_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].aboutUs").value(hasItem(DEFAULT_ABOUT_US.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStoresWithEagerRelationshipsIsEnabled() throws Exception {
        when(storeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStoreMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(storeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStoresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(storeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStoreMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(storeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStore() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get the store
        restStoreMockMvc
            .perform(get(ENTITY_API_URL_ID, store.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(store.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.locationContentType").value(DEFAULT_LOCATION_CONTENT_TYPE))
            .andExpect(jsonPath("$.location").value(Base64Utils.encodeToString(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.notificationEmail").value(DEFAULT_NOTIFICATION_EMAIL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.aboutUs").value(DEFAULT_ABOUT_US.toString()));
    }

    @Test
    @Transactional
    void getStoresByIdFiltering() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        Long id = store.getId();

        defaultStoreShouldBeFound("id.equals=" + id);
        defaultStoreShouldNotBeFound("id.notEquals=" + id);

        defaultStoreShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStoreShouldNotBeFound("id.greaterThan=" + id);

        defaultStoreShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStoreShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStoresByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where code equals to DEFAULT_CODE
        defaultStoreShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the storeList where code equals to UPDATED_CODE
        defaultStoreShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStoresByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where code in DEFAULT_CODE or UPDATED_CODE
        defaultStoreShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the storeList where code equals to UPDATED_CODE
        defaultStoreShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStoresByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where code is not null
        defaultStoreShouldBeFound("code.specified=true");

        // Get all the storeList where code is null
        defaultStoreShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllStoresByCodeContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where code contains DEFAULT_CODE
        defaultStoreShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the storeList where code contains UPDATED_CODE
        defaultStoreShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStoresByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where code does not contain DEFAULT_CODE
        defaultStoreShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the storeList where code does not contain UPDATED_CODE
        defaultStoreShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStoresByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where address equals to DEFAULT_ADDRESS
        defaultStoreShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the storeList where address equals to UPDATED_ADDRESS
        defaultStoreShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllStoresByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultStoreShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the storeList where address equals to UPDATED_ADDRESS
        defaultStoreShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllStoresByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where address is not null
        defaultStoreShouldBeFound("address.specified=true");

        // Get all the storeList where address is null
        defaultStoreShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    void getAllStoresByAddressContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where address contains DEFAULT_ADDRESS
        defaultStoreShouldBeFound("address.contains=" + DEFAULT_ADDRESS);

        // Get all the storeList where address contains UPDATED_ADDRESS
        defaultStoreShouldNotBeFound("address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllStoresByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where address does not contain DEFAULT_ADDRESS
        defaultStoreShouldNotBeFound("address.doesNotContain=" + DEFAULT_ADDRESS);

        // Get all the storeList where address does not contain UPDATED_ADDRESS
        defaultStoreShouldBeFound("address.doesNotContain=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllStoresByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where name equals to DEFAULT_NAME
        defaultStoreShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the storeList where name equals to UPDATED_NAME
        defaultStoreShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStoresByNameIsInShouldWork() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultStoreShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the storeList where name equals to UPDATED_NAME
        defaultStoreShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStoresByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where name is not null
        defaultStoreShouldBeFound("name.specified=true");

        // Get all the storeList where name is null
        defaultStoreShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllStoresByNameContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where name contains DEFAULT_NAME
        defaultStoreShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the storeList where name contains UPDATED_NAME
        defaultStoreShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStoresByNameNotContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where name does not contain DEFAULT_NAME
        defaultStoreShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the storeList where name does not contain UPDATED_NAME
        defaultStoreShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllStoresByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where description equals to DEFAULT_DESCRIPTION
        defaultStoreShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the storeList where description equals to UPDATED_DESCRIPTION
        defaultStoreShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStoresByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultStoreShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the storeList where description equals to UPDATED_DESCRIPTION
        defaultStoreShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStoresByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where description is not null
        defaultStoreShouldBeFound("description.specified=true");

        // Get all the storeList where description is null
        defaultStoreShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllStoresByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where description contains DEFAULT_DESCRIPTION
        defaultStoreShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the storeList where description contains UPDATED_DESCRIPTION
        defaultStoreShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStoresByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where description does not contain DEFAULT_DESCRIPTION
        defaultStoreShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the storeList where description does not contain UPDATED_DESCRIPTION
        defaultStoreShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllStoresByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where currency equals to DEFAULT_CURRENCY
        defaultStoreShouldBeFound("currency.equals=" + DEFAULT_CURRENCY);

        // Get all the storeList where currency equals to UPDATED_CURRENCY
        defaultStoreShouldNotBeFound("currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllStoresByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where currency in DEFAULT_CURRENCY or UPDATED_CURRENCY
        defaultStoreShouldBeFound("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY);

        // Get all the storeList where currency equals to UPDATED_CURRENCY
        defaultStoreShouldNotBeFound("currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllStoresByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where currency is not null
        defaultStoreShouldBeFound("currency.specified=true");

        // Get all the storeList where currency is null
        defaultStoreShouldNotBeFound("currency.specified=false");
    }

    @Test
    @Transactional
    void getAllStoresByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where phone equals to DEFAULT_PHONE
        defaultStoreShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the storeList where phone equals to UPDATED_PHONE
        defaultStoreShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStoresByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultStoreShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the storeList where phone equals to UPDATED_PHONE
        defaultStoreShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStoresByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where phone is not null
        defaultStoreShouldBeFound("phone.specified=true");

        // Get all the storeList where phone is null
        defaultStoreShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllStoresByPhoneContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where phone contains DEFAULT_PHONE
        defaultStoreShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the storeList where phone contains UPDATED_PHONE
        defaultStoreShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStoresByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where phone does not contain DEFAULT_PHONE
        defaultStoreShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the storeList where phone does not contain UPDATED_PHONE
        defaultStoreShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStoresByNotificationEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where notificationEmail equals to DEFAULT_NOTIFICATION_EMAIL
        defaultStoreShouldBeFound("notificationEmail.equals=" + DEFAULT_NOTIFICATION_EMAIL);

        // Get all the storeList where notificationEmail equals to UPDATED_NOTIFICATION_EMAIL
        defaultStoreShouldNotBeFound("notificationEmail.equals=" + UPDATED_NOTIFICATION_EMAIL);
    }

    @Test
    @Transactional
    void getAllStoresByNotificationEmailIsInShouldWork() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where notificationEmail in DEFAULT_NOTIFICATION_EMAIL or UPDATED_NOTIFICATION_EMAIL
        defaultStoreShouldBeFound("notificationEmail.in=" + DEFAULT_NOTIFICATION_EMAIL + "," + UPDATED_NOTIFICATION_EMAIL);

        // Get all the storeList where notificationEmail equals to UPDATED_NOTIFICATION_EMAIL
        defaultStoreShouldNotBeFound("notificationEmail.in=" + UPDATED_NOTIFICATION_EMAIL);
    }

    @Test
    @Transactional
    void getAllStoresByNotificationEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where notificationEmail is not null
        defaultStoreShouldBeFound("notificationEmail.specified=true");

        // Get all the storeList where notificationEmail is null
        defaultStoreShouldNotBeFound("notificationEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllStoresByNotificationEmailContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where notificationEmail contains DEFAULT_NOTIFICATION_EMAIL
        defaultStoreShouldBeFound("notificationEmail.contains=" + DEFAULT_NOTIFICATION_EMAIL);

        // Get all the storeList where notificationEmail contains UPDATED_NOTIFICATION_EMAIL
        defaultStoreShouldNotBeFound("notificationEmail.contains=" + UPDATED_NOTIFICATION_EMAIL);
    }

    @Test
    @Transactional
    void getAllStoresByNotificationEmailNotContainsSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where notificationEmail does not contain DEFAULT_NOTIFICATION_EMAIL
        defaultStoreShouldNotBeFound("notificationEmail.doesNotContain=" + DEFAULT_NOTIFICATION_EMAIL);

        // Get all the storeList where notificationEmail does not contain UPDATED_NOTIFICATION_EMAIL
        defaultStoreShouldBeFound("notificationEmail.doesNotContain=" + UPDATED_NOTIFICATION_EMAIL);
    }

    @Test
    @Transactional
    void getAllStoresByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where status equals to DEFAULT_STATUS
        defaultStoreShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the storeList where status equals to UPDATED_STATUS
        defaultStoreShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStoresByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultStoreShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the storeList where status equals to UPDATED_STATUS
        defaultStoreShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStoresByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        // Get all the storeList where status is not null
        defaultStoreShouldBeFound("status.specified=true");

        // Get all the storeList where status is null
        defaultStoreShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllStoresByZoneIsEqualToSomething() throws Exception {
        Zone zone;
        if (TestUtil.findAll(em, Zone.class).isEmpty()) {
            storeRepository.saveAndFlush(store);
            zone = ZoneResourceIT.createEntity(em);
        } else {
            zone = TestUtil.findAll(em, Zone.class).get(0);
        }
        em.persist(zone);
        em.flush();
        store.setZone(zone);
        storeRepository.saveAndFlush(store);
        Long zoneId = zone.getId();

        // Get all the storeList where zone equals to zoneId
        defaultStoreShouldBeFound("zoneId.equals=" + zoneId);

        // Get all the storeList where zone equals to (zoneId + 1)
        defaultStoreShouldNotBeFound("zoneId.equals=" + (zoneId + 1));
    }

    @Test
    @Transactional
    void getAllStoresByTownIsEqualToSomething() throws Exception {
        Town town;
        if (TestUtil.findAll(em, Town.class).isEmpty()) {
            storeRepository.saveAndFlush(store);
            town = TownResourceIT.createEntity(em);
        } else {
            town = TestUtil.findAll(em, Town.class).get(0);
        }
        em.persist(town);
        em.flush();
        store.setTown(town);
        storeRepository.saveAndFlush(store);
        Long townId = town.getId();

        // Get all the storeList where town equals to townId
        defaultStoreShouldBeFound("townId.equals=" + townId);

        // Get all the storeList where town equals to (townId + 1)
        defaultStoreShouldNotBeFound("townId.equals=" + (townId + 1));
    }

    @Test
    @Transactional
    void getAllStoresByDepartmentIsEqualToSomething() throws Exception {
        Department department;
        if (TestUtil.findAll(em, Department.class).isEmpty()) {
            storeRepository.saveAndFlush(store);
            department = DepartmentResourceIT.createEntity(em);
        } else {
            department = TestUtil.findAll(em, Department.class).get(0);
        }
        em.persist(department);
        em.flush();
        store.setDepartment(department);
        storeRepository.saveAndFlush(store);
        Long departmentId = department.getId();

        // Get all the storeList where department equals to departmentId
        defaultStoreShouldBeFound("departmentId.equals=" + departmentId);

        // Get all the storeList where department equals to (departmentId + 1)
        defaultStoreShouldNotBeFound("departmentId.equals=" + (departmentId + 1));
    }

    @Test
    @Transactional
    void getAllStoresByRegionIsEqualToSomething() throws Exception {
        Region region;
        if (TestUtil.findAll(em, Region.class).isEmpty()) {
            storeRepository.saveAndFlush(store);
            region = RegionResourceIT.createEntity(em);
        } else {
            region = TestUtil.findAll(em, Region.class).get(0);
        }
        em.persist(region);
        em.flush();
        store.setRegion(region);
        storeRepository.saveAndFlush(store);
        Long regionId = region.getId();

        // Get all the storeList where region equals to regionId
        defaultStoreShouldBeFound("regionId.equals=" + regionId);

        // Get all the storeList where region equals to (regionId + 1)
        defaultStoreShouldNotBeFound("regionId.equals=" + (regionId + 1));
    }

    @Test
    @Transactional
    void getAllStoresByCountryIsEqualToSomething() throws Exception {
        Country country;
        if (TestUtil.findAll(em, Country.class).isEmpty()) {
            storeRepository.saveAndFlush(store);
            country = CountryResourceIT.createEntity(em);
        } else {
            country = TestUtil.findAll(em, Country.class).get(0);
        }
        em.persist(country);
        em.flush();
        store.setCountry(country);
        storeRepository.saveAndFlush(store);
        Long countryId = country.getId();

        // Get all the storeList where country equals to countryId
        defaultStoreShouldBeFound("countryId.equals=" + countryId);

        // Get all the storeList where country equals to (countryId + 1)
        defaultStoreShouldNotBeFound("countryId.equals=" + (countryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStoreShouldBeFound(String filter) throws Exception {
        restStoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(store.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].locationContentType").value(hasItem(DEFAULT_LOCATION_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOCATION))))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].notificationEmail").value(hasItem(DEFAULT_NOTIFICATION_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].aboutUs").value(hasItem(DEFAULT_ABOUT_US.toString())));

        // Check, that the count call also returns 1
        restStoreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStoreShouldNotBeFound(String filter) throws Exception {
        restStoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStoreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStore() throws Exception {
        // Get the store
        restStoreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStore() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        int databaseSizeBeforeUpdate = storeRepository.findAll().size();
        storeSearchRepository.save(store);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());

        // Update the store
        Store updatedStore = storeRepository.findById(store.getId()).get();
        // Disconnect from session so that the updates on updatedStore are not directly saved in db
        em.detach(updatedStore);
        updatedStore
            .code(UPDATED_CODE)
            .location(UPDATED_LOCATION)
            .locationContentType(UPDATED_LOCATION_CONTENT_TYPE)
            .address(UPDATED_ADDRESS)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .currency(UPDATED_CURRENCY)
            .phone(UPDATED_PHONE)
            .notificationEmail(UPDATED_NOTIFICATION_EMAIL)
            .status(UPDATED_STATUS)
            .aboutUs(UPDATED_ABOUT_US);
        StoreDTO storeDTO = storeMapper.toDto(updatedStore);

        restStoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testStore.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testStore.getLocationContentType()).isEqualTo(UPDATED_LOCATION_CONTENT_TYPE);
        assertThat(testStore.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testStore.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStore.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStore.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testStore.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testStore.getNotificationEmail()).isEqualTo(UPDATED_NOTIFICATION_EMAIL);
        assertThat(testStore.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStore.getAboutUs()).isEqualTo(UPDATED_ABOUT_US);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Store> storeSearchList = IterableUtils.toList(storeSearchRepository.findAll());
                Store testStoreSearch = storeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testStoreSearch.getCode()).isEqualTo(UPDATED_CODE);
                assertThat(testStoreSearch.getLocation()).isEqualTo(UPDATED_LOCATION);
                assertThat(testStoreSearch.getLocationContentType()).isEqualTo(UPDATED_LOCATION_CONTENT_TYPE);
                assertThat(testStoreSearch.getAddress()).isEqualTo(UPDATED_ADDRESS);
                assertThat(testStoreSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testStoreSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testStoreSearch.getCurrency()).isEqualTo(UPDATED_CURRENCY);
                assertThat(testStoreSearch.getPhone()).isEqualTo(UPDATED_PHONE);
                assertThat(testStoreSearch.getNotificationEmail()).isEqualTo(UPDATED_NOTIFICATION_EMAIL);
                assertThat(testStoreSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testStoreSearch.getAboutUs()).isEqualTo(UPDATED_ABOUT_US);
            });
    }

    @Test
    @Transactional
    void putNonExistingStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, storeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(storeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(storeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateStoreWithPatch() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        int databaseSizeBeforeUpdate = storeRepository.findAll().size();

        // Update the store using partial update
        Store partialUpdatedStore = new Store();
        partialUpdatedStore.setId(store.getId());

        partialUpdatedStore
            .location(UPDATED_LOCATION)
            .locationContentType(UPDATED_LOCATION_CONTENT_TYPE)
            .currency(UPDATED_CURRENCY)
            .phone(UPDATED_PHONE)
            .notificationEmail(UPDATED_NOTIFICATION_EMAIL);

        restStoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStore))
            )
            .andExpect(status().isOk());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testStore.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testStore.getLocationContentType()).isEqualTo(UPDATED_LOCATION_CONTENT_TYPE);
        assertThat(testStore.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testStore.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStore.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStore.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testStore.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testStore.getNotificationEmail()).isEqualTo(UPDATED_NOTIFICATION_EMAIL);
        assertThat(testStore.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testStore.getAboutUs()).isEqualTo(DEFAULT_ABOUT_US);
    }

    @Test
    @Transactional
    void fullUpdateStoreWithPatch() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);

        int databaseSizeBeforeUpdate = storeRepository.findAll().size();

        // Update the store using partial update
        Store partialUpdatedStore = new Store();
        partialUpdatedStore.setId(store.getId());

        partialUpdatedStore
            .code(UPDATED_CODE)
            .location(UPDATED_LOCATION)
            .locationContentType(UPDATED_LOCATION_CONTENT_TYPE)
            .address(UPDATED_ADDRESS)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .currency(UPDATED_CURRENCY)
            .phone(UPDATED_PHONE)
            .notificationEmail(UPDATED_NOTIFICATION_EMAIL)
            .status(UPDATED_STATUS)
            .aboutUs(UPDATED_ABOUT_US);

        restStoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStore))
            )
            .andExpect(status().isOk());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testStore.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testStore.getLocationContentType()).isEqualTo(UPDATED_LOCATION_CONTENT_TYPE);
        assertThat(testStore.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testStore.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStore.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStore.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testStore.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testStore.getNotificationEmail()).isEqualTo(UPDATED_NOTIFICATION_EMAIL);
        assertThat(testStore.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testStore.getAboutUs()).isEqualTo(UPDATED_ABOUT_US);
    }

    @Test
    @Transactional
    void patchNonExistingStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, storeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(storeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(storeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStoreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(storeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteStore() throws Exception {
        // Initialize the database
        storeRepository.saveAndFlush(store);
        storeRepository.save(store);
        storeSearchRepository.save(store);

        int databaseSizeBeforeDelete = storeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the store
        restStoreMockMvc
            .perform(delete(ENTITY_API_URL_ID, store.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Store> storeList = storeRepository.findAll();
        assertThat(storeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(storeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchStore() throws Exception {
        // Initialize the database
        store = storeRepository.saveAndFlush(store);
        storeSearchRepository.save(store);

        // Search the store
        restStoreMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + store.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(store.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].locationContentType").value(hasItem(DEFAULT_LOCATION_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOCATION))))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].notificationEmail").value(hasItem(DEFAULT_NOTIFICATION_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].aboutUs").value(hasItem(DEFAULT_ABOUT_US.toString())));
    }
}
