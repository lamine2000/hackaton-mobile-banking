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
import sn.finedev.java.domain.TicketDeliveryMethod;
import sn.finedev.java.repository.TicketDeliveryMethodRepository;
import sn.finedev.java.repository.search.TicketDeliveryMethodSearchRepository;
import sn.finedev.java.service.criteria.TicketDeliveryMethodCriteria;
import sn.finedev.java.service.dto.TicketDeliveryMethodDTO;
import sn.finedev.java.service.mapper.TicketDeliveryMethodMapper;

/**
 * Integration tests for the {@link TicketDeliveryMethodResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TicketDeliveryMethodResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ticket-delivery-methods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/ticket-delivery-methods";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TicketDeliveryMethodRepository ticketDeliveryMethodRepository;

    @Autowired
    private TicketDeliveryMethodMapper ticketDeliveryMethodMapper;

    @Autowired
    private TicketDeliveryMethodSearchRepository ticketDeliveryMethodSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketDeliveryMethodMockMvc;

    private TicketDeliveryMethod ticketDeliveryMethod;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketDeliveryMethod createEntity(EntityManager em) {
        TicketDeliveryMethod ticketDeliveryMethod = new TicketDeliveryMethod().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return ticketDeliveryMethod;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketDeliveryMethod createUpdatedEntity(EntityManager em) {
        TicketDeliveryMethod ticketDeliveryMethod = new TicketDeliveryMethod().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return ticketDeliveryMethod;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        ticketDeliveryMethodSearchRepository.deleteAll();
        assertThat(ticketDeliveryMethodSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        ticketDeliveryMethod = createEntity(em);
    }

    @Test
    @Transactional
    void createTicketDeliveryMethod() throws Exception {
        int databaseSizeBeforeCreate = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        // Create the TicketDeliveryMethod
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);
        restTicketDeliveryMethodMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TicketDeliveryMethod testTicketDeliveryMethod = ticketDeliveryMethodList.get(ticketDeliveryMethodList.size() - 1);
        assertThat(testTicketDeliveryMethod.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTicketDeliveryMethod.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTicketDeliveryMethodWithExistingId() throws Exception {
        // Create the TicketDeliveryMethod with an existing ID
        ticketDeliveryMethod.setId(1L);
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);

        int databaseSizeBeforeCreate = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketDeliveryMethodMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        // set the field null
        ticketDeliveryMethod.setName(null);

        // Create the TicketDeliveryMethod, which fails.
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);

        restTicketDeliveryMethodMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isBadRequest());

        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTicketDeliveryMethods() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        // Get all the ticketDeliveryMethodList
        restTicketDeliveryMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketDeliveryMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getTicketDeliveryMethod() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        // Get the ticketDeliveryMethod
        restTicketDeliveryMethodMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketDeliveryMethod.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketDeliveryMethod.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getTicketDeliveryMethodsByIdFiltering() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        Long id = ticketDeliveryMethod.getId();

        defaultTicketDeliveryMethodShouldBeFound("id.equals=" + id);
        defaultTicketDeliveryMethodShouldNotBeFound("id.notEquals=" + id);

        defaultTicketDeliveryMethodShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTicketDeliveryMethodShouldNotBeFound("id.greaterThan=" + id);

        defaultTicketDeliveryMethodShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTicketDeliveryMethodShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTicketDeliveryMethodsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        // Get all the ticketDeliveryMethodList where name equals to DEFAULT_NAME
        defaultTicketDeliveryMethodShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the ticketDeliveryMethodList where name equals to UPDATED_NAME
        defaultTicketDeliveryMethodShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTicketDeliveryMethodsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        // Get all the ticketDeliveryMethodList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTicketDeliveryMethodShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the ticketDeliveryMethodList where name equals to UPDATED_NAME
        defaultTicketDeliveryMethodShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTicketDeliveryMethodsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        // Get all the ticketDeliveryMethodList where name is not null
        defaultTicketDeliveryMethodShouldBeFound("name.specified=true");

        // Get all the ticketDeliveryMethodList where name is null
        defaultTicketDeliveryMethodShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketDeliveryMethodsByNameContainsSomething() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        // Get all the ticketDeliveryMethodList where name contains DEFAULT_NAME
        defaultTicketDeliveryMethodShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the ticketDeliveryMethodList where name contains UPDATED_NAME
        defaultTicketDeliveryMethodShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTicketDeliveryMethodsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        // Get all the ticketDeliveryMethodList where name does not contain DEFAULT_NAME
        defaultTicketDeliveryMethodShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the ticketDeliveryMethodList where name does not contain UPDATED_NAME
        defaultTicketDeliveryMethodShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketDeliveryMethodShouldBeFound(String filter) throws Exception {
        restTicketDeliveryMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketDeliveryMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));

        // Check, that the count call also returns 1
        restTicketDeliveryMethodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketDeliveryMethodShouldNotBeFound(String filter) throws Exception {
        restTicketDeliveryMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketDeliveryMethodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTicketDeliveryMethod() throws Exception {
        // Get the ticketDeliveryMethod
        restTicketDeliveryMethodMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketDeliveryMethod() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();
        ticketDeliveryMethodSearchRepository.save(ticketDeliveryMethod);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());

        // Update the ticketDeliveryMethod
        TicketDeliveryMethod updatedTicketDeliveryMethod = ticketDeliveryMethodRepository.findById(ticketDeliveryMethod.getId()).get();
        // Disconnect from session so that the updates on updatedTicketDeliveryMethod are not directly saved in db
        em.detach(updatedTicketDeliveryMethod);
        updatedTicketDeliveryMethod.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(updatedTicketDeliveryMethod);

        restTicketDeliveryMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketDeliveryMethodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isOk());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        TicketDeliveryMethod testTicketDeliveryMethod = ticketDeliveryMethodList.get(ticketDeliveryMethodList.size() - 1);
        assertThat(testTicketDeliveryMethod.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTicketDeliveryMethod.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TicketDeliveryMethod> ticketDeliveryMethodSearchList = IterableUtils.toList(
                    ticketDeliveryMethodSearchRepository.findAll()
                );
                TicketDeliveryMethod testTicketDeliveryMethodSearch = ticketDeliveryMethodSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTicketDeliveryMethodSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testTicketDeliveryMethodSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingTicketDeliveryMethod() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        ticketDeliveryMethod.setId(count.incrementAndGet());

        // Create the TicketDeliveryMethod
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketDeliveryMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketDeliveryMethodDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketDeliveryMethod() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        ticketDeliveryMethod.setId(count.incrementAndGet());

        // Create the TicketDeliveryMethod
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketDeliveryMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketDeliveryMethod() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        ticketDeliveryMethod.setId(count.incrementAndGet());

        // Create the TicketDeliveryMethod
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketDeliveryMethodMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTicketDeliveryMethodWithPatch() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();

        // Update the ticketDeliveryMethod using partial update
        TicketDeliveryMethod partialUpdatedTicketDeliveryMethod = new TicketDeliveryMethod();
        partialUpdatedTicketDeliveryMethod.setId(ticketDeliveryMethod.getId());

        partialUpdatedTicketDeliveryMethod.name(UPDATED_NAME);

        restTicketDeliveryMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketDeliveryMethod.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTicketDeliveryMethod))
            )
            .andExpect(status().isOk());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        TicketDeliveryMethod testTicketDeliveryMethod = ticketDeliveryMethodList.get(ticketDeliveryMethodList.size() - 1);
        assertThat(testTicketDeliveryMethod.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTicketDeliveryMethod.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTicketDeliveryMethodWithPatch() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);

        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();

        // Update the ticketDeliveryMethod using partial update
        TicketDeliveryMethod partialUpdatedTicketDeliveryMethod = new TicketDeliveryMethod();
        partialUpdatedTicketDeliveryMethod.setId(ticketDeliveryMethod.getId());

        partialUpdatedTicketDeliveryMethod.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restTicketDeliveryMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketDeliveryMethod.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTicketDeliveryMethod))
            )
            .andExpect(status().isOk());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        TicketDeliveryMethod testTicketDeliveryMethod = ticketDeliveryMethodList.get(ticketDeliveryMethodList.size() - 1);
        assertThat(testTicketDeliveryMethod.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTicketDeliveryMethod.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTicketDeliveryMethod() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        ticketDeliveryMethod.setId(count.incrementAndGet());

        // Create the TicketDeliveryMethod
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketDeliveryMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketDeliveryMethodDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketDeliveryMethod() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        ticketDeliveryMethod.setId(count.incrementAndGet());

        // Create the TicketDeliveryMethod
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketDeliveryMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketDeliveryMethod() throws Exception {
        int databaseSizeBeforeUpdate = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        ticketDeliveryMethod.setId(count.incrementAndGet());

        // Create the TicketDeliveryMethod
        TicketDeliveryMethodDTO ticketDeliveryMethodDTO = ticketDeliveryMethodMapper.toDto(ticketDeliveryMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketDeliveryMethodMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ticketDeliveryMethodDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketDeliveryMethod in the database
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTicketDeliveryMethod() throws Exception {
        // Initialize the database
        ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);
        ticketDeliveryMethodRepository.save(ticketDeliveryMethod);
        ticketDeliveryMethodSearchRepository.save(ticketDeliveryMethod);

        int databaseSizeBeforeDelete = ticketDeliveryMethodRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ticketDeliveryMethod
        restTicketDeliveryMethodMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketDeliveryMethod.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TicketDeliveryMethod> ticketDeliveryMethodList = ticketDeliveryMethodRepository.findAll();
        assertThat(ticketDeliveryMethodList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ticketDeliveryMethodSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTicketDeliveryMethod() throws Exception {
        // Initialize the database
        ticketDeliveryMethod = ticketDeliveryMethodRepository.saveAndFlush(ticketDeliveryMethod);
        ticketDeliveryMethodSearchRepository.save(ticketDeliveryMethod);

        // Search the ticketDeliveryMethod
        restTicketDeliveryMethodMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + ticketDeliveryMethod.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketDeliveryMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
