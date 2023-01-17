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
import sn.finedev.java.domain.TicketDelivery;
import sn.finedev.java.domain.TicketDeliveryMethod;
import sn.finedev.java.repository.TicketDeliveryRepository;
import sn.finedev.java.repository.search.TicketDeliverySearchRepository;
import sn.finedev.java.service.criteria.TicketDeliveryCriteria;
import sn.finedev.java.service.dto.TicketDeliveryDTO;
import sn.finedev.java.service.mapper.TicketDeliveryMapper;

/**
 * Integration tests for the {@link TicketDeliveryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketDeliveryResourceIT {

    private static final Instant DEFAULT_BOUGHT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BOUGHT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_BOUGHT_BY = "AAAAAAAAAA";
    private static final String UPDATED_BOUGHT_BY = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final String ENTITY_API_URL = "/api/ticket-deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/ticket-deliveries";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TicketDeliveryRepository ticketDeliveryRepository;

    @Autowired
    private TicketDeliveryMapper ticketDeliveryMapper;

    @Autowired
    private TicketDeliverySearchRepository ticketDeliverySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketDeliveryMockMvc;

    private TicketDelivery ticketDelivery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketDelivery createEntity(EntityManager em) {
        TicketDelivery ticketDelivery = new TicketDelivery()
            .boughtAt(DEFAULT_BOUGHT_AT)
            .boughtBy(DEFAULT_BOUGHT_BY)
            .quantity(DEFAULT_QUANTITY);
        return ticketDelivery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketDelivery createUpdatedEntity(EntityManager em) {
        TicketDelivery ticketDelivery = new TicketDelivery()
            .boughtAt(UPDATED_BOUGHT_AT)
            .boughtBy(UPDATED_BOUGHT_BY)
            .quantity(UPDATED_QUANTITY);
        return ticketDelivery;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        ticketDeliverySearchRepository.deleteAll();
        assertThat(ticketDeliverySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        ticketDelivery = createEntity(em);
    }

    @Test
    @Transactional
    void createTicketDelivery() throws Exception {
        int databaseSizeBeforeCreate = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        // Create the TicketDelivery
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);
        restTicketDeliveryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TicketDelivery testTicketDelivery = ticketDeliveryList.get(ticketDeliveryList.size() - 1);
        assertThat(testTicketDelivery.getBoughtAt()).isEqualTo(DEFAULT_BOUGHT_AT);
        assertThat(testTicketDelivery.getBoughtBy()).isEqualTo(DEFAULT_BOUGHT_BY);
        assertThat(testTicketDelivery.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void createTicketDeliveryWithExistingId() throws Exception {
        // Create the TicketDelivery with an existing ID
        ticketDelivery.setId(1L);
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        int databaseSizeBeforeCreate = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketDeliveryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkBoughtAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        // set the field null
        ticketDelivery.setBoughtAt(null);

        // Create the TicketDelivery, which fails.
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        restTicketDeliveryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        // set the field null
        ticketDelivery.setQuantity(null);

        // Create the TicketDelivery, which fails.
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        restTicketDeliveryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTicketDeliveries() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList
        restTicketDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketDelivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].boughtAt").value(hasItem(DEFAULT_BOUGHT_AT.toString())))
            .andExpect(jsonPath("$.[*].boughtBy").value(hasItem(DEFAULT_BOUGHT_BY)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @Test
    @Transactional
    void getTicketDelivery() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get the ticketDelivery
        restTicketDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketDelivery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketDelivery.getId().intValue()))
            .andExpect(jsonPath("$.boughtAt").value(DEFAULT_BOUGHT_AT.toString()))
            .andExpect(jsonPath("$.boughtBy").value(DEFAULT_BOUGHT_BY))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getTicketDeliveriesByIdFiltering() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        Long id = ticketDelivery.getId();

        defaultTicketDeliveryShouldBeFound("id.equals=" + id);
        defaultTicketDeliveryShouldNotBeFound("id.notEquals=" + id);

        defaultTicketDeliveryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTicketDeliveryShouldNotBeFound("id.greaterThan=" + id);

        defaultTicketDeliveryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTicketDeliveryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByBoughtAtIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where boughtAt equals to DEFAULT_BOUGHT_AT
        defaultTicketDeliveryShouldBeFound("boughtAt.equals=" + DEFAULT_BOUGHT_AT);

        // Get all the ticketDeliveryList where boughtAt equals to UPDATED_BOUGHT_AT
        defaultTicketDeliveryShouldNotBeFound("boughtAt.equals=" + UPDATED_BOUGHT_AT);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByBoughtAtIsInShouldWork() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where boughtAt in DEFAULT_BOUGHT_AT or UPDATED_BOUGHT_AT
        defaultTicketDeliveryShouldBeFound("boughtAt.in=" + DEFAULT_BOUGHT_AT + "," + UPDATED_BOUGHT_AT);

        // Get all the ticketDeliveryList where boughtAt equals to UPDATED_BOUGHT_AT
        defaultTicketDeliveryShouldNotBeFound("boughtAt.in=" + UPDATED_BOUGHT_AT);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByBoughtAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where boughtAt is not null
        defaultTicketDeliveryShouldBeFound("boughtAt.specified=true");

        // Get all the ticketDeliveryList where boughtAt is null
        defaultTicketDeliveryShouldNotBeFound("boughtAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByBoughtByIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where boughtBy equals to DEFAULT_BOUGHT_BY
        defaultTicketDeliveryShouldBeFound("boughtBy.equals=" + DEFAULT_BOUGHT_BY);

        // Get all the ticketDeliveryList where boughtBy equals to UPDATED_BOUGHT_BY
        defaultTicketDeliveryShouldNotBeFound("boughtBy.equals=" + UPDATED_BOUGHT_BY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByBoughtByIsInShouldWork() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where boughtBy in DEFAULT_BOUGHT_BY or UPDATED_BOUGHT_BY
        defaultTicketDeliveryShouldBeFound("boughtBy.in=" + DEFAULT_BOUGHT_BY + "," + UPDATED_BOUGHT_BY);

        // Get all the ticketDeliveryList where boughtBy equals to UPDATED_BOUGHT_BY
        defaultTicketDeliveryShouldNotBeFound("boughtBy.in=" + UPDATED_BOUGHT_BY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByBoughtByIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where boughtBy is not null
        defaultTicketDeliveryShouldBeFound("boughtBy.specified=true");

        // Get all the ticketDeliveryList where boughtBy is null
        defaultTicketDeliveryShouldNotBeFound("boughtBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByBoughtByContainsSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where boughtBy contains DEFAULT_BOUGHT_BY
        defaultTicketDeliveryShouldBeFound("boughtBy.contains=" + DEFAULT_BOUGHT_BY);

        // Get all the ticketDeliveryList where boughtBy contains UPDATED_BOUGHT_BY
        defaultTicketDeliveryShouldNotBeFound("boughtBy.contains=" + UPDATED_BOUGHT_BY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByBoughtByNotContainsSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where boughtBy does not contain DEFAULT_BOUGHT_BY
        defaultTicketDeliveryShouldNotBeFound("boughtBy.doesNotContain=" + DEFAULT_BOUGHT_BY);

        // Get all the ticketDeliveryList where boughtBy does not contain UPDATED_BOUGHT_BY
        defaultTicketDeliveryShouldBeFound("boughtBy.doesNotContain=" + UPDATED_BOUGHT_BY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where quantity equals to DEFAULT_QUANTITY
        defaultTicketDeliveryShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the ticketDeliveryList where quantity equals to UPDATED_QUANTITY
        defaultTicketDeliveryShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultTicketDeliveryShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the ticketDeliveryList where quantity equals to UPDATED_QUANTITY
        defaultTicketDeliveryShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where quantity is not null
        defaultTicketDeliveryShouldBeFound("quantity.specified=true");

        // Get all the ticketDeliveryList where quantity is null
        defaultTicketDeliveryShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultTicketDeliveryShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the ticketDeliveryList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultTicketDeliveryShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultTicketDeliveryShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the ticketDeliveryList where quantity is less than or equal to SMALLER_QUANTITY
        defaultTicketDeliveryShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where quantity is less than DEFAULT_QUANTITY
        defaultTicketDeliveryShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the ticketDeliveryList where quantity is less than UPDATED_QUANTITY
        defaultTicketDeliveryShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        // Get all the ticketDeliveryList where quantity is greater than DEFAULT_QUANTITY
        defaultTicketDeliveryShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the ticketDeliveryList where quantity is greater than SMALLER_QUANTITY
        defaultTicketDeliveryShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllTicketDeliveriesByTicketDeliveryMethodIsEqualToSomething() throws Exception {
        TicketDeliveryMethod ticketDeliveryMethod;
        if (TestUtil.findAll(em, TicketDeliveryMethod.class).isEmpty()) {
            ticketDeliveryRepository.saveAndFlush(ticketDelivery);
            ticketDeliveryMethod = TicketDeliveryMethodResourceIT.createEntity(em);
        } else {
            ticketDeliveryMethod = TestUtil.findAll(em, TicketDeliveryMethod.class).get(0);
        }
        em.persist(ticketDeliveryMethod);
        em.flush();
        ticketDelivery.setTicketDeliveryMethod(ticketDeliveryMethod);
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);
        Long ticketDeliveryMethodId = ticketDeliveryMethod.getId();

        // Get all the ticketDeliveryList where ticketDeliveryMethod equals to ticketDeliveryMethodId
        defaultTicketDeliveryShouldBeFound("ticketDeliveryMethodId.equals=" + ticketDeliveryMethodId);

        // Get all the ticketDeliveryList where ticketDeliveryMethod equals to (ticketDeliveryMethodId + 1)
        defaultTicketDeliveryShouldNotBeFound("ticketDeliveryMethodId.equals=" + (ticketDeliveryMethodId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketDeliveryShouldBeFound(String filter) throws Exception {
        restTicketDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketDelivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].boughtAt").value(hasItem(DEFAULT_BOUGHT_AT.toString())))
            .andExpect(jsonPath("$.[*].boughtBy").value(hasItem(DEFAULT_BOUGHT_BY)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));

        // Check, that the count call also returns 1
        restTicketDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketDeliveryShouldNotBeFound(String filter) throws Exception {
        restTicketDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTicketDelivery() throws Exception {
        // Get the ticketDelivery
        restTicketDeliveryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketDelivery() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();
        ticketDeliverySearchRepository.save(ticketDelivery);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());

        // Update the ticketDelivery
        TicketDelivery updatedTicketDelivery = ticketDeliveryRepository.findById(ticketDelivery.getId()).get();
        // Disconnect from session so that the updates on updatedTicketDelivery are not directly saved in db
        em.detach(updatedTicketDelivery);
        updatedTicketDelivery.boughtAt(UPDATED_BOUGHT_AT).boughtBy(UPDATED_BOUGHT_BY).quantity(UPDATED_QUANTITY);
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(updatedTicketDelivery);

        restTicketDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketDeliveryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isOk());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        TicketDelivery testTicketDelivery = ticketDeliveryList.get(ticketDeliveryList.size() - 1);
        assertThat(testTicketDelivery.getBoughtAt()).isEqualTo(UPDATED_BOUGHT_AT);
        assertThat(testTicketDelivery.getBoughtBy()).isEqualTo(UPDATED_BOUGHT_BY);
        assertThat(testTicketDelivery.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TicketDelivery> ticketDeliverySearchList = IterableUtils.toList(ticketDeliverySearchRepository.findAll());
                TicketDelivery testTicketDeliverySearch = ticketDeliverySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTicketDeliverySearch.getBoughtAt()).isEqualTo(UPDATED_BOUGHT_AT);
                assertThat(testTicketDeliverySearch.getBoughtBy()).isEqualTo(UPDATED_BOUGHT_BY);
                assertThat(testTicketDeliverySearch.getQuantity()).isEqualTo(UPDATED_QUANTITY);
            });
    }

    @Test
    @Transactional
    void putNonExistingTicketDelivery() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        ticketDelivery.setId(count.incrementAndGet());

        // Create the TicketDelivery
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketDeliveryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketDelivery() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        ticketDelivery.setId(count.incrementAndGet());

        // Create the TicketDelivery
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketDelivery() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        ticketDelivery.setId(count.incrementAndGet());

        // Create the TicketDelivery
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketDeliveryWithPatch() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();

        // Update the ticketDelivery using partial update
        TicketDelivery partialUpdatedTicketDelivery = new TicketDelivery();
        partialUpdatedTicketDelivery.setId(ticketDelivery.getId());

        partialUpdatedTicketDelivery.boughtAt(UPDATED_BOUGHT_AT);

        restTicketDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTicketDelivery))
            )
            .andExpect(status().isOk());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        TicketDelivery testTicketDelivery = ticketDeliveryList.get(ticketDeliveryList.size() - 1);
        assertThat(testTicketDelivery.getBoughtAt()).isEqualTo(UPDATED_BOUGHT_AT);
        assertThat(testTicketDelivery.getBoughtBy()).isEqualTo(DEFAULT_BOUGHT_BY);
        assertThat(testTicketDelivery.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void fullUpdateTicketDeliveryWithPatch() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);

        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();

        // Update the ticketDelivery using partial update
        TicketDelivery partialUpdatedTicketDelivery = new TicketDelivery();
        partialUpdatedTicketDelivery.setId(ticketDelivery.getId());

        partialUpdatedTicketDelivery.boughtAt(UPDATED_BOUGHT_AT).boughtBy(UPDATED_BOUGHT_BY).quantity(UPDATED_QUANTITY);

        restTicketDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTicketDelivery))
            )
            .andExpect(status().isOk());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        TicketDelivery testTicketDelivery = ticketDeliveryList.get(ticketDeliveryList.size() - 1);
        assertThat(testTicketDelivery.getBoughtAt()).isEqualTo(UPDATED_BOUGHT_AT);
        assertThat(testTicketDelivery.getBoughtBy()).isEqualTo(UPDATED_BOUGHT_BY);
        assertThat(testTicketDelivery.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void patchNonExistingTicketDelivery() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        ticketDelivery.setId(count.incrementAndGet());

        // Create the TicketDelivery
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketDeliveryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketDelivery() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        ticketDelivery.setId(count.incrementAndGet());

        // Create the TicketDelivery
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketDelivery() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        ticketDelivery.setId(count.incrementAndGet());

        // Create the TicketDelivery
        TicketDeliveryDTO ticketDeliveryDTO = ticketDeliveryMapper.toDto(ticketDelivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketDelivery in the database
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTicketDelivery() throws Exception {
        // Initialize the database
        ticketDeliveryRepository.saveAndFlush(ticketDelivery);
        ticketDeliveryRepository.save(ticketDelivery);
        ticketDeliverySearchRepository.save(ticketDelivery);

        int databaseSizeBeforeDelete = ticketDeliveryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ticketDelivery
        restTicketDeliveryMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketDelivery.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TicketDelivery> ticketDeliveryList = ticketDeliveryRepository.findAll();
        assertThat(ticketDeliveryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTicketDelivery() throws Exception {
        // Initialize the database
        ticketDelivery = ticketDeliveryRepository.saveAndFlush(ticketDelivery);
        ticketDeliverySearchRepository.save(ticketDelivery);

        // Search the ticketDelivery
        restTicketDeliveryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ticketDelivery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketDelivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].boughtAt").value(hasItem(DEFAULT_BOUGHT_AT.toString())))
            .andExpect(jsonPath("$.[*].boughtBy").value(hasItem(DEFAULT_BOUGHT_BY)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }
}
