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
import sn.finedev.java.domain.Town;
import sn.finedev.java.repository.TownRepository;
import sn.finedev.java.repository.search.TownSearchRepository;
import sn.finedev.java.service.criteria.TownCriteria;
import sn.finedev.java.service.dto.TownDTO;
import sn.finedev.java.service.mapper.TownMapper;

/**
 * Integration tests for the {@link TownResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TownResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/towns";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/towns";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private TownMapper townMapper;

    @Autowired
    private TownSearchRepository townSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTownMockMvc;

    private Town town;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Town createEntity(EntityManager em) {
        Town town = new Town().name(DEFAULT_NAME).code(DEFAULT_CODE).createdAt(DEFAULT_CREATED_AT).createdBy(DEFAULT_CREATED_BY);
        return town;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Town createUpdatedEntity(EntityManager em) {
        Town town = new Town().name(UPDATED_NAME).code(UPDATED_CODE).createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);
        return town;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        townSearchRepository.deleteAll();
        assertThat(townSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        town = createEntity(em);
    }

    @Test
    @Transactional
    void createTown() throws Exception {
        int databaseSizeBeforeCreate = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        // Create the Town
        TownDTO townDTO = townMapper.toDto(town);
        restTownMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(townDTO)))
            .andExpect(status().isCreated());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Town testTown = townList.get(townList.size() - 1);
        assertThat(testTown.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTown.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTown.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testTown.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
    void createTownWithExistingId() throws Exception {
        // Create the Town with an existing ID
        town.setId(1L);
        TownDTO townDTO = townMapper.toDto(town);

        int databaseSizeBeforeCreate = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTownMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(townDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        // set the field null
        town.setName(null);

        // Create the Town, which fails.
        TownDTO townDTO = townMapper.toDto(town);

        restTownMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(townDTO)))
            .andExpect(status().isBadRequest());

        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        // set the field null
        town.setCode(null);

        // Create the Town, which fails.
        TownDTO townDTO = townMapper.toDto(town);

        restTownMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(townDTO)))
            .andExpect(status().isBadRequest());

        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTowns() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList
        restTownMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(town.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }

    @Test
    @Transactional
    void getTown() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get the town
        restTownMockMvc
            .perform(get(ENTITY_API_URL_ID, town.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(town.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY));
    }

    @Test
    @Transactional
    void getTownsByIdFiltering() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        Long id = town.getId();

        defaultTownShouldBeFound("id.equals=" + id);
        defaultTownShouldNotBeFound("id.notEquals=" + id);

        defaultTownShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTownShouldNotBeFound("id.greaterThan=" + id);

        defaultTownShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTownShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTownsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where name equals to DEFAULT_NAME
        defaultTownShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the townList where name equals to UPDATED_NAME
        defaultTownShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTownsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTownShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the townList where name equals to UPDATED_NAME
        defaultTownShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTownsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where name is not null
        defaultTownShouldBeFound("name.specified=true");

        // Get all the townList where name is null
        defaultTownShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTownsByNameContainsSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where name contains DEFAULT_NAME
        defaultTownShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the townList where name contains UPDATED_NAME
        defaultTownShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTownsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where name does not contain DEFAULT_NAME
        defaultTownShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the townList where name does not contain UPDATED_NAME
        defaultTownShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTownsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where code equals to DEFAULT_CODE
        defaultTownShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the townList where code equals to UPDATED_CODE
        defaultTownShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTownsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where code in DEFAULT_CODE or UPDATED_CODE
        defaultTownShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the townList where code equals to UPDATED_CODE
        defaultTownShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTownsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where code is not null
        defaultTownShouldBeFound("code.specified=true");

        // Get all the townList where code is null
        defaultTownShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllTownsByCodeContainsSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where code contains DEFAULT_CODE
        defaultTownShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the townList where code contains UPDATED_CODE
        defaultTownShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTownsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where code does not contain DEFAULT_CODE
        defaultTownShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the townList where code does not contain UPDATED_CODE
        defaultTownShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTownsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where createdAt equals to DEFAULT_CREATED_AT
        defaultTownShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the townList where createdAt equals to UPDATED_CREATED_AT
        defaultTownShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTownsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultTownShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the townList where createdAt equals to UPDATED_CREATED_AT
        defaultTownShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTownsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where createdAt is not null
        defaultTownShouldBeFound("createdAt.specified=true");

        // Get all the townList where createdAt is null
        defaultTownShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTownsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where createdBy equals to DEFAULT_CREATED_BY
        defaultTownShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the townList where createdBy equals to UPDATED_CREATED_BY
        defaultTownShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTownsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTownShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the townList where createdBy equals to UPDATED_CREATED_BY
        defaultTownShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTownsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where createdBy is not null
        defaultTownShouldBeFound("createdBy.specified=true");

        // Get all the townList where createdBy is null
        defaultTownShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTownsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where createdBy contains DEFAULT_CREATED_BY
        defaultTownShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the townList where createdBy contains UPDATED_CREATED_BY
        defaultTownShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTownsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        // Get all the townList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTownShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the townList where createdBy does not contain UPDATED_CREATED_BY
        defaultTownShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTownShouldBeFound(String filter) throws Exception {
        restTownMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(town.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));

        // Check, that the count call also returns 1
        restTownMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTownShouldNotBeFound(String filter) throws Exception {
        restTownMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTownMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTown() throws Exception {
        // Get the town
        restTownMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTown() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        int databaseSizeBeforeUpdate = townRepository.findAll().size();
        townSearchRepository.save(town);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());

        // Update the town
        Town updatedTown = townRepository.findById(town.getId()).get();
        // Disconnect from session so that the updates on updatedTown are not directly saved in db
        em.detach(updatedTown);
        updatedTown.name(UPDATED_NAME).code(UPDATED_CODE).createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);
        TownDTO townDTO = townMapper.toDto(updatedTown);

        restTownMockMvc
            .perform(
                put(ENTITY_API_URL_ID, townDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(townDTO))
            )
            .andExpect(status().isOk());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        Town testTown = townList.get(townList.size() - 1);
        assertThat(testTown.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTown.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTown.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTown.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Town> townSearchList = IterableUtils.toList(townSearchRepository.findAll());
                Town testTownSearch = townSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTownSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testTownSearch.getCode()).isEqualTo(UPDATED_CODE);
                assertThat(testTownSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testTownSearch.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
            });
    }

    @Test
    @Transactional
    void putNonExistingTown() throws Exception {
        int databaseSizeBeforeUpdate = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        town.setId(count.incrementAndGet());

        // Create the Town
        TownDTO townDTO = townMapper.toDto(town);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTownMockMvc
            .perform(
                put(ENTITY_API_URL_ID, townDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(townDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTown() throws Exception {
        int databaseSizeBeforeUpdate = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        town.setId(count.incrementAndGet());

        // Create the Town
        TownDTO townDTO = townMapper.toDto(town);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTownMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(townDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTown() throws Exception {
        int databaseSizeBeforeUpdate = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        town.setId(count.incrementAndGet());

        // Create the Town
        TownDTO townDTO = townMapper.toDto(town);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTownMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(townDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTownWithPatch() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        int databaseSizeBeforeUpdate = townRepository.findAll().size();

        // Update the town using partial update
        Town partialUpdatedTown = new Town();
        partialUpdatedTown.setId(town.getId());

        partialUpdatedTown.code(UPDATED_CODE);

        restTownMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTown.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTown))
            )
            .andExpect(status().isOk());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        Town testTown = townList.get(townList.size() - 1);
        assertThat(testTown.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTown.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTown.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testTown.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
    void fullUpdateTownWithPatch() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);

        int databaseSizeBeforeUpdate = townRepository.findAll().size();

        // Update the town using partial update
        Town partialUpdatedTown = new Town();
        partialUpdatedTown.setId(town.getId());

        partialUpdatedTown.name(UPDATED_NAME).code(UPDATED_CODE).createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);

        restTownMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTown.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTown))
            )
            .andExpect(status().isOk());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        Town testTown = townList.get(townList.size() - 1);
        assertThat(testTown.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTown.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTown.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTown.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingTown() throws Exception {
        int databaseSizeBeforeUpdate = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        town.setId(count.incrementAndGet());

        // Create the Town
        TownDTO townDTO = townMapper.toDto(town);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTownMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, townDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(townDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTown() throws Exception {
        int databaseSizeBeforeUpdate = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        town.setId(count.incrementAndGet());

        // Create the Town
        TownDTO townDTO = townMapper.toDto(town);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTownMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(townDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTown() throws Exception {
        int databaseSizeBeforeUpdate = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        town.setId(count.incrementAndGet());

        // Create the Town
        TownDTO townDTO = townMapper.toDto(town);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTownMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(townDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Town in the database
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTown() throws Exception {
        // Initialize the database
        townRepository.saveAndFlush(town);
        townRepository.save(town);
        townSearchRepository.save(town);

        int databaseSizeBeforeDelete = townRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the town
        restTownMockMvc
            .perform(delete(ENTITY_API_URL_ID, town.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Town> townList = townRepository.findAll();
        assertThat(townList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(townSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTown() throws Exception {
        // Initialize the database
        town = townRepository.saveAndFlush(town);
        townSearchRepository.save(town);

        // Search the town
        restTownMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + town.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(town.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }
}
