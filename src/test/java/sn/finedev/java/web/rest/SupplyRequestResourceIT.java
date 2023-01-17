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
import sn.finedev.java.IntegrationTest;
import sn.finedev.java.domain.Functionality;
import sn.finedev.java.domain.SupplyRequest;
import sn.finedev.java.domain.enumeration.SupplyRequestStatus;
import sn.finedev.java.repository.SupplyRequestRepository;
import sn.finedev.java.repository.search.SupplyRequestSearchRepository;
import sn.finedev.java.service.criteria.SupplyRequestCriteria;
import sn.finedev.java.service.dto.SupplyRequestDTO;
import sn.finedev.java.service.mapper.SupplyRequestMapper;

/**
 * Integration tests for the {@link SupplyRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SupplyRequestResourceIT {

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;
    private static final Double SMALLER_AMOUNT = 1D - 1D;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final SupplyRequestStatus DEFAULT_STATUS = SupplyRequestStatus.PENDING;
    private static final SupplyRequestStatus UPDATED_STATUS = SupplyRequestStatus.REJECTED;

    private static final String ENTITY_API_URL = "/api/supply-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/supply-requests";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SupplyRequestRepository supplyRequestRepository;

    @Autowired
    private SupplyRequestMapper supplyRequestMapper;

    @Autowired
    private SupplyRequestSearchRepository supplyRequestSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupplyRequestMockMvc;

    private SupplyRequest supplyRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupplyRequest createEntity(EntityManager em) {
        SupplyRequest supplyRequest = new SupplyRequest().amount(DEFAULT_AMOUNT).quantity(DEFAULT_QUANTITY).status(DEFAULT_STATUS);
        return supplyRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupplyRequest createUpdatedEntity(EntityManager em) {
        SupplyRequest supplyRequest = new SupplyRequest().amount(UPDATED_AMOUNT).quantity(UPDATED_QUANTITY).status(UPDATED_STATUS);
        return supplyRequest;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        supplyRequestSearchRepository.deleteAll();
        assertThat(supplyRequestSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        supplyRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createSupplyRequest() throws Exception {
        int databaseSizeBeforeCreate = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        // Create the SupplyRequest
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);
        restSupplyRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SupplyRequest testSupplyRequest = supplyRequestList.get(supplyRequestList.size() - 1);
        assertThat(testSupplyRequest.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testSupplyRequest.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testSupplyRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createSupplyRequestWithExistingId() throws Exception {
        // Create the SupplyRequest with an existing ID
        supplyRequest.setId(1L);
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);

        int databaseSizeBeforeCreate = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupplyRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        // set the field null
        supplyRequest.setStatus(null);

        // Create the SupplyRequest, which fails.
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);

        restSupplyRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isBadRequest());

        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSupplyRequests() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList
        restSupplyRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplyRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getSupplyRequest() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get the supplyRequest
        restSupplyRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, supplyRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supplyRequest.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getSupplyRequestsByIdFiltering() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        Long id = supplyRequest.getId();

        defaultSupplyRequestShouldBeFound("id.equals=" + id);
        defaultSupplyRequestShouldNotBeFound("id.notEquals=" + id);

        defaultSupplyRequestShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSupplyRequestShouldNotBeFound("id.greaterThan=" + id);

        defaultSupplyRequestShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSupplyRequestShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where amount equals to DEFAULT_AMOUNT
        defaultSupplyRequestShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the supplyRequestList where amount equals to UPDATED_AMOUNT
        defaultSupplyRequestShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultSupplyRequestShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the supplyRequestList where amount equals to UPDATED_AMOUNT
        defaultSupplyRequestShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where amount is not null
        defaultSupplyRequestShouldBeFound("amount.specified=true");

        // Get all the supplyRequestList where amount is null
        defaultSupplyRequestShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultSupplyRequestShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the supplyRequestList where amount is greater than or equal to UPDATED_AMOUNT
        defaultSupplyRequestShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where amount is less than or equal to DEFAULT_AMOUNT
        defaultSupplyRequestShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the supplyRequestList where amount is less than or equal to SMALLER_AMOUNT
        defaultSupplyRequestShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where amount is less than DEFAULT_AMOUNT
        defaultSupplyRequestShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the supplyRequestList where amount is less than UPDATED_AMOUNT
        defaultSupplyRequestShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where amount is greater than DEFAULT_AMOUNT
        defaultSupplyRequestShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the supplyRequestList where amount is greater than SMALLER_AMOUNT
        defaultSupplyRequestShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where quantity equals to DEFAULT_QUANTITY
        defaultSupplyRequestShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the supplyRequestList where quantity equals to UPDATED_QUANTITY
        defaultSupplyRequestShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultSupplyRequestShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the supplyRequestList where quantity equals to UPDATED_QUANTITY
        defaultSupplyRequestShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where quantity is not null
        defaultSupplyRequestShouldBeFound("quantity.specified=true");

        // Get all the supplyRequestList where quantity is null
        defaultSupplyRequestShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultSupplyRequestShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the supplyRequestList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultSupplyRequestShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultSupplyRequestShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the supplyRequestList where quantity is less than or equal to SMALLER_QUANTITY
        defaultSupplyRequestShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where quantity is less than DEFAULT_QUANTITY
        defaultSupplyRequestShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the supplyRequestList where quantity is less than UPDATED_QUANTITY
        defaultSupplyRequestShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where quantity is greater than DEFAULT_QUANTITY
        defaultSupplyRequestShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the supplyRequestList where quantity is greater than SMALLER_QUANTITY
        defaultSupplyRequestShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where status equals to DEFAULT_STATUS
        defaultSupplyRequestShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the supplyRequestList where status equals to UPDATED_STATUS
        defaultSupplyRequestShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultSupplyRequestShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the supplyRequestList where status equals to UPDATED_STATUS
        defaultSupplyRequestShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        // Get all the supplyRequestList where status is not null
        defaultSupplyRequestShouldBeFound("status.specified=true");

        // Get all the supplyRequestList where status is null
        defaultSupplyRequestShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllSupplyRequestsByFunctionalityIsEqualToSomething() throws Exception {
        Functionality functionality;
        if (TestUtil.findAll(em, Functionality.class).isEmpty()) {
            supplyRequestRepository.saveAndFlush(supplyRequest);
            functionality = FunctionalityResourceIT.createEntity(em);
        } else {
            functionality = TestUtil.findAll(em, Functionality.class).get(0);
        }
        em.persist(functionality);
        em.flush();
        supplyRequest.setFunctionality(functionality);
        supplyRequestRepository.saveAndFlush(supplyRequest);
        Long functionalityId = functionality.getId();

        // Get all the supplyRequestList where functionality equals to functionalityId
        defaultSupplyRequestShouldBeFound("functionalityId.equals=" + functionalityId);

        // Get all the supplyRequestList where functionality equals to (functionalityId + 1)
        defaultSupplyRequestShouldNotBeFound("functionalityId.equals=" + (functionalityId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSupplyRequestShouldBeFound(String filter) throws Exception {
        restSupplyRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplyRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restSupplyRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSupplyRequestShouldNotBeFound(String filter) throws Exception {
        restSupplyRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSupplyRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSupplyRequest() throws Exception {
        // Get the supplyRequest
        restSupplyRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupplyRequest() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();
        supplyRequestSearchRepository.save(supplyRequest);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());

        // Update the supplyRequest
        SupplyRequest updatedSupplyRequest = supplyRequestRepository.findById(supplyRequest.getId()).get();
        // Disconnect from session so that the updates on updatedSupplyRequest are not directly saved in db
        em.detach(updatedSupplyRequest);
        updatedSupplyRequest.amount(UPDATED_AMOUNT).quantity(UPDATED_QUANTITY).status(UPDATED_STATUS);
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(updatedSupplyRequest);

        restSupplyRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplyRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        SupplyRequest testSupplyRequest = supplyRequestList.get(supplyRequestList.size() - 1);
        assertThat(testSupplyRequest.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testSupplyRequest.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testSupplyRequest.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SupplyRequest> supplyRequestSearchList = IterableUtils.toList(supplyRequestSearchRepository.findAll());
                SupplyRequest testSupplyRequestSearch = supplyRequestSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSupplyRequestSearch.getAmount()).isEqualTo(UPDATED_AMOUNT);
                assertThat(testSupplyRequestSearch.getQuantity()).isEqualTo(UPDATED_QUANTITY);
                assertThat(testSupplyRequestSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingSupplyRequest() throws Exception {
        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        supplyRequest.setId(count.incrementAndGet());

        // Create the SupplyRequest
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplyRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplyRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupplyRequest() throws Exception {
        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        supplyRequest.setId(count.incrementAndGet());

        // Create the SupplyRequest
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupplyRequest() throws Exception {
        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        supplyRequest.setId(count.incrementAndGet());

        // Create the SupplyRequest
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyRequestMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSupplyRequestWithPatch() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();

        // Update the supplyRequest using partial update
        SupplyRequest partialUpdatedSupplyRequest = new SupplyRequest();
        partialUpdatedSupplyRequest.setId(supplyRequest.getId());

        restSupplyRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplyRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSupplyRequest))
            )
            .andExpect(status().isOk());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        SupplyRequest testSupplyRequest = supplyRequestList.get(supplyRequestList.size() - 1);
        assertThat(testSupplyRequest.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testSupplyRequest.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testSupplyRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateSupplyRequestWithPatch() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);

        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();

        // Update the supplyRequest using partial update
        SupplyRequest partialUpdatedSupplyRequest = new SupplyRequest();
        partialUpdatedSupplyRequest.setId(supplyRequest.getId());

        partialUpdatedSupplyRequest.amount(UPDATED_AMOUNT).quantity(UPDATED_QUANTITY).status(UPDATED_STATUS);

        restSupplyRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplyRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSupplyRequest))
            )
            .andExpect(status().isOk());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        SupplyRequest testSupplyRequest = supplyRequestList.get(supplyRequestList.size() - 1);
        assertThat(testSupplyRequest.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testSupplyRequest.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testSupplyRequest.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingSupplyRequest() throws Exception {
        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        supplyRequest.setId(count.incrementAndGet());

        // Create the SupplyRequest
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplyRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supplyRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupplyRequest() throws Exception {
        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        supplyRequest.setId(count.incrementAndGet());

        // Create the SupplyRequest
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupplyRequest() throws Exception {
        int databaseSizeBeforeUpdate = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        supplyRequest.setId(count.incrementAndGet());

        // Create the SupplyRequest
        SupplyRequestDTO supplyRequestDTO = supplyRequestMapper.toDto(supplyRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyRequestMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(supplyRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SupplyRequest in the database
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSupplyRequest() throws Exception {
        // Initialize the database
        supplyRequestRepository.saveAndFlush(supplyRequest);
        supplyRequestRepository.save(supplyRequest);
        supplyRequestSearchRepository.save(supplyRequest);

        int databaseSizeBeforeDelete = supplyRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the supplyRequest
        restSupplyRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, supplyRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SupplyRequest> supplyRequestList = supplyRequestRepository.findAll();
        assertThat(supplyRequestList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplyRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSupplyRequest() throws Exception {
        // Initialize the database
        supplyRequest = supplyRequestRepository.saveAndFlush(supplyRequest);
        supplyRequestSearchRepository.save(supplyRequest);

        // Search the supplyRequest
        restSupplyRequestMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + supplyRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplyRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
