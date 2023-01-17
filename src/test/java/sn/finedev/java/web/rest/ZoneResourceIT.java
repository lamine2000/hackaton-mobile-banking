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
import sn.finedev.java.domain.Zone;
import sn.finedev.java.repository.ZoneRepository;
import sn.finedev.java.repository.search.ZoneSearchRepository;
import sn.finedev.java.service.criteria.ZoneCriteria;
import sn.finedev.java.service.dto.ZoneDTO;
import sn.finedev.java.service.mapper.ZoneMapper;

/**
 * Integration tests for the {@link ZoneResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ZoneResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/zones";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/zones";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private ZoneMapper zoneMapper;

    @Autowired
    private ZoneSearchRepository zoneSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restZoneMockMvc;

    private Zone zone;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Zone createEntity(EntityManager em) {
        Zone zone = new Zone().name(DEFAULT_NAME).code(DEFAULT_CODE).createdAt(DEFAULT_CREATED_AT).createdBy(DEFAULT_CREATED_BY);
        return zone;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Zone createUpdatedEntity(EntityManager em) {
        Zone zone = new Zone().name(UPDATED_NAME).code(UPDATED_CODE).createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);
        return zone;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        zoneSearchRepository.deleteAll();
        assertThat(zoneSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        zone = createEntity(em);
    }

    @Test
    @Transactional
    void createZone() throws Exception {
        int databaseSizeBeforeCreate = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        // Create the Zone
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);
        restZoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(zoneDTO)))
            .andExpect(status().isCreated());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Zone testZone = zoneList.get(zoneList.size() - 1);
        assertThat(testZone.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testZone.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testZone.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testZone.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
    void createZoneWithExistingId() throws Exception {
        // Create the Zone with an existing ID
        zone.setId(1L);
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        int databaseSizeBeforeCreate = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restZoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(zoneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        // set the field null
        zone.setName(null);

        // Create the Zone, which fails.
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        restZoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(zoneDTO)))
            .andExpect(status().isBadRequest());

        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        // set the field null
        zone.setCode(null);

        // Create the Zone, which fails.
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        restZoneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(zoneDTO)))
            .andExpect(status().isBadRequest());

        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllZones() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList
        restZoneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zone.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }

    @Test
    @Transactional
    void getZone() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get the zone
        restZoneMockMvc
            .perform(get(ENTITY_API_URL_ID, zone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(zone.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY));
    }

    @Test
    @Transactional
    void getZonesByIdFiltering() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        Long id = zone.getId();

        defaultZoneShouldBeFound("id.equals=" + id);
        defaultZoneShouldNotBeFound("id.notEquals=" + id);

        defaultZoneShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultZoneShouldNotBeFound("id.greaterThan=" + id);

        defaultZoneShouldBeFound("id.lessThanOrEqual=" + id);
        defaultZoneShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllZonesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where name equals to DEFAULT_NAME
        defaultZoneShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the zoneList where name equals to UPDATED_NAME
        defaultZoneShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllZonesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where name in DEFAULT_NAME or UPDATED_NAME
        defaultZoneShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the zoneList where name equals to UPDATED_NAME
        defaultZoneShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllZonesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where name is not null
        defaultZoneShouldBeFound("name.specified=true");

        // Get all the zoneList where name is null
        defaultZoneShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllZonesByNameContainsSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where name contains DEFAULT_NAME
        defaultZoneShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the zoneList where name contains UPDATED_NAME
        defaultZoneShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllZonesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where name does not contain DEFAULT_NAME
        defaultZoneShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the zoneList where name does not contain UPDATED_NAME
        defaultZoneShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllZonesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where code equals to DEFAULT_CODE
        defaultZoneShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the zoneList where code equals to UPDATED_CODE
        defaultZoneShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllZonesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where code in DEFAULT_CODE or UPDATED_CODE
        defaultZoneShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the zoneList where code equals to UPDATED_CODE
        defaultZoneShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllZonesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where code is not null
        defaultZoneShouldBeFound("code.specified=true");

        // Get all the zoneList where code is null
        defaultZoneShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllZonesByCodeContainsSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where code contains DEFAULT_CODE
        defaultZoneShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the zoneList where code contains UPDATED_CODE
        defaultZoneShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllZonesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where code does not contain DEFAULT_CODE
        defaultZoneShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the zoneList where code does not contain UPDATED_CODE
        defaultZoneShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllZonesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where createdAt equals to DEFAULT_CREATED_AT
        defaultZoneShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the zoneList where createdAt equals to UPDATED_CREATED_AT
        defaultZoneShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllZonesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultZoneShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the zoneList where createdAt equals to UPDATED_CREATED_AT
        defaultZoneShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllZonesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where createdAt is not null
        defaultZoneShouldBeFound("createdAt.specified=true");

        // Get all the zoneList where createdAt is null
        defaultZoneShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllZonesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where createdBy equals to DEFAULT_CREATED_BY
        defaultZoneShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the zoneList where createdBy equals to UPDATED_CREATED_BY
        defaultZoneShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllZonesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultZoneShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the zoneList where createdBy equals to UPDATED_CREATED_BY
        defaultZoneShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllZonesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where createdBy is not null
        defaultZoneShouldBeFound("createdBy.specified=true");

        // Get all the zoneList where createdBy is null
        defaultZoneShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllZonesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where createdBy contains DEFAULT_CREATED_BY
        defaultZoneShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the zoneList where createdBy contains UPDATED_CREATED_BY
        defaultZoneShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllZonesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        // Get all the zoneList where createdBy does not contain DEFAULT_CREATED_BY
        defaultZoneShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the zoneList where createdBy does not contain UPDATED_CREATED_BY
        defaultZoneShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultZoneShouldBeFound(String filter) throws Exception {
        restZoneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zone.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));

        // Check, that the count call also returns 1
        restZoneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultZoneShouldNotBeFound(String filter) throws Exception {
        restZoneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restZoneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingZone() throws Exception {
        // Get the zone
        restZoneMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingZone() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();
        zoneSearchRepository.save(zone);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());

        // Update the zone
        Zone updatedZone = zoneRepository.findById(zone.getId()).get();
        // Disconnect from session so that the updates on updatedZone are not directly saved in db
        em.detach(updatedZone);
        updatedZone.name(UPDATED_NAME).code(UPDATED_CODE).createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);
        ZoneDTO zoneDTO = zoneMapper.toDto(updatedZone);

        restZoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, zoneDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(zoneDTO))
            )
            .andExpect(status().isOk());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        Zone testZone = zoneList.get(zoneList.size() - 1);
        assertThat(testZone.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testZone.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testZone.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testZone.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Zone> zoneSearchList = IterableUtils.toList(zoneSearchRepository.findAll());
                Zone testZoneSearch = zoneSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testZoneSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testZoneSearch.getCode()).isEqualTo(UPDATED_CODE);
                assertThat(testZoneSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testZoneSearch.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
            });
    }

    @Test
    @Transactional
    void putNonExistingZone() throws Exception {
        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        zone.setId(count.incrementAndGet());

        // Create the Zone
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restZoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, zoneDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(zoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchZone() throws Exception {
        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        zone.setId(count.incrementAndGet());

        // Create the Zone
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZoneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(zoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamZone() throws Exception {
        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        zone.setId(count.incrementAndGet());

        // Create the Zone
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZoneMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(zoneDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateZoneWithPatch() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();

        // Update the zone using partial update
        Zone partialUpdatedZone = new Zone();
        partialUpdatedZone.setId(zone.getId());

        partialUpdatedZone.createdBy(UPDATED_CREATED_BY);

        restZoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedZone.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedZone))
            )
            .andExpect(status().isOk());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        Zone testZone = zoneList.get(zoneList.size() - 1);
        assertThat(testZone.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testZone.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testZone.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testZone.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void fullUpdateZoneWithPatch() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);

        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();

        // Update the zone using partial update
        Zone partialUpdatedZone = new Zone();
        partialUpdatedZone.setId(zone.getId());

        partialUpdatedZone.name(UPDATED_NAME).code(UPDATED_CODE).createdAt(UPDATED_CREATED_AT).createdBy(UPDATED_CREATED_BY);

        restZoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedZone.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedZone))
            )
            .andExpect(status().isOk());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        Zone testZone = zoneList.get(zoneList.size() - 1);
        assertThat(testZone.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testZone.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testZone.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testZone.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingZone() throws Exception {
        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        zone.setId(count.incrementAndGet());

        // Create the Zone
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restZoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, zoneDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(zoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchZone() throws Exception {
        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        zone.setId(count.incrementAndGet());

        // Create the Zone
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZoneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(zoneDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamZone() throws Exception {
        int databaseSizeBeforeUpdate = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        zone.setId(count.incrementAndGet());

        // Create the Zone
        ZoneDTO zoneDTO = zoneMapper.toDto(zone);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZoneMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(zoneDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Zone in the database
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteZone() throws Exception {
        // Initialize the database
        zoneRepository.saveAndFlush(zone);
        zoneRepository.save(zone);
        zoneSearchRepository.save(zone);

        int databaseSizeBeforeDelete = zoneRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the zone
        restZoneMockMvc
            .perform(delete(ENTITY_API_URL_ID, zone.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Zone> zoneList = zoneRepository.findAll();
        assertThat(zoneList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(zoneSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchZone() throws Exception {
        // Initialize the database
        zone = zoneRepository.saveAndFlush(zone);
        zoneSearchRepository.save(zone);

        // Search the zone
        restZoneMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + zone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zone.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }
}
