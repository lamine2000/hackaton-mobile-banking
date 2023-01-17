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
import sn.finedev.java.domain.InsuranceAndMicroCreditsActor;
import sn.finedev.java.domain.InsuranceAndMicroCreditsContribution;
import sn.finedev.java.domain.Payment;
import sn.finedev.java.repository.InsuranceAndMicroCreditsContributionRepository;
import sn.finedev.java.repository.search.InsuranceAndMicroCreditsContributionSearchRepository;
import sn.finedev.java.service.criteria.InsuranceAndMicroCreditsContributionCriteria;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsContributionDTO;
import sn.finedev.java.service.mapper.InsuranceAndMicroCreditsContributionMapper;

/**
 * Integration tests for the {@link InsuranceAndMicroCreditsContributionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InsuranceAndMicroCreditsContributionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/insurance-and-micro-credits-contributions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/insurance-and-micro-credits-contributions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InsuranceAndMicroCreditsContributionRepository insuranceAndMicroCreditsContributionRepository;

    @Autowired
    private InsuranceAndMicroCreditsContributionMapper insuranceAndMicroCreditsContributionMapper;

    @Autowired
    private InsuranceAndMicroCreditsContributionSearchRepository insuranceAndMicroCreditsContributionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInsuranceAndMicroCreditsContributionMockMvc;

    private InsuranceAndMicroCreditsContribution insuranceAndMicroCreditsContribution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InsuranceAndMicroCreditsContribution createEntity(EntityManager em) {
        InsuranceAndMicroCreditsContribution insuranceAndMicroCreditsContribution = new InsuranceAndMicroCreditsContribution()
            .code(DEFAULT_CODE);
        return insuranceAndMicroCreditsContribution;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InsuranceAndMicroCreditsContribution createUpdatedEntity(EntityManager em) {
        InsuranceAndMicroCreditsContribution insuranceAndMicroCreditsContribution = new InsuranceAndMicroCreditsContribution()
            .code(UPDATED_CODE);
        return insuranceAndMicroCreditsContribution;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        insuranceAndMicroCreditsContributionSearchRepository.deleteAll();
        assertThat(insuranceAndMicroCreditsContributionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        insuranceAndMicroCreditsContribution = createEntity(em);
    }

    @Test
    @Transactional
    void createInsuranceAndMicroCreditsContribution() throws Exception {
        int databaseSizeBeforeCreate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        // Create the InsuranceAndMicroCreditsContribution
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        InsuranceAndMicroCreditsContribution testInsuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionList.get(
            insuranceAndMicroCreditsContributionList.size() - 1
        );
        assertThat(testInsuranceAndMicroCreditsContribution.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    void createInsuranceAndMicroCreditsContributionWithExistingId() throws Exception {
        // Create the InsuranceAndMicroCreditsContribution with an existing ID
        insuranceAndMicroCreditsContribution.setId(1L);
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );

        int databaseSizeBeforeCreate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        // set the field null
        insuranceAndMicroCreditsContribution.setCode(null);

        // Create the InsuranceAndMicroCreditsContribution, which fails.
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );

        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isBadRequest());

        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsContributions() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        // Get all the insuranceAndMicroCreditsContributionList
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insuranceAndMicroCreditsContribution.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getInsuranceAndMicroCreditsContribution() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        // Get the insuranceAndMicroCreditsContribution
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(get(ENTITY_API_URL_ID, insuranceAndMicroCreditsContribution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(insuranceAndMicroCreditsContribution.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getInsuranceAndMicroCreditsContributionsByIdFiltering() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        Long id = insuranceAndMicroCreditsContribution.getId();

        defaultInsuranceAndMicroCreditsContributionShouldBeFound("id.equals=" + id);
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("id.notEquals=" + id);

        defaultInsuranceAndMicroCreditsContributionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("id.greaterThan=" + id);

        defaultInsuranceAndMicroCreditsContributionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsContributionsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        // Get all the insuranceAndMicroCreditsContributionList where code equals to DEFAULT_CODE
        defaultInsuranceAndMicroCreditsContributionShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the insuranceAndMicroCreditsContributionList where code equals to UPDATED_CODE
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsContributionsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        // Get all the insuranceAndMicroCreditsContributionList where code in DEFAULT_CODE or UPDATED_CODE
        defaultInsuranceAndMicroCreditsContributionShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the insuranceAndMicroCreditsContributionList where code equals to UPDATED_CODE
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsContributionsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        // Get all the insuranceAndMicroCreditsContributionList where code is not null
        defaultInsuranceAndMicroCreditsContributionShouldBeFound("code.specified=true");

        // Get all the insuranceAndMicroCreditsContributionList where code is null
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsContributionsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        // Get all the insuranceAndMicroCreditsContributionList where code contains DEFAULT_CODE
        defaultInsuranceAndMicroCreditsContributionShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the insuranceAndMicroCreditsContributionList where code contains UPDATED_CODE
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsContributionsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        // Get all the insuranceAndMicroCreditsContributionList where code does not contain DEFAULT_CODE
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the insuranceAndMicroCreditsContributionList where code does not contain UPDATED_CODE
        defaultInsuranceAndMicroCreditsContributionShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsContributionsByInsuranceAndMicroCreditsActorIsEqualToSomething() throws Exception {
        InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor;
        if (TestUtil.findAll(em, InsuranceAndMicroCreditsActor.class).isEmpty()) {
            insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);
            insuranceAndMicroCreditsActor = InsuranceAndMicroCreditsActorResourceIT.createEntity(em);
        } else {
            insuranceAndMicroCreditsActor = TestUtil.findAll(em, InsuranceAndMicroCreditsActor.class).get(0);
        }
        em.persist(insuranceAndMicroCreditsActor);
        em.flush();
        insuranceAndMicroCreditsContribution.setInsuranceAndMicroCreditsActor(insuranceAndMicroCreditsActor);
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);
        Long insuranceAndMicroCreditsActorId = insuranceAndMicroCreditsActor.getId();

        // Get all the insuranceAndMicroCreditsContributionList where insuranceAndMicroCreditsActor equals to insuranceAndMicroCreditsActorId
        defaultInsuranceAndMicroCreditsContributionShouldBeFound(
            "insuranceAndMicroCreditsActorId.equals=" + insuranceAndMicroCreditsActorId
        );

        // Get all the insuranceAndMicroCreditsContributionList where insuranceAndMicroCreditsActor equals to (insuranceAndMicroCreditsActorId + 1)
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound(
            "insuranceAndMicroCreditsActorId.equals=" + (insuranceAndMicroCreditsActorId + 1)
        );
    }

    @Test
    @Transactional
    void getAllInsuranceAndMicroCreditsContributionsByPaymentIsEqualToSomething() throws Exception {
        Payment payment;
        if (TestUtil.findAll(em, Payment.class).isEmpty()) {
            insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);
            payment = PaymentResourceIT.createEntity(em);
        } else {
            payment = TestUtil.findAll(em, Payment.class).get(0);
        }
        em.persist(payment);
        em.flush();
        insuranceAndMicroCreditsContribution.setPayment(payment);
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);
        Long paymentId = payment.getId();

        // Get all the insuranceAndMicroCreditsContributionList where payment equals to paymentId
        defaultInsuranceAndMicroCreditsContributionShouldBeFound("paymentId.equals=" + paymentId);

        // Get all the insuranceAndMicroCreditsContributionList where payment equals to (paymentId + 1)
        defaultInsuranceAndMicroCreditsContributionShouldNotBeFound("paymentId.equals=" + (paymentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInsuranceAndMicroCreditsContributionShouldBeFound(String filter) throws Exception {
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insuranceAndMicroCreditsContribution.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));

        // Check, that the count call also returns 1
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInsuranceAndMicroCreditsContributionShouldNotBeFound(String filter) throws Exception {
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInsuranceAndMicroCreditsContribution() throws Exception {
        // Get the insuranceAndMicroCreditsContribution
        restInsuranceAndMicroCreditsContributionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInsuranceAndMicroCreditsContribution() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        insuranceAndMicroCreditsContributionSearchRepository.save(insuranceAndMicroCreditsContribution);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());

        // Update the insuranceAndMicroCreditsContribution
        InsuranceAndMicroCreditsContribution updatedInsuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionRepository
            .findById(insuranceAndMicroCreditsContribution.getId())
            .get();
        // Disconnect from session so that the updates on updatedInsuranceAndMicroCreditsContribution are not directly saved in db
        em.detach(updatedInsuranceAndMicroCreditsContribution);
        updatedInsuranceAndMicroCreditsContribution.code(UPDATED_CODE);
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            updatedInsuranceAndMicroCreditsContribution
        );

        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, insuranceAndMicroCreditsContributionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isOk());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        InsuranceAndMicroCreditsContribution testInsuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionList.get(
            insuranceAndMicroCreditsContributionList.size() - 1
        );
        assertThat(testInsuranceAndMicroCreditsContribution.getCode()).isEqualTo(UPDATED_CODE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionSearchList = IterableUtils.toList(
                    insuranceAndMicroCreditsContributionSearchRepository.findAll()
                );
                InsuranceAndMicroCreditsContribution testInsuranceAndMicroCreditsContributionSearch = insuranceAndMicroCreditsContributionSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testInsuranceAndMicroCreditsContributionSearch.getCode()).isEqualTo(UPDATED_CODE);
            });
    }

    @Test
    @Transactional
    void putNonExistingInsuranceAndMicroCreditsContribution() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        insuranceAndMicroCreditsContribution.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsContribution
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, insuranceAndMicroCreditsContributionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInsuranceAndMicroCreditsContribution() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        insuranceAndMicroCreditsContribution.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsContribution
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInsuranceAndMicroCreditsContribution() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        insuranceAndMicroCreditsContribution.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsContribution
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInsuranceAndMicroCreditsContributionWithPatch() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();

        // Update the insuranceAndMicroCreditsContribution using partial update
        InsuranceAndMicroCreditsContribution partialUpdatedInsuranceAndMicroCreditsContribution = new InsuranceAndMicroCreditsContribution();
        partialUpdatedInsuranceAndMicroCreditsContribution.setId(insuranceAndMicroCreditsContribution.getId());

        partialUpdatedInsuranceAndMicroCreditsContribution.code(UPDATED_CODE);

        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInsuranceAndMicroCreditsContribution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInsuranceAndMicroCreditsContribution))
            )
            .andExpect(status().isOk());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        InsuranceAndMicroCreditsContribution testInsuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionList.get(
            insuranceAndMicroCreditsContributionList.size() - 1
        );
        assertThat(testInsuranceAndMicroCreditsContribution.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void fullUpdateInsuranceAndMicroCreditsContributionWithPatch() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);

        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();

        // Update the insuranceAndMicroCreditsContribution using partial update
        InsuranceAndMicroCreditsContribution partialUpdatedInsuranceAndMicroCreditsContribution = new InsuranceAndMicroCreditsContribution();
        partialUpdatedInsuranceAndMicroCreditsContribution.setId(insuranceAndMicroCreditsContribution.getId());

        partialUpdatedInsuranceAndMicroCreditsContribution.code(UPDATED_CODE);

        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInsuranceAndMicroCreditsContribution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInsuranceAndMicroCreditsContribution))
            )
            .andExpect(status().isOk());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        InsuranceAndMicroCreditsContribution testInsuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionList.get(
            insuranceAndMicroCreditsContributionList.size() - 1
        );
        assertThat(testInsuranceAndMicroCreditsContribution.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingInsuranceAndMicroCreditsContribution() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        insuranceAndMicroCreditsContribution.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsContribution
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, insuranceAndMicroCreditsContributionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInsuranceAndMicroCreditsContribution() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        insuranceAndMicroCreditsContribution.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsContribution
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInsuranceAndMicroCreditsContribution() throws Exception {
        int databaseSizeBeforeUpdate = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        insuranceAndMicroCreditsContribution.setId(count.incrementAndGet());

        // Create the InsuranceAndMicroCreditsContribution
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(insuranceAndMicroCreditsContributionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InsuranceAndMicroCreditsContribution in the database
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInsuranceAndMicroCreditsContribution() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);
        insuranceAndMicroCreditsContributionRepository.save(insuranceAndMicroCreditsContribution);
        insuranceAndMicroCreditsContributionSearchRepository.save(insuranceAndMicroCreditsContribution);

        int databaseSizeBeforeDelete = insuranceAndMicroCreditsContributionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the insuranceAndMicroCreditsContribution
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(delete(ENTITY_API_URL_ID, insuranceAndMicroCreditsContribution.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InsuranceAndMicroCreditsContribution> insuranceAndMicroCreditsContributionList = insuranceAndMicroCreditsContributionRepository.findAll();
        assertThat(insuranceAndMicroCreditsContributionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(insuranceAndMicroCreditsContributionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInsuranceAndMicroCreditsContribution() throws Exception {
        // Initialize the database
        insuranceAndMicroCreditsContribution =
            insuranceAndMicroCreditsContributionRepository.saveAndFlush(insuranceAndMicroCreditsContribution);
        insuranceAndMicroCreditsContributionSearchRepository.save(insuranceAndMicroCreditsContribution);

        // Search the insuranceAndMicroCreditsContribution
        restInsuranceAndMicroCreditsContributionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + insuranceAndMicroCreditsContribution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insuranceAndMicroCreditsContribution.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }
}
