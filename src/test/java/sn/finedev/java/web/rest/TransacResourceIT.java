package sn.finedev.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import sn.finedev.java.IntegrationTest;
import sn.finedev.java.domain.Transac;
import sn.finedev.java.domain.enumeration.CurrencyCode;
import sn.finedev.java.domain.enumeration.TransacType;
import sn.finedev.java.repository.TransacRepository;
import sn.finedev.java.repository.search.TransacSearchRepository;
import sn.finedev.java.service.criteria.TransacCriteria;
import sn.finedev.java.service.dto.TransacDTO;
import sn.finedev.java.service.mapper.TransacMapper;

/**
 * Integration tests for the {@link TransacResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransacResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RECEIVER = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER = "AAAAAAAAAA";
    private static final String UPDATED_SENDER = "BBBBBBBBBB";

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;
    private static final Double SMALLER_AMOUNT = 1D - 1D;

    private static final CurrencyCode DEFAULT_CURRENCY = CurrencyCode.XOF;
    private static final CurrencyCode UPDATED_CURRENCY = CurrencyCode.USD;

    private static final TransacType DEFAULT_TYPE = TransacType.DEPOSIT;
    private static final TransacType UPDATED_TYPE = TransacType.WITHDRAW;

    private static final String ENTITY_API_URL = "/api/transacs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transacs";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransacRepository transacRepository;

    @Autowired
    private TransacMapper transacMapper;

    @Autowired
    private TransacSearchRepository transacSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransacMockMvc;

    private Transac transac;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transac createEntity(EntityManager em) {
        Transac transac = new Transac()
            .code(DEFAULT_CODE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdAt(DEFAULT_CREATED_AT)
            .receiver(DEFAULT_RECEIVER)
            .sender(DEFAULT_SENDER)
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .type(DEFAULT_TYPE);
        return transac;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transac createUpdatedEntity(EntityManager em) {
        Transac transac = new Transac()
            .code(UPDATED_CODE)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .receiver(UPDATED_RECEIVER)
            .sender(UPDATED_SENDER)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .type(UPDATED_TYPE);
        return transac;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transacSearchRepository.deleteAll();
        assertThat(transacSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transac = createEntity(em);
    }

    @Test
    @Transactional
    void createTransac() throws Exception {
        int databaseSizeBeforeCreate = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        // Create the Transac
        TransacDTO transacDTO = transacMapper.toDto(transac);
        restTransacMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isCreated());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Transac testTransac = transacList.get(transacList.size() - 1);
        assertThat(testTransac.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTransac.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTransac.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testTransac.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testTransac.getSender()).isEqualTo(DEFAULT_SENDER);
        assertThat(testTransac.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testTransac.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testTransac.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createTransacWithExistingId() throws Exception {
        // Create the Transac with an existing ID
        transac.setId(1L);
        TransacDTO transacDTO = transacMapper.toDto(transac);

        int databaseSizeBeforeCreate = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransacMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        // set the field null
        transac.setCode(null);

        // Create the Transac, which fails.
        TransacDTO transacDTO = transacMapper.toDto(transac);

        restTransacMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isBadRequest());

        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        // set the field null
        transac.setCreatedBy(null);

        // Create the Transac, which fails.
        TransacDTO transacDTO = transacMapper.toDto(transac);

        restTransacMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isBadRequest());

        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        // set the field null
        transac.setCreatedAt(null);

        // Create the Transac, which fails.
        TransacDTO transacDTO = transacMapper.toDto(transac);

        restTransacMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isBadRequest());

        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        // set the field null
        transac.setAmount(null);

        // Create the Transac, which fails.
        TransacDTO transacDTO = transacMapper.toDto(transac);

        restTransacMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isBadRequest());

        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        // set the field null
        transac.setCurrency(null);

        // Create the Transac, which fails.
        TransacDTO transacDTO = transacMapper.toDto(transac);

        restTransacMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isBadRequest());

        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        // set the field null
        transac.setType(null);

        // Create the Transac, which fails.
        TransacDTO transacDTO = transacMapper.toDto(transac);

        restTransacMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isBadRequest());

        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransacs() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList
        restTransacMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transac.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)))
            .andExpect(jsonPath("$.[*].sender").value(hasItem(DEFAULT_SENDER)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getTransac() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get the transac
        restTransacMockMvc
            .perform(get(ENTITY_API_URL_ID, transac.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transac.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER))
            .andExpect(jsonPath("$.sender").value(DEFAULT_SENDER))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getTransacsByIdFiltering() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        Long id = transac.getId();

        defaultTransacShouldBeFound("id.equals=" + id);
        defaultTransacShouldNotBeFound("id.notEquals=" + id);

        defaultTransacShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransacShouldNotBeFound("id.greaterThan=" + id);

        defaultTransacShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransacShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransacsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where code equals to DEFAULT_CODE
        defaultTransacShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the transacList where code equals to UPDATED_CODE
        defaultTransacShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTransacsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where code in DEFAULT_CODE or UPDATED_CODE
        defaultTransacShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the transacList where code equals to UPDATED_CODE
        defaultTransacShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTransacsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where code is not null
        defaultTransacShouldBeFound("code.specified=true");

        // Get all the transacList where code is null
        defaultTransacShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllTransacsByCodeContainsSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where code contains DEFAULT_CODE
        defaultTransacShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the transacList where code contains UPDATED_CODE
        defaultTransacShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTransacsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where code does not contain DEFAULT_CODE
        defaultTransacShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the transacList where code does not contain UPDATED_CODE
        defaultTransacShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTransacsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where createdBy equals to DEFAULT_CREATED_BY
        defaultTransacShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the transacList where createdBy equals to UPDATED_CREATED_BY
        defaultTransacShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTransacsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTransacShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the transacList where createdBy equals to UPDATED_CREATED_BY
        defaultTransacShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTransacsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where createdBy is not null
        defaultTransacShouldBeFound("createdBy.specified=true");

        // Get all the transacList where createdBy is null
        defaultTransacShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTransacsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where createdBy contains DEFAULT_CREATED_BY
        defaultTransacShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the transacList where createdBy contains UPDATED_CREATED_BY
        defaultTransacShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTransacsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTransacShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the transacList where createdBy does not contain UPDATED_CREATED_BY
        defaultTransacShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTransacsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where createdAt equals to DEFAULT_CREATED_AT
        defaultTransacShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the transacList where createdAt equals to UPDATED_CREATED_AT
        defaultTransacShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransacsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultTransacShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the transacList where createdAt equals to UPDATED_CREATED_AT
        defaultTransacShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTransacsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where createdAt is not null
        defaultTransacShouldBeFound("createdAt.specified=true");

        // Get all the transacList where createdAt is null
        defaultTransacShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTransacsByReceiverIsEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where receiver equals to DEFAULT_RECEIVER
        defaultTransacShouldBeFound("receiver.equals=" + DEFAULT_RECEIVER);

        // Get all the transacList where receiver equals to UPDATED_RECEIVER
        defaultTransacShouldNotBeFound("receiver.equals=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllTransacsByReceiverIsInShouldWork() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where receiver in DEFAULT_RECEIVER or UPDATED_RECEIVER
        defaultTransacShouldBeFound("receiver.in=" + DEFAULT_RECEIVER + "," + UPDATED_RECEIVER);

        // Get all the transacList where receiver equals to UPDATED_RECEIVER
        defaultTransacShouldNotBeFound("receiver.in=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllTransacsByReceiverIsNullOrNotNull() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where receiver is not null
        defaultTransacShouldBeFound("receiver.specified=true");

        // Get all the transacList where receiver is null
        defaultTransacShouldNotBeFound("receiver.specified=false");
    }

    @Test
    @Transactional
    void getAllTransacsByReceiverContainsSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where receiver contains DEFAULT_RECEIVER
        defaultTransacShouldBeFound("receiver.contains=" + DEFAULT_RECEIVER);

        // Get all the transacList where receiver contains UPDATED_RECEIVER
        defaultTransacShouldNotBeFound("receiver.contains=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllTransacsByReceiverNotContainsSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where receiver does not contain DEFAULT_RECEIVER
        defaultTransacShouldNotBeFound("receiver.doesNotContain=" + DEFAULT_RECEIVER);

        // Get all the transacList where receiver does not contain UPDATED_RECEIVER
        defaultTransacShouldBeFound("receiver.doesNotContain=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllTransacsBySenderIsEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where sender equals to DEFAULT_SENDER
        defaultTransacShouldBeFound("sender.equals=" + DEFAULT_SENDER);

        // Get all the transacList where sender equals to UPDATED_SENDER
        defaultTransacShouldNotBeFound("sender.equals=" + UPDATED_SENDER);
    }

    @Test
    @Transactional
    void getAllTransacsBySenderIsInShouldWork() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where sender in DEFAULT_SENDER or UPDATED_SENDER
        defaultTransacShouldBeFound("sender.in=" + DEFAULT_SENDER + "," + UPDATED_SENDER);

        // Get all the transacList where sender equals to UPDATED_SENDER
        defaultTransacShouldNotBeFound("sender.in=" + UPDATED_SENDER);
    }

    @Test
    @Transactional
    void getAllTransacsBySenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where sender is not null
        defaultTransacShouldBeFound("sender.specified=true");

        // Get all the transacList where sender is null
        defaultTransacShouldNotBeFound("sender.specified=false");
    }

    @Test
    @Transactional
    void getAllTransacsBySenderContainsSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where sender contains DEFAULT_SENDER
        defaultTransacShouldBeFound("sender.contains=" + DEFAULT_SENDER);

        // Get all the transacList where sender contains UPDATED_SENDER
        defaultTransacShouldNotBeFound("sender.contains=" + UPDATED_SENDER);
    }

    @Test
    @Transactional
    void getAllTransacsBySenderNotContainsSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where sender does not contain DEFAULT_SENDER
        defaultTransacShouldNotBeFound("sender.doesNotContain=" + DEFAULT_SENDER);

        // Get all the transacList where sender does not contain UPDATED_SENDER
        defaultTransacShouldBeFound("sender.doesNotContain=" + UPDATED_SENDER);
    }

    @Test
    @Transactional
    void getAllTransacsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where amount equals to DEFAULT_AMOUNT
        defaultTransacShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the transacList where amount equals to UPDATED_AMOUNT
        defaultTransacShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransacsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultTransacShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the transacList where amount equals to UPDATED_AMOUNT
        defaultTransacShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransacsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where amount is not null
        defaultTransacShouldBeFound("amount.specified=true");

        // Get all the transacList where amount is null
        defaultTransacShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransacsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultTransacShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the transacList where amount is greater than or equal to UPDATED_AMOUNT
        defaultTransacShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransacsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where amount is less than or equal to DEFAULT_AMOUNT
        defaultTransacShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the transacList where amount is less than or equal to SMALLER_AMOUNT
        defaultTransacShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransacsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where amount is less than DEFAULT_AMOUNT
        defaultTransacShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the transacList where amount is less than UPDATED_AMOUNT
        defaultTransacShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransacsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where amount is greater than DEFAULT_AMOUNT
        defaultTransacShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the transacList where amount is greater than SMALLER_AMOUNT
        defaultTransacShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransacsByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where currency equals to DEFAULT_CURRENCY
        defaultTransacShouldBeFound("currency.equals=" + DEFAULT_CURRENCY);

        // Get all the transacList where currency equals to UPDATED_CURRENCY
        defaultTransacShouldNotBeFound("currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllTransacsByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where currency in DEFAULT_CURRENCY or UPDATED_CURRENCY
        defaultTransacShouldBeFound("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY);

        // Get all the transacList where currency equals to UPDATED_CURRENCY
        defaultTransacShouldNotBeFound("currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllTransacsByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where currency is not null
        defaultTransacShouldBeFound("currency.specified=true");

        // Get all the transacList where currency is null
        defaultTransacShouldNotBeFound("currency.specified=false");
    }

    @Test
    @Transactional
    void getAllTransacsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where type equals to DEFAULT_TYPE
        defaultTransacShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the transacList where type equals to UPDATED_TYPE
        defaultTransacShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTransacsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultTransacShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the transacList where type equals to UPDATED_TYPE
        defaultTransacShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTransacsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        // Get all the transacList where type is not null
        defaultTransacShouldBeFound("type.specified=true");

        // Get all the transacList where type is null
        defaultTransacShouldNotBeFound("type.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransacShouldBeFound(String filter) throws Exception {
        restTransacMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transac.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)))
            .andExpect(jsonPath("$.[*].sender").value(hasItem(DEFAULT_SENDER)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));

        // Check, that the count call also returns 1
        restTransacMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransacShouldNotBeFound(String filter) throws Exception {
        restTransacMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransacMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransac() throws Exception {
        // Get the transac
        restTransacMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransac() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        int databaseSizeBeforeUpdate = transacRepository.findAll().size();
        transacSearchRepository.save(transac);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());

        // Update the transac
        Transac updatedTransac = transacRepository.findById(transac.getId()).get();
        // Disconnect from session so that the updates on updatedTransac are not directly saved in db
        em.detach(updatedTransac);
        updatedTransac
            .code(UPDATED_CODE)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .receiver(UPDATED_RECEIVER)
            .sender(UPDATED_SENDER)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .type(UPDATED_TYPE);
        TransacDTO transacDTO = transacMapper.toDto(updatedTransac);

        restTransacMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transacDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transacDTO))
            )
            .andExpect(status().isOk());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        Transac testTransac = transacList.get(transacList.size() - 1);
        assertThat(testTransac.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTransac.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTransac.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTransac.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testTransac.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testTransac.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testTransac.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testTransac.getType()).isEqualTo(UPDATED_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Transac> transacSearchList = IterableUtils.toList(transacSearchRepository.findAll());
                Transac testTransacSearch = transacSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransacSearch.getCode()).isEqualTo(UPDATED_CODE);
                assertThat(testTransacSearch.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
                assertThat(testTransacSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testTransacSearch.getReceiver()).isEqualTo(UPDATED_RECEIVER);
                assertThat(testTransacSearch.getSender()).isEqualTo(UPDATED_SENDER);
                assertThat(testTransacSearch.getAmount()).isEqualTo(UPDATED_AMOUNT);
                assertThat(testTransacSearch.getCurrency()).isEqualTo(UPDATED_CURRENCY);
                assertThat(testTransacSearch.getType()).isEqualTo(UPDATED_TYPE);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransac() throws Exception {
        int databaseSizeBeforeUpdate = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        transac.setId(count.incrementAndGet());

        // Create the Transac
        TransacDTO transacDTO = transacMapper.toDto(transac);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransacMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transacDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transacDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransac() throws Exception {
        int databaseSizeBeforeUpdate = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        transac.setId(count.incrementAndGet());

        // Create the Transac
        TransacDTO transacDTO = transacMapper.toDto(transac);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransacMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transacDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransac() throws Exception {
        int databaseSizeBeforeUpdate = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        transac.setId(count.incrementAndGet());

        // Create the Transac
        TransacDTO transacDTO = transacMapper.toDto(transac);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransacMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transacDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransacWithPatch() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        int databaseSizeBeforeUpdate = transacRepository.findAll().size();

        // Update the transac using partial update
        Transac partialUpdatedTransac = new Transac();
        partialUpdatedTransac.setId(transac.getId());

        partialUpdatedTransac.code(UPDATED_CODE).amount(UPDATED_AMOUNT).type(UPDATED_TYPE);

        restTransacMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransac.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransac))
            )
            .andExpect(status().isOk());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        Transac testTransac = transacList.get(transacList.size() - 1);
        assertThat(testTransac.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTransac.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTransac.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testTransac.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testTransac.getSender()).isEqualTo(DEFAULT_SENDER);
        assertThat(testTransac.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testTransac.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testTransac.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateTransacWithPatch() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);

        int databaseSizeBeforeUpdate = transacRepository.findAll().size();

        // Update the transac using partial update
        Transac partialUpdatedTransac = new Transac();
        partialUpdatedTransac.setId(transac.getId());

        partialUpdatedTransac
            .code(UPDATED_CODE)
            .createdBy(UPDATED_CREATED_BY)
            .createdAt(UPDATED_CREATED_AT)
            .receiver(UPDATED_RECEIVER)
            .sender(UPDATED_SENDER)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .type(UPDATED_TYPE);

        restTransacMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransac.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransac))
            )
            .andExpect(status().isOk());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        Transac testTransac = transacList.get(transacList.size() - 1);
        assertThat(testTransac.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTransac.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTransac.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTransac.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testTransac.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testTransac.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testTransac.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testTransac.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingTransac() throws Exception {
        int databaseSizeBeforeUpdate = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        transac.setId(count.incrementAndGet());

        // Create the Transac
        TransacDTO transacDTO = transacMapper.toDto(transac);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransacMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transacDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transacDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransac() throws Exception {
        int databaseSizeBeforeUpdate = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        transac.setId(count.incrementAndGet());

        // Create the Transac
        TransacDTO transacDTO = transacMapper.toDto(transac);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransacMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transacDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransac() throws Exception {
        int databaseSizeBeforeUpdate = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        transac.setId(count.incrementAndGet());

        // Create the Transac
        TransacDTO transacDTO = transacMapper.toDto(transac);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransacMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transacDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transac in the database
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransac() throws Exception {
        // Initialize the database
        transacRepository.saveAndFlush(transac);
        transacRepository.save(transac);
        transacSearchRepository.save(transac);

        int databaseSizeBeforeDelete = transacRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transac
        restTransacMockMvc
            .perform(delete(ENTITY_API_URL_ID, transac.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transac> transacList = transacRepository.findAll();
        assertThat(transacList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transacSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransac() throws Exception {
        // Initialize the database
        transac = transacRepository.saveAndFlush(transac);
        transacSearchRepository.save(transac);

        // Search the transac
        restTransacMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transac.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transac.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)))
            .andExpect(jsonPath("$.[*].sender").value(hasItem(DEFAULT_SENDER)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
